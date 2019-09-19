package com.example.galeria

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView




class MainActivity : AppCompatActivity()  {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)
        fragment!!.onActivityResult(requestCode, resultCode, data)
    }


}
