package com.example.galeria

import android.graphics.Bitmap

class myImage {
    constructor(image : String, queue: Int, bitmap : Bitmap?) {
        this.text = "Brak opisu"
        this.image = image
        this.queue = queue
        this.note = 3.0F
        this.camera = false
        this.bitmap = bitmap
        if(bitmap != null) {
            this.camera = true;
            this.text = "Dodane przez aparat";
        }
    }

    var text: String
    val image: String
    val queue: Int
    var note: Float
    var camera: Boolean
    val bitmap: Bitmap?

}