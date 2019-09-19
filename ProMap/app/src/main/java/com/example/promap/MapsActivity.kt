package com.example.promap

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.runBlocking
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.Polyline






class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, TaskLoadedCallback {
    private val db = FirebaseFirestore.getInstance()
    private val firestore = FirestoreHandler()

    private lateinit var mMap: GoogleMap
    var cityArray = arrayListOf<City>()
    var spinnerArray = arrayListOf<String>()
    lateinit var spinnerArrayAdapter: ArrayAdapter<String>
    private var currentPolyline: Polyline? = null
    private var place1: Marker? = null
    private var place2: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        spinnerArray.add("")

        spinnerArrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            spinnerArray
        ) //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )
        spinner.adapter = spinnerArrayAdapter

        spinnerSecond.adapter = spinnerArrayAdapter

        Thread {
            runBlocking {
                cityArray.clear()
                val docRef = firestore.getAllCities().addOnSuccessListener { result ->
                    cityArray.addAll(mapDocumentsToCities(result))
                }

                Thread.sleep(2000)
            }

            run {

                spinnerArray.clear()

                for (i in 0 .. cityArray.size-1)
                    spinnerArray.add(cityArray.get(i).name)
                spinnerArray.sort()

                spinnerArrayAdapter.setNotifyOnChange(true)
            }
        }.start()



    }



    fun findIt(view: View) {
        lateinit var choosenCity : City
        for (i in 0 .. cityArray.size-1){
            if(cityArray.get(i).name == spinner.selectedItem.toString()){
                choosenCity = cityArray.get(i)
                break
            }
        }
        val city = LatLng(choosenCity.X.toDouble(), choosenCity.Y.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city,17f))
    }

    fun add(view: View) {
        val myintent = Intent(this, addCity::class.java)
        startActivityForResult(myintent, 100)
    }

    fun connect(view: View) {
        lateinit var choosenCity : City
        lateinit var choosenCity2 : City
        for (i in 0 .. cityArray.size-1){
            if(cityArray.get(i).name == spinner.selectedItem.toString()){
                choosenCity = cityArray.get(i)
                break
            }
        }
        val city1 = LatLng(choosenCity.X.toDouble(), choosenCity.Y.toDouble())

        for (i in 0 .. cityArray.size-1){
            if(cityArray.get(i).name == spinnerSecond.selectedItem.toString()){
                choosenCity2 = cityArray.get(i)
                break
            }
        }

        if(choosenCity == choosenCity2){
            Toast.makeText(this, "Wybierz różne miasta!", Toast.LENGTH_SHORT).show()
        } else {
            if(place1!=null && place2!=null){
                place1!!.remove()
                place2!!.remove()
                if(currentPolyline != null)
                     currentPolyline!!.remove()
            }
            val city2 = LatLng(choosenCity2.X.toDouble(), choosenCity2.Y.toDouble())
            place1 = mMap.addMarker(MarkerOptions().position(city1).title("Marker w ${choosenCity.name}"))
            place2 = mMap.addMarker(MarkerOptions().position(city2).title("Marker w ${choosenCity2.name}"))

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city1,17f))

            FetchURL(this).execute(getUrl(place1!!.getPosition(), place2!!.getPosition(), "driving"), "driving");
        }
    }

    fun delete(view: View) {
        if(place1!=null && place2!=null){
            place1!!.remove()
            place2!!.remove()
            if(currentPolyline != null)
                 currentPolyline!!.remove()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
          //  Toast.makeText(this, "Granted!!", Toast.LENGTH_LONG).show()
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener(this)
            mMap.setOnMyLocationClickListener(this)
        }
        else {
          //  Toast.makeText(this, "No Granted!!", Toast.LENGTH_LONG).show()
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),123)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 123) {
            if (permissions.size == 1 &&
                permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                //Toast.makeText(this, "Granted!!", Toast.LENGTH_LONG).show()
                mMap.isMyLocationEnabled = true
                mMap.setOnMyLocationButtonClickListener(this)
                mMap.setOnMyLocationClickListener(this)
            } else {
                // Permission was denied. Display an error message.
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){

            var x = data!!.extras.getFloat("X", 0.0F)
            var y = data!!.extras.getFloat("Y", 0.0F)
            var name = data!!.extras.getString("name")

            cityArray.add(City(name,x,y))
            spinnerArray.add(name)
            spinnerArray.sort()

            spinnerArrayAdapter.setNotifyOnChange(true)

        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Aktualna lokacja:", Toast.LENGTH_LONG).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Aktualna lokacja:\n$p0", Toast.LENGTH_LONG).show()
    }

    private fun mapDocumentsToCities(querySnapshot: QuerySnapshot): List<City> {
        return querySnapshot.map { document ->
            City(document.data)
        }
    }

    override fun onTaskDone(vararg values: Any) {
        if (currentPolyline != null)
            currentPolyline!!.remove()
        currentPolyline = mMap.addPolyline(values[0] as PolylineOptions)
    }

    private fun getUrl(origin:LatLng, dest:LatLng, directionMode:String):String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Mode
        val mode = "mode=" + directionMode
        // Building the parameters to the web service
        val parameters = str_origin + "&" + str_dest + "&" + mode
        // Output format
        val output = "json"
        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key)
        return url
    }


   /* override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }*/


}
