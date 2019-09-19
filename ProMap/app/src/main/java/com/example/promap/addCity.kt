package com.example.promap

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_city.*
import kotlinx.coroutines.runBlocking

class addCity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)
    }

    fun dodaj(view: View) {
            if(miasto.text.toString()!="" && wspX.text.toString()!="" && wspY.text.toString()!=""){
                Thread {
                    try {
                        FirestoreHandler().createCity(
                            miasto.text.toString(),
                            wspX.text.toString().toFloat(),
                            wspY.text.toString().toFloat()
                        )
                        Thread.sleep(2000)
                        intent.putExtra("name",miasto.text.toString())
                        intent.putExtra("X",wspX.text.toString().toFloat())
                        intent.putExtra("Y",wspY.text.toString().toFloat())
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } catch (nfe: NumberFormatException) {

                    }
                }.start()
            } else {
                Toast.makeText(this, "Niepoprawne dane", Toast.LENGTH_SHORT).show()
            }

    }
}
