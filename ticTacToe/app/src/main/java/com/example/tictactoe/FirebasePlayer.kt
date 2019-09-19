package com.example.tictactoe

class FirebasePlayer {
    var isSelected: Boolean = false
    lateinit var name: String
    lateinit var token: String
    var points: Int = 0

    constructor()

    constructor(name: String, token: String, points: Long) {
        this.name = name
        this.token = token
        this.points = points.toInt()
    }
}