package com.example.to_do

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MyAdapter(private val context: Context,
                private val toDo: ArrayList<data>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return toDo.size
    }

    //2
    override fun getItem(position: Int): Any {
        return toDo[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.listview_item, parent, false)

        val textView = rowView.findViewById(R.id.text) as TextView

// Get subtitle element
        val timeView = rowView.findViewById(R.id.time) as TextView

// Get detail element
        val priorityView = rowView.findViewById(R.id.priority) as TextView

// Get thumbnail element
        val pictureView = rowView.findViewById(R.id.picture) as ImageView

        val data = getItem(position) as data

// 2
        textView.text = data.text
        timeView.text = data.time
        priorityView.text = data.priority.toString()
        when (data.image) {
            "1" -> pictureView.setImageResource(R.drawable.portfolio)
            "2" -> pictureView.setImageResource(R.drawable.gloves)
            "3" -> pictureView.setImageResource(R.drawable.`fun`)
        }


        return rowView
    }
}