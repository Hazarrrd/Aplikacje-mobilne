package com.example.tictactoe

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class OneVsOneGameActivity : AppCompatActivity() {
    private var answerCorrect: Boolean = false
    private var turn : Int = 0
    private var buttons = arrayOfNulls<ImageButton>(9)
    private lateinit var results : ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_vs_one_game)
        turn = intent.getIntExtra("turn",-1)
        results = intent.getIntegerArrayListExtra("results")

        prepere()

        var y = 0
        for (x in 0..8) {
            y = x+1
            val btn = "Button$y"
            val resID = resources.getIdentifier(btn, "id", packageName)
            buttons[x] = findViewById(resID)
            buttons[x]!!.setOnClickListener {
                doMove(x)
            }
            if(results[x]==0){
                buttons[x]!!.setImageResource(R.drawable.background)

            }else if(results[x]==1){
                buttons[x]!!.setClickable(false)
                buttons[x]!!.setImageResource(R.drawable.circle)

            }else if(results[x]==2) {
                buttons[x]!!.setClickable(false)
                buttons[x]!!.setImageResource(R.drawable.cross)
            }

        }

    }

    private fun prepere() {
        if (turn == 0) {
            findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.circle)
        }
        else {
            findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.cross)
        }
        findViewById<ImageView>(R.id.imageView).setVisibility(View.VISIBLE);
    }

    private fun doMove(x: Int) {

        var btn = buttons[x]!!
        if (turn == 0){
            btn.setImageResource(R.drawable.circle)
        }
        else{
            btn.setImageResource(R.drawable.cross)
        }
        results[x] = turn + 1
        btn.setClickable(false);

        val resultIntent = Intent().apply {
            putExtra("results", results)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onBackPressed() {}
}
