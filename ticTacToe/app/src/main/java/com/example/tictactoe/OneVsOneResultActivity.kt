package com.example.tictactoe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_onevs_one_result.*

class OneVsOneResultActivity : AppCompatActivity() {
    private lateinit var player1Name: String
    private lateinit var player2Name: String
    private var player1Points: Int = 0
    private var player2Points: Int = 0
    private var rounds = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onevs_one_result)
        player1Name = intent.getStringExtra("player1")
        player2Name = intent.getStringExtra("player2")
        player1Points = intent.getIntExtra("player1Points", 0)
        player2Points = intent.getIntExtra("player2Points", 0)
        getWinner()
        setWinner()
        setTexts()
    }

    fun setWinner() {
        winnerTextView.text = when (getWinner()) {
            1 -> "$player1Name wygrał!"
            -1 -> "$player2Name wygrał!"
            else -> "Remis!"
        }

        player1PointsTextView.text = when (getWinner()) {
            1 -> ";-)"
            -1 -> ";-("
            else -> ";_;"
        }

        player2PointsTextView.text = when (getWinner()) {
            1 -> ";-("
            -1 -> ";-)"
            else -> ";_;"
        }
    }

    fun onBackButton(view: View) {
        finish()
    }

    private fun getWinner() = when {
        player1Points > player2Points -> 1
        player1Points < player2Points -> -1
        else -> 0
    }

    private fun setTexts() {
        player1NameTextView.text = player1Name
      //  player1PointsTextView.text = player1Points.toString()
        player2NameTextView.text = player2Name
       // player2PointsTextView.text = player2Points.toString()
    }
}
