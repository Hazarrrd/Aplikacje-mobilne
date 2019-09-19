package com.example.tictactoe

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreHandler {
    private val firestore = FirebaseFirestore.getInstance()

    fun createGame(
        gameName: String,
        challenger: Player,
        challenged: Player
    ) {
        if((0..1).random() == 0){
            val game = OneVsOneGame(gameName, challenger, challenged, arrayListOf<Int>(0,0,0,0,0,0,0,0,0),(0..1).random(),challenged ,0)
            createDocumentInCollectionWithData(
                "1vs1",
                gameName,
                game.toMap()
            )
        } else {
            val game = OneVsOneGame(gameName, challenger, challenged,arrayListOf<Int>(0,0,0,0,0,0,0,0,0),(0..1).random(),challenger ,0)
            createDocumentInCollectionWithData(
                "1vs1",
                gameName,
                game.toMap()
            )
        }
    }

    fun createPlayer(name: String, token: String, password : String) {
        createDocumentInCollectionWithData(
            "players",
            name,
            mapOf("name" to name, "token" to token, "password" to password)
        )
    }

    fun nextRoundInGame(game: OneVsOneGame, results : ArrayList<Int>) {
        if (game.nextToPlay.name == game.challenged.name) {
            game.nextToPlay = game.challenger
            game.turn +=1;
            game.turn = game.turn % 2;
            game.results = results

        } else if (game.nextToPlay.name == game.challenger.name) {
            game.nextToPlay = game.challenged
            game.currentRound += 1
            game.turn +=1;
            game.turn = game.turn % 2;
            game.results = results
        }
        firestore.collection("/1vs1")
            .document(game.gameName)
            .set(game.toMap())
    }

    fun getAllPlayers(): Task<QuerySnapshot> {
        return firestore.collection("/players")
            .get()
    }

    fun deleteGame(gameName: String): Task<Void> {
        return firestore.collection("/1vs1")
            .document(gameName)
            .delete()
    }

    fun getGamesForPlayerAsChallenger(playerName: String): Task<QuerySnapshot> {
        return firestore.collection("/1vs1")
            .whereEqualTo("challenger.name", playerName)
            .get()
    }

    fun getGamesForPlayerAsChallenged(playerName: String): Task<QuerySnapshot> {
        return firestore.collection("/1vs1")
            .whereEqualTo("challenged.name", playerName)
            .get()
    }


    private fun createDocumentInCollectionWithData(
        collectionName: String,
        documentName: String,
        data: Any
    ): Task<Void> {
        return firestore.collection(collectionName)
            .document(documentName)
            .set(data)
    }
}