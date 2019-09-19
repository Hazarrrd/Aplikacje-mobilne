package com.example.promap

class City(var name: String, val X: Float, var Y: Float) {
    constructor(map: Map<String, Any>): this(
        (map.getValue("City") as String),
        (map.getValue("X") as Number).toFloat(),
        (map.getValue("Y") as Number).toFloat()
    )
    fun toMap(): Map<String, Any> {
        return mapOf(
            "City" to name, "X" to X, "Y" to Y
        )
    }
}
