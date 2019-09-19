package com.example.tictactoe

class Player(var name: String, private val token: String, var points: Int) {
    constructor(map: Map<String, String>): this(
        map.getValue("name"),
        map.getValue("token"),
        (map.getValue("points") as Long).toInt()
    )
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "token" to token,
            "points" to points
        )
    }
}
