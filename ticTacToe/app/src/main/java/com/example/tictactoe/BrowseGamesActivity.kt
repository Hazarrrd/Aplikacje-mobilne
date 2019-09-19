package com.example.tictactoe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.firebase.firestore.QuerySnapshot

class BrowseGamesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var roomListAdapter: RecyclerView.Adapter<*>
    private lateinit var listLayoutManager: RecyclerView.LayoutManager
    private lateinit var selectedGame: OneVsOneGame
    private lateinit var playerName: String
    private lateinit var token: String
    private var gamesList = arrayListOf<OneVsOneGame>()
    private val firestore = FirestoreHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_rooms)
        getPlayerInfo()
        loadGames()

        listLayoutManager = LinearLayoutManager(this)
        roomListAdapter = GamesListAdapter(gamesList, applicationContext) { game -> onItemClick(game) }
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = listLayoutManager
            adapter = roomListAdapter
        }
        roomListAdapter.notifyDataSetChanged()
    }

    private fun loadGames() {
        gamesList.clear()
        firestore.getGamesForPlayerAsChallenged(playerName)
            .addOnSuccessListener { result ->
                gamesList.addAll(mapDocumentsToGames(result))
                roomListAdapter.notifyDataSetChanged()
            }
        firestore.getGamesForPlayerAsChallenger(playerName)
            .addOnSuccessListener { result ->
                gamesList.addAll(mapDocumentsToGames(result))
                roomListAdapter.notifyDataSetChanged()
            }
    }

    private fun onItemClick(game: OneVsOneGame) {
        if (game.nextToPlay.name == playerName && !game.isEnded()) {
            selectedGame = game
            val intent = Intent(this, OneVsOneGameActivity::class.java).apply {
                putExtra("turn", game.turn)
                putExtra("results", game.results)
            }
            startActivityForResult(intent, REQUEST_PLAY_GAME)
        }
        if (game.isEnded()) {
            var draw = true

            if(game.results[0] == game.results[1] && game.results[1] == game.results[2] && game.results[2] != 0){
                draw = false
            } else if(game.results[3] == game.results[4] && game.results[4] == game.results[5] && game.results[5] != 0){
                draw = false
            } else if(game.results[6] == game.results[7] && game.results[7] == game.results[8] && game.results[8] != 0){
                draw = false
            } else if(game.results[0] == game.results[3] && game.results[3] == game.results[6] && game.results[6] != 0){
                draw = false
            } else if(game.results[1] == game.results[4] && game.results[4] == game.results[7] && game.results[7] != 0){
                draw = false
            } else if(game.results[2] == game.results[5] && game.results[5] == game.results[8] && game.results[8] != 0){
                draw = false
            } else if(game.results[0] == game.results[4] && game.results[4] == game.results[8] && game.results[8] != 0){
                draw = false
            } else if(game.results[2] == game.results[4] && game.results[4] == game.results[6] && game.results[6] != 0){
                draw = false
            }

            if(!draw){
                if(game.nextToPlay.name == game.challenged.name){
                    game.challenger.points = 2
                } else {
                    game.challenged.points = 2
                }
            }
            val intent = Intent(this, OneVsOneResultActivity::class.java).apply {
                putExtra("player1", game.challenged.name)
                putExtra("player1Points", game.challenged.points)
                putExtra("player2", game.challenger.name)
                putExtra("player2Points", game.challenger.points)
            }
            startActivity(intent)
        }
    }

    fun onSubmitButtonClick(view: View) {
        val intent = Intent(applicationContext, NewGameActivity::class.java)
        startActivity(intent)
    }

    fun onRefreshButtonClick(view: View) {
        loadGames()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PLAY_GAME) {
            if(resultCode == Activity.RESULT_OK) {
                val results = data!!
                    .getIntegerArrayListExtra("results")
                FirestoreHandler().nextRoundInGame(selectedGame, results)
            }
        }
        loadGames()
    }

    private fun getPlayerInfo() {
        val sharedPreferences= getSharedPreferences("token", Context.MODE_PRIVATE)
        playerName = sharedPreferences.getString("playerName", "")!!
        token = sharedPreferences.getString("token", "")!!
    }

    private fun mapDocumentsToGames(querySnapshot: QuerySnapshot): List<OneVsOneGame> {
        return querySnapshot.map { document ->
            OneVsOneGame(document.id, document.data)
        }
    }

    companion object {
        const val REQUEST_PLAY_GAME = 121
    }
}
