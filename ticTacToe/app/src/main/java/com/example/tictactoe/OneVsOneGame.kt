package com.example.tictactoe




class OneVsOneGame(
    val gameName: String,
    val challenger: Player,
    val challenged: Player,
    var results: ArrayList<Int>,
    var turn: Int,
    var nextToPlay: Player,
    var currentRound: Int
) {

    constructor(name: String, gameMap: Map<String, Any>): this(
        name,
        Player(gameMap.getValue("challenger") as Map<String, String>),
        Player(gameMap.getValue("challenged") as Map<String, String>),
        arrayListOf<Int>(0,0,0,0,0,0,1,0,0),
        (gameMap.getValue("turn") as Long).toInt(),
        Player(gameMap.getValue("nextToPlay") as Map<String, String>),
        (gameMap.getValue("currentRound") as Long).toInt()
    ) {
        val longs = (gameMap.getValue("results") as ArrayList<Long>)
        for (i in 0 until longs.size) {
            results[i] = longs[i].toInt()
        }
    }

    fun toMap(): Map<String, Any> {

        return mapOf(
            "challenger" to challenger.toMap(),
            "challenged" to challenged.toMap(),
            "results" to results,
            "turn" to turn,
            "nextToPlay" to nextToPlay.toMap(),
            "currentRound" to currentRound
        )
    }

    fun isEnded() : Boolean {
        if(results[0] == results[1] && results[1] == results[2] && results[2] != 0){
           return true
        } else if(results[3] == results[4] && results[4] == results[5] && results[5] != 0){
            return true
        } else if(results[6] == results[7] && results[7] == results[8] && results[8] != 0){
            return true
        } else if(results[0] == results[3] && results[3] == results[6] && results[6] != 0){
            return true
        } else if(results[1] == results[4] && results[4] == results[7] && results[7] != 0){
            return true
        } else if(results[2] == results[5] && results[5] == results[8] && results[8] != 0){
            return true
        } else if(results[0] == results[4] && results[4] == results[8] && results[8] != 0){
            return true
        } else if(results[2] == results[4] && results[4] == results[6] && results[6] != 0){
            return true
        }
        results.forEachIndexed { index, i -> if(i == 0) return false }
        return true
    }
}