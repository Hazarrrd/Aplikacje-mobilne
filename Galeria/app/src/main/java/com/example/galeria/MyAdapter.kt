package com.example.galeria


import android.content.Context
import android.content.Intent

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.nio.file.Files.size
import android.support.v4.content.ContextCompat.startActivity
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import android.app.Activity
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


class MyAdapter(private val activity: Activity, private val galleryList: ArrayList<myImage>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.listview_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyAdapter.ViewHolder, i: Int) {
      //  viewHolder.title.setText("XXX")
        viewHolder.img.scaleType = ImageView.ScaleType.CENTER_CROP
       // viewHolder.img.setImageResource(getImageId(context,galleryList.get(i).image))
        if(!galleryList.get(i).camera)
            Picasso.get().load(getImageId(activity,galleryList.get(i).image)).into(viewHolder.img);
        else {
            viewHolder.img.setImageBitmap(galleryList.get(i).bitmap)
        }
        viewHolder.img.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
               // Toast.makeText(activity, "Image " + galleryList.get(i).queue, Toast.LENGTH_SHORT).show()
                var intent = Intent(activity, Main2Activity::class.java)
                intent.putExtra("text", galleryList.get(i).text)
                intent.putExtra("image", galleryList.get(i).image)
                intent.putExtra("note",galleryList.get(i).note)
                intent.putExtra("camera",galleryList.get(i).camera)
                intent.putExtra("id",i)
                if(galleryList.get(i).camera){
                    val stream = ByteArrayOutputStream()
                    galleryList.get(i).bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    intent.putExtra("bitmap", byteArray)
                }
               // intent.putExtra("image_name", mImageNames.get(position));
                activity.startActivityForResult(intent,0)
            }
        })

    }

    override fun getItemCount(): Int {
        return galleryList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
       // val title: TextView
        val img: ImageView
       // var parentLayout: RelativeLayout? = null

        init {

           // title = view.findViewById<View>(R.id.title) as TextView
            img = view.findViewById(R.id.img) as ImageView
           // parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    fun getImageId(context: Context, imageName: String): Int {
        return context.resources.getIdentifier("drawable/$imageName", null, context.packageName)
    }
}