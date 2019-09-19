package com.example.galeria

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.support.v4.app.FragmentActivity




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [first.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [first.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class first : Fragment() {
    // TODO: Rename and change types of parameters
    var array = arrayListOf<myImage>()
    var recyclerView : RecyclerView? = null;
    var adapter : MyAdapter? = null;
    val REQUEST_IMAGE_CAPTURE = 100;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true);

        for (i in 1 .. 7) {
            var stringg = "pic"
            stringg += i
            array.add(myImage(stringg,array.size,null))
        }
        array.get(0).text = "Król lew"
        array.get(1).text = "'Some man just want to watch the world burn'"
        array.get(2).text = "Karate WKF - kumite"
        array.get(3).text = "Sztuka spadania - Bagiński"
        array.get(4).text = "Katedra Notre Dame - Beksiński"
        array.get(5).text = "Muhammed Ali ~ Float like a butterfly, sting like a bee"
        array.get(6).text = "Rocky Balboa"


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_first, container, false)
        // Inflate the layout for this fragment


        recyclerView = view.findViewById(R.id.imagegallery)
         recyclerView!!.setHasFixedSize(true)

         val layoutManager = GridLayoutManager( activity!!.applicationContext, 2)
         recyclerView!!.layoutManager = layoutManager as RecyclerView.LayoutManager?

         adapter = MyAdapter(activity!!, array)
         recyclerView!!.adapter = adapter

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       // super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            var i = data!!.extras!!.getInt("id")
            array.get(i).note = data!!.extras!!.getFloat("note")
            array.sortByDescending { it.note }
            adapter!!.notifyDataSetChanged();
          //  Toast.makeText(context,   " AAAAAAA", Toast.LENGTH_SHORT).show()
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            array.add(myImage("Dodane przez aparat",array.size,imageBitmap))
            array.sortByDescending { it.note }
            adapter!!.notifyDataSetChanged();
            Toast.makeText(context, "" + " Dodano zdjęcie ", Toast.LENGTH_SHORT).show()

        }
       // Toast.makeText(context, ""  + "222 AAAAAAA", Toast.LENGTH_SHORT).show()
    }


}

/*var array = arrayListOf<myImage>()
var recyclerView : RecyclerView? = null;
var adapter : MyAdapter? = null;
val REQUEST_IMAGE_CAPTURE = 100;

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
}

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_first, container, false)
} */