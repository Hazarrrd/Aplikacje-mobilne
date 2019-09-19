package com.example.shotter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    var win = false
    var isThreadAlive = true;
    var stillPlaying = true
    var recordValue = 500
    var hits = 0;
    var pause = false
    var t : Thread? = null
    private var PREFS_NAME = "prefs"
    lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences( PREFS_NAME, Context.MODE_PRIVATE)
        recordValue = sharedPref.getInt("record", 0)

        var record = findViewById(R.id.record) as TextView
        record.text = "REKORD :" + recordValue.toString()

        buildStartDialog()

    }

    override fun onPause() {
        super.onPause()
        pause = true
    }

    override fun onStart() {
        super.onStart()

        pause = false
    }

    override fun onResume() {
        super.onResume()
        pause = false
    }

    override fun onStop() {
        super.onStop()

        pause = true
    }


    private fun startGame() {
        t = Thread {
            var result = findViewById(R.id.result) as TextView
            while (stillPlaying){
                ball.isInit = true
                while (true) {
                    while(pause){

                    }
                    if (ball.isInit == false && (ball.bricks.size == 0 || ball.lose == true)) {
                        break
                    } else {
                        Thread.sleep(20)
                        ball.update()
                        runOnUiThread {
                            result.text = "ILOŚĆ ODBIĆ :" + ball.hits.toString()
                        }
                    }
                }

                val message = Message()

                if (ball.lose == true) {
                    win = false
                    message.what = 1
                    message.arg1 = 0
                } else {
                    win = true
                    hits = ball.hits
                    message.what = 1
                    message.arg1 = 1
                }
                isThreadAlive = false
                stillPlaying = false
                mHandler.sendMessage(message)
                while (!stillPlaying) {

                }
            }
        }
        t!!.start()
    }

    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if(msg!!.what == 1)
                buildEndDialog()

        }
    }

    private fun buildEndDialog() {

        val builder = AlertDialog.Builder(this@MainActivity)

        // Set the alert dialog title
        if (win == true)
            if(hits < recordValue || recordValue == 0) {
                recordValue = hits
                builder.setTitle("Gratulacje, NOWY REKORD : " + hits + " !!!")

                sharedPref = getSharedPreferences( PREFS_NAME, Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("record", recordValue)
                editor!!.commit()

                var record = findViewById(R.id.record) as TextView
                record.text = "REKORD :" + recordValue.toString()
            } else {
                builder.setTitle("Wygrałeś, ILOŚĆ ODBIĆ : " + hits + " !!!")
            }
        else
            builder.setTitle("Tym razem się nie udało :-(")

        // Display a message on alert dialog
        builder.setMessage("Chcesz zagrać jeszcze raz?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("TAK!"){dialog, which ->
            // Do something when user press the positive button
            stillPlaying = true
        }


        // Display a negative button on alert dialog
        builder.setNegativeButton("NIE!"){dialog,which ->
            stillPlaying = false
            finish();
            System.exit(0);
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun buildStartDialog() {

        val builder = AlertDialog.Builder(this@MainActivity)

        // Set the alert dialog title
        builder.setTitle("Witaj w grze!")


        // Display a message on alert dialog
        builder.setMessage("Chcesz rozpocząć rozgrywkę, czy jednak nie tym razem?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("ZACZYNAM!"){dialog, which ->
            // Do something when user press the positive button
            startGame()

        }


        // Display a negative button on alert dialog
        builder.setNegativeButton("NIE TERAZ!"){dialog,which ->
            finish();
            System.exit(0);
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        // Display the alert dialog on app interface
        dialog.show()
    }


}
