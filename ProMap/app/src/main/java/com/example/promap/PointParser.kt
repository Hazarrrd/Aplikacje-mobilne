package com.example.promap

import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject


class PointsParser(mContext: MapsActivity, directionMode: String) :
    AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

    internal var taskCallback: TaskLoadedCallback
    internal var directionMode = "driving"

    init {
        this.taskCallback = mContext as (TaskLoadedCallback)
        this.directionMode = directionMode
    }

    // Parsing the data in non-ui thread
    override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {

        val jObject: JSONObject
        var routes: List<List<HashMap<String, String>>>? = null

        try {
            jObject = JSONObject(jsonData[0])
            Log.d("mylog", jsonData[0])
            val parser = DataParser()
            Log.d("mylog", parser.toString())

            // Starts parsing data
            routes = parser.parse(jObject)
            Log.d("mylog", "Executing routes")
            Log.d("mylog", routes!!.toString())

        } catch (e: Exception) {
            Log.d("mylog", e.toString())
            e.printStackTrace()
        }

        return routes
    }

    // Executes in UI thread, after the parsing process
    override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        Log.d("mylog", "JESTEM22")
        var points: ArrayList<LatLng>
        var lineOptions: PolylineOptions? = null
        // Traversing through all the routes
        for (i in result.indices) {
            points = ArrayList()
            lineOptions = PolylineOptions()
            // Fetching i-th route
            val path = result[i]
            // Fetching all the points in i-th route
            for (j in path.indices) {
                val point = path[j]
                val lat = java.lang.Double.parseDouble(point["lat"])
                val lng = java.lang.Double.parseDouble(point["lng"])
                val position = LatLng(lat, lng)
                points.add(position)
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points)
            if (directionMode.equals("walking", ignoreCase = true)) {
                lineOptions.width(10f)
                lineOptions.color(Color.MAGENTA)
            } else {
                lineOptions.width(20f)
                lineOptions.color(Color.RED)
            }
            Log.d("mylog", "onPostExecute lineoptions decoded")
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions)

        } else {
            Log.d("mylog", "without Polylines drawn")
        }
    }
}