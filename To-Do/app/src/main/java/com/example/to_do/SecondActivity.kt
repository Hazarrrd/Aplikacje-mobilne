package com.example.to_do

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class SecondActivity : AppCompatActivity() {


    val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
    private var mydate: String = ""
    var image : String = "0";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtolist)

        var temp = findViewById(R.id.calendarView2) as CalendarView
        temp.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("yyyyMdd")
            val d = sdf.parse("$year${month + 1}$dayOfMonth")
            mydate = dateFormat.format(d)
        }
        mydate = dateFormat.format(temp.date)
    }

    fun chooseImg1(view: View) {
        var temp1 = findViewById(R.id.imageButton1) as ImageButton
        var temp2 = findViewById(R.id.imageButton2) as ImageButton
        var temp3 = findViewById(R.id.imageButton3) as ImageButton
        temp1.setBackgroundResource(android.R.drawable.btn_default)
        temp2.setBackgroundResource(android.R.drawable.btn_default)
        temp3.setBackgroundResource(android.R.drawable.btn_default)
        temp1.setBackgroundColor(Color.parseColor("#ff0099cc"))
        image = "1";

    }

    fun chooseImg2(view: View) {
        var temp1 = findViewById(R.id.imageButton1) as ImageButton
        var temp2 = findViewById(R.id.imageButton2) as ImageButton
        var temp3 = findViewById(R.id.imageButton3) as ImageButton
        temp1.setBackgroundResource(android.R.drawable.btn_default)
        temp2.setBackgroundResource(android.R.drawable.btn_default)
        temp3.setBackgroundResource(android.R.drawable.btn_default)
        temp2.setBackgroundColor(Color.parseColor("#ff0099cc"))
        image = "2";
    }

    fun chooseImg3(view: View) {
        var temp1 = findViewById(R.id.imageButton1) as ImageButton
        var temp2 = findViewById(R.id.imageButton2) as ImageButton
        var temp3 = findViewById(R.id.imageButton3) as ImageButton
        temp1.setBackgroundResource(android.R.drawable.btn_default)
        temp2.setBackgroundResource(android.R.drawable.btn_default)
        temp3.setBackgroundResource(android.R.drawable.btn_default)
        temp3.setBackgroundColor(Color.parseColor("#ff0099cc"))
        image = "3";
    }

    fun sumbit(view: View) {
        var temp = findViewById(R.id.input) as EditText
        //var temp2 = findViewById(R.id.calendarView2) as CalendarView
        var temp3 = findViewById(R.id.seekBar) as SeekBar
        var newData = temp.text.toString()

        if (newData != "" && image != "0") {
            val data = Intent(this, MainActivity::class.java)
            data.putExtra("note", newData)
            data.putExtra("priority", temp3.progress)
            data.putExtra("date", mydate)
            data.putExtra("image", image)
            //data.putExtra("image", choosenImage(pickimage))
            setResult(Activity.RESULT_OK, data)
            finish()
        } else if(newData == ""){
            Toast.makeText(
                applicationContext,
                "Najpierw wpisz jakąś notkę", Toast.LENGTH_SHORT

            ).show()
        } else if(image == "0"){
            Toast.makeText(
                applicationContext,
                "Najpierw wybierz jakiś obrazek", Toast.LENGTH_SHORT

            ).show()
        }
        //setContentView(R.layout.activity_main)

    }
}