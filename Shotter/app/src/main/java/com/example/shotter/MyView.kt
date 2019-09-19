package com.example.shotter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.service.autofill.Validators.not
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.lang.Math.abs


class MyView(context: Context, attrs: AttributeSet)
    : View(context, attrs) {

    var paint = Paint()

    var hits = 0
    var ballX = 0f
    var ballY = 0f
    var lineX = 0f
    var dx = 8f
    var dy = 8f
    var isInit = true
    var lose = false
    var ballSize = 50f
    var lineSize = 300f
    var brickWidth = 0.0
    var brickHeight = 0.0
    var bricks = arrayListOf<Brick>()
   // private val thread : GameThread




    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isInit)
            initFunction()

        val color = Paint()
        if (canvas == null) return

        if(bricks.size > 0)
            for (i in 0 .. bricks.size-1){
                if(i < bricks.size) {
                    val brick = bricks[i]

                    paint.setStrokeWidth(3f);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.RED);
                    canvas.drawRect(RectF(brick.left, brick.top, brick.right, brick.bottom), paint)
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(RectF(brick.left, brick.top, brick.right, brick.bottom), paint)
                }
              //  Toast.makeText(context,   " " + brick.left + " ; " +  brick.top + " ; " + brick.right + " ; " + brick.bottom, Toast.LENGTH_SHORT).show()

            }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawOval(RectF(ballX, ballY,ballX+ballSize,ballY+ballSize), paint)

        paint.setStrokeWidth(30f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        canvas.drawLine(lineX, height.toFloat(),lineX + lineSize, height.toFloat(), paint)

    }

    private fun initFunction() {

        lose = false
        dx = (-12..12).random().toFloat()
        dy = 8f
        hits = 0
        bricks = arrayListOf<Brick>()
        brickWidth = (width/10).toDouble()
        brickHeight = (height/18).toDouble()
 // 5 i 9
            for (j in 0 .. 5)
                for (i in 0 .. 9){
                    bricks.add(Brick((brickWidth*i).toFloat(), (brickHeight*j).toFloat(),
                        (brickWidth*(i+1)).toFloat(), (brickHeight*(j+1)).toFloat()
                    ))
                    //Toast.makeText(context,   " " + (brickHeight*i).toFloat() + " " + (brickHeight*i), Toast.LENGTH_SHORT).show()
                }
            ballX = (width/2).toFloat() - ballSize/2
            ballY = (height-ballSize).toFloat() - 21f
            lineX = ((width/2)-(lineSize/2))

            isInit = false


    }


    fun update() {


        if((ballY >= height - ballSize - 20f) &&  (abs(lineX+ (lineSize/2) - ballX-(ballSize/2) ) <= lineSize/2) && !isInit ){
            if(ballX+(ballSize/2) == lineX + lineSize/2){
                dx = 0f
                dy = -abs(dy)
            } else if (ballX+(ballSize/2) > lineX + lineSize/2){
                dx = (abs(lineX+ (lineSize/2) - ballX-(ballSize/2))/(lineSize/2))*18f
                dy = -abs(dy)
            }
            else {
                dx = - (abs(lineX+ (lineSize/2) - ballX-(ballSize/2))/(lineSize/2))*18f
                dy = -abs(dy)
            }
            hits ++
        } else if((ballY >= height - ballSize) && !isInit){
            lose = true
        }

        if (ballX <= 0 || ballX+ballSize >= width) {
            dx = -dx

        }
        if (ballY <= 0 ) {
            dy = -dy

        }

        ballX+=dx
        ballY+=dy


        if(bricks.size > 0){
            var i =0
            while (i<bricks.size){
                val brick = bricks[i]

                if (insideBrick(brick,ballX,ballY) && insideBrick(brick,ballX+ballSize,ballY) && rinsideBrick(brick,ballX,ballY+ballSize) && rinsideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dy = -dy
                    bricks.removeAt(i)
                    i = i -1
                } else if (rinsideBrick(brick,ballX,ballY) && rinsideBrick(brick,ballX+ballSize,ballY) && insideBrick(brick,ballX,ballY+ballSize) && insideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dy = -dy
                    bricks.removeAt(i)
                    i = i -1
                } else if (rinsideBrick(brick,ballX,ballY) && insideBrick(brick,ballX+ballSize,ballY) && rinsideBrick(brick,ballX,ballY+ballSize) && insideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dx = -dx
                    bricks.removeAt(i)
                    i = i -1
                } else if (insideBrick(brick,ballX,ballY) && rinsideBrick(brick,ballX+ballSize,ballY) && insideBrick(brick,ballX,ballY+ballSize) && rinsideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dx = -dx
                    bricks.removeAt(i)
                    i = i -1
                } else if (insideBrick(brick,ballX,ballY) && rinsideBrick(brick,ballX+ballSize,ballY) && rinsideBrick(brick,ballX,ballY+ballSize) && rinsideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dx = abs(dx)
                    dy = abs(dy)
                    bricks.removeAt(i)
                    i = i -1
                } else if (rinsideBrick(brick,ballX,ballY) && insideBrick(brick,ballX+ballSize,ballY) && rinsideBrick(brick,ballX,ballY+ballSize) && rinsideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dx = -abs(dx)
                    dy = abs(dy)
                    bricks.removeAt(i)
                    i = i -1
                } else if (rinsideBrick(brick,ballX,ballY) && rinsideBrick(brick,ballX+ballSize,ballY) && insideBrick(brick,ballX,ballY+ballSize) && rinsideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dx = abs(dx)
                    dy = -abs(dy)
                    bricks.removeAt(i)
                    i = i -1
                } else if (rinsideBrick(brick,ballX,ballY) && rinsideBrick(brick,ballX+ballSize,ballY) && rinsideBrick(brick,ballX,ballY+ballSize) && insideBrick(brick,ballX+ballSize,ballY+ballSize)) {
                    dx = -abs(dx)
                    dy = -abs(dy)
                    bricks.removeAt(i)
                    i = i -1
                }
                i++
            }

            }
        postInvalidate()
    }

    private fun rinsideBrick(brick: Brick, ballX: Float, ballY: Float): Boolean {
        return !(insideBrick(brick, ballX, ballY))
    }

    private fun insideBrick(brick: Brick, ballX: Float, ballY: Float): Boolean {

        if(brick.left <= ballX && ballX <= brick.right && brick.top <= ballY && brick.bottom >= ballY )
            return true
        else
            return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
/*
        ballX = event.x.toFloat() - (ballSize/2)
        ballY = event.y.toFloat() - (ballSize/2)

        if (ballX <= 0) ballX = Math.abs(dx)
        if (ballX+ballSize >= width) ballX = width.toFloat() - ballSize - Math.abs(dx)
        if (ballY <= 0) ballY = Math.abs(dy)
        if (ballY+ballSize >= height) ballY = height.toFloat() - ballSize - Math.abs(dy)
*/
        lineX = event.x.toFloat() - (lineSize/2)

        postInvalidate()

        return true

        return super.onTouchEvent(event)
    }
}