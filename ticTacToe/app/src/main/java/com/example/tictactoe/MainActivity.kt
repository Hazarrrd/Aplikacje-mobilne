package com.example.tictactoe

import android.R.*
import android.R.attr.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import android.widget.ImageButton
import android.content.Intent



class MainActivity : AppCompatActivity() {

    private var buttons = arrayOfNulls<ImageButton>(25)
    private var results = arrayOfNulls<Int>(25)
    private var turn = 0
    private var winner = 0
    private var cpu = 0
    private var init = true
    private var single = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var y = 0
        for (x in 0..24) {
            y = x+1
            val btn = "imageButton$y"
            val resID = resources.getIdentifier(btn, "id", packageName)
            buttons[x] = findViewById(resID)
            buttons[x]!!.setOnClickListener {
                doMove(x)
            }
            buttons[x]!!.setClickable(false)

        }
    }

    private fun prepere() {
        winner = 0
        turn = (0..1).random()

        if (turn == 0) {
            findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.circle)
            findViewById<TextView>(R.id.textView).text = "Teraz ruch ma kółko"
        }
        else {
            findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.cross)
            findViewById<TextView>(R.id.textView).text = "Teraz ruch ma krzyżyk"
        }
        findViewById<ImageView>(R.id.imageView).setVisibility(View.VISIBLE);
        for (x in 0..24) {
            results[x] = 0
        }

        for (x in 0..24) {
            buttons[x]!!.setClickable(true)
            buttons[x]!!.setImageResource(R.drawable.background)
        }

    }

    fun onePlayer(view: View) {
        single = true
        prepereForOne()
    }

    fun multi(view: View) {
        val myIntent = Intent(this, LoginActivity::class.java)
        startActivity(myIntent)
        //finish()
    }

    private fun prepereForOne() {
        cpu = (0..1).random()
        prepere()
        init = true
        if (cpu == turn){
            cpuMove()
        }
    }


    fun twoPlayer(view: View) {
        single = false
        prepereForTwo()
    }

    private fun prepereForTwo() {
        prepere()
    }


    private fun doMove(x: Int) {

        var btn = buttons[x]!!
        if (turn == 0){
            btn.setImageResource(R.drawable.circle)
            findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.cross)
            findViewById<TextView>(R.id.textView).text = "Teraz ruch ma krzyżyk"
        }
        else{
            btn.setImageResource(R.drawable.cross)
            findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.circle)
            findViewById<TextView>(R.id.textView).text = "Teraz ruch ma kółko"
        }
        results[x] = turn + 1
        btn.setClickable(false);
        turn = (turn+1) % 2
        checkIfEnd()

        if(single && turn == cpu){
            cpuMove()
        }

    }

    private fun cpuMove() {
        var arr: Array<Int>
        if (init){
            init = false
            if(results[12]==0) {
                cpuClick(12)
                return
            }
            else {
                arr = arrayOf(6,18,8,16)
                cpuClick(arr.random())
                return
            }
        } else {

            if (defense()) return
            var opp = ((cpu +1)%2)+1
            for (i in 0..4) {
                if(attack1(i * 5,i * 5+1,i * 5+2,i * 5+3, i * 5+4, opp)) return
            }

            for (i in 0..4) {
                if(attack1(i ,i + 5,i + 10,i + 15,i + 20, opp)) return
            }
            if(attack1(0,6,12,18,24, opp)) return
            if(attack1(4,8,12,16,20, opp)) return
            if(attack2(5,11,17,23, opp)) return
            if(attack2(1,7,13,19, opp)) return
            if(attack2(3,7,11,15 , opp)) return
            if(attack2(9,13,17,21, opp)) return

            for (j in 0..24) {
                if (results[j]==0) {
                    cpuClick(j)
                    return
                }
            }




        }
    }

    private fun attack1(
        a: Int,
        b: Int,
        c: Int,
        d: Int,
        e: Int,
        opp: Int

    ): Boolean {

        if ((results[a] != opp || results[e] != opp) && results[b] != opp && results[c] != opp && results[d] != opp) {
            var arr1 = arrayOf(0)
            if (results[a] != opp)
                arr1 = arrayOf(a, b, c, d)
            else
                arr1 = arrayOf(b, c, d, e)
            var rand = arr1.random()
            while (results[rand] != 0) {
                rand = arr1.random()
            }
            cpuClick(rand)
            return true
        }
        return false
    }

    private fun attack2(
        a: Int,
        b: Int,
        c: Int,
        d: Int,
        opp: Int
    ): Boolean {
        var arr1 = arrayOf(0)
        if (results[a] != opp  && results[b] != opp && results[c] != opp && results[d] != opp) {
            arr1 = arrayOf(a, b, c, d)
            var rand = arr1.random()
            while (results[rand] != 0) {
                rand = arr1.random()
            }
            cpuClick(rand)
            return true
        }
        return false
    }

    private fun defense(): Boolean {
        for (i in 6..18) {
            for (z in 6..18) {
                if (z != i && results[i] == results[z] && results[i] == (((cpu + 1) % 2) + 1)) {
                    if (areNeigbours(i, z)) {
                        if (clickIfDangerous(i, z)) return true
                    }
                }
            }
        }
        if (cpuHelper(1, 2)) return true
        if (cpuHelper(2, 3)) return true
        if (cpuHelper(9, 14)) return true
        if (cpuHelper(14, 19)) return true
        if (cpuHelper(21, 22)) return true
        if (cpuHelper(22, 23)) return true
        if (cpuHelper(5, 10)) return true
        if (cpuHelper(10, 15)) return true
        return false
    }


    private fun cpuHelper(i: Int, z: Int): Boolean {
        if(results[i] == results[z] && results[i]==(((cpu +1)%2)+1)){
            if (clickIfDangerous(i, z)) return true
        }
        return false
    }

    private fun clickIfDangerous(i: Int, z: Int): Boolean {
        if (results[2 * i - z] != (cpu + 1) && results[2 * z - i] != (cpu + 1)) {
            var rand = arrayOf(i, z).random()

            if (rand == i) {
                if (results[i*2 - z]==0)
                    cpuClick(i*2 - z)
                else
                    cpuClick(z*2 - i)
                return true
            } else {
                if (results[z*2 - i]!=0)
                    cpuClick(i*2 - z)
                else
                    cpuClick(z*2 - i)
                return true
            }
        }
        return false
    }

    private fun areNeigbours(i: Int, z: Int): Boolean {
        if(z==i+1 || z == i-1 || z == i -5 || z == i +5 || z == i +6 || z == i -6 || z == i -4 || z == i +4)
            return true
        return false
    }

    private fun cpuClick(i: Int) {
        results[i] = turn + 1
        buttons[i]!!.performClick()
        buttons[i]!!.setClickable(false);
    }

    private fun checkIfEnd() {
        for (i in 0..4) {
            if ((results[i*5] == results[i*5+1] || results[i*5+3] == results[i*5+4] ) && results[i*5+1] == results[i*5+2] && results[i*5+2] == results[i*5+3] && results[i*5+1]!=0 ) {
                winner = results[i*5+1]!!
            }

            if ((results[i] == results[i+5] || results[i+15] == results[i+20] )&& results[i+5] == results[i+10] && results[i+10] == results[i+15] && results[i+5]!=0 ) {
                winner = results[i+5]!!
            }
        }

        if ((results[0] == results[6] || results[18] == results[24]) && results[6] == results[12] && results[12] == results[18]  && results[18]!=0  ) {
            winner = results[6]!!
        }

        if ((results[4] == results[8] || results[16] == results[20]) && results[8] == results[12] && results[12] == results[16] && results[16]!=0 ) {
            winner = results[8]!!
        }

        if (results[1] == results[7] && results[7] == results[13] && results[13] == results[19] && results[19]!=0 ) {
            winner = results[1]!!
        }

        if (results[5] == results[11] && results[11] == results[17] && results[17] == results[23] && results[23]!=0 ) {
            winner = results[5]!!
        }

        if (results[3] == results[7] && results[7] == results[11] && results[11] == results[15] && results[15]!=0 ) {
            winner = results[3]!!
        }

        if (results[9] == results[13] && results[13] == results[17] && results[17] == results[21] && results[21]!=0 ) {
            winner = results[21]!!
        }


        if(winner == 1) {
            for (x in 0..24) {
                buttons[x]!!.setClickable(false)
            }
            findViewById<ImageView>(R.id.imageView).setVisibility(View.INVISIBLE);
            findViewById<TextView>(R.id.textView).text = "WYGRAŁO KÓŁKO, GRATULACJE!"
            single = false;
            Toast.makeText(this, "Kółko wygrało!", Toast.LENGTH_SHORT).show()
        } else if(winner == 2) {
            for (x in 0..24) {
                buttons[x]!!.setClickable(false)
            }
            findViewById<ImageView>(R.id.imageView).setVisibility(View.INVISIBLE);
            findViewById<TextView>(R.id.textView).text = "WYGRAŁ KRZYŻYK, GRATULACJE!"
            Toast.makeText(this, "Krzyżyk wygrał!", Toast.LENGTH_SHORT).show()
            single = false;
        } else  {
            winner = -1
            for (x in 0..24) {
                if(results[x]==0) {
                    winner = 0
                    break;
                }
            }
            if (winner == -1) {
                for (x in 0..24) {
                    buttons[x]!!.setClickable(false)
                }
                findViewById<ImageView>(R.id.imageView).setVisibility(View.INVISIBLE);
                findViewById<TextView>(R.id.textView).text = "GRA ZAKOŃCZONA REMISEM!"
                Toast.makeText(this, "Remis!", Toast.LENGTH_SHORT).show()
                single = false;
            }
        }
    }
}
