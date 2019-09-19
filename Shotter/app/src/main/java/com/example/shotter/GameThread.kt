package com.example.shotter

import android.graphics.Canvas
import android.os.Handler
import android.os.Message
import android.view.SurfaceHolder

class GameThread(
                 private val gameView: MyView) : Thread() {

    private var running : Boolean = false
    private val targetFPS = 50
    private var canvas: Canvas? = null

    fun setRunning(isRunning : Boolean) {
        this.running = isRunning
    }

    override fun run() {
        var startTime : Long
        var timeMillis : Long
        var waitTime : Long
        val targetTime = (1000 / targetFPS).toLong()

        while(running) {
            startTime = System.nanoTime()
            canvas = null

            try {
               // canvas = surfaceHolder.lockCanvas()
         //       synchronized(surfaceHolder) {
                    gameView.update()
                   // gameView.draw(canvas!!)
             //   }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
               /* if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }*/
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000

            waitTime = targetTime - timeMillis

            try {
                sleep(waitTime)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


}