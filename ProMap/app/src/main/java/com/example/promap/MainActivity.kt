package com.example.promap

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myintent = Intent(this, MapsActivity::class.java)
        startActivity(myintent)
    }

    override fun onResume() {
        super.onResume()
        finish()
    }
}
