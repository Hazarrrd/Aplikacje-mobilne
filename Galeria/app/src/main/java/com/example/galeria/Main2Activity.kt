package com.example.galeria



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main2.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap



class Main2Activity : AppCompatActivity() {

        var id = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var img = findViewById(R.id.imageView) as ImageView
        var ratingbar = findViewById(R.id.ratingBar) as RatingBar
        var textview = findViewById(R.id.textView) as TextView

        val text = intent.getStringExtra("text")
        val image = intent.getStringExtra("image")
        val note = intent.getFloatExtra("note", 5.0F)
        val camera = intent.getBooleanExtra("camera", false)
        id = intent.getIntExtra("id",-1)

        if(!camera)
            Picasso.get().load(getImageId(applicationContext,image)).into(img);
        else {
            val byteArray = intent.getByteArrayExtra("bitmap")
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            img.setImageBitmap(bmp)
        }
        ratingbar.rating = note
        textview.text = text
    }

    override fun onStop() {
        super.onStop()

       /* var ratingbar = findViewById(R.id.ratingBar) as RatingBar
        var textview = findViewById(R.id.textView) as TextView
        val data = Intent(this, MainActivity::class.java)

        data.putExtra("text", textview.text)
        data.putExtra("note", ratingbar.rating)
        data.putExtra("id", id)
        setResult(Activity.RESULT_OK, data)
        finish()*/
    }

    fun sumbit(view: View) {
        var note = findViewById(R.id.ratingBar) as RatingBar
        //var temp2 = findViewById(R.id.calendarView2) as CalendarView
        var text = findViewById(R.id.textView) as TextView

        val data = Intent(this, MainActivity::class.java)

        data.putExtra("text", text.text)
        data.putExtra("note", note.rating)
        data.putExtra("id", id)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    fun getImageId(context: Context, imageName: String): Int {
        return context.resources.getIdentifier("drawable/$imageName", null, context.packageName)
    }
}
