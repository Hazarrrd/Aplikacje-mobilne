package com.example.tictactoe

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.content.Context
import kotlinx.android.synthetic.main.activity_new_game.*


class NewGameActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var playerListAdapter: RecyclerView.Adapter<*>
    private lateinit var listLayoutManager: RecyclerView.LayoutManager
    private var playersList = mutableListOf<FirebasePlayer>()
    private lateinit var selectedPlayer: FirebasePlayer
    private lateinit var currentPlayerName: String
    private lateinit var currentPlayerToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        getPlayerInfo()
        loadOpponents()
        recyclerView = opponentsView
        listLayoutManager = LinearLayoutManager(this)
        playerListAdapter = PlayersListAdapter(playersList, this) { player -> onItemClick(player) }
        recyclerView = opponentsView.apply {
            layoutManager = listLayoutManager
            adapter = playerListAdapter
        }
        playerListAdapter.notifyDataSetChanged()
    }

    private fun onItemClick(player: FirebasePlayer) {
        selectedPlayer = player
        opponentListLabel.text = "Opponent: ${player.name}"
    }

    fun onSubmit(view: View) {
        FirestoreHandler().createGame(
            gameNameEditText.text.toString(),
            Player(currentPlayerName, currentPlayerToken, 0),
            Player(selectedPlayer.name, selectedPlayer.token, 0)
        )
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun loadOpponents() {
        FirestoreHandler().getAllPlayers()
            .addOnSuccessListener { result ->
                playersList.addAll(result.documents
                    .map { document -> document.toObject(FirebasePlayer::class.java)!! }
                    .filter { player -> player.name != currentPlayerName }
                )
            }
            .addOnFailureListener { error -> error.printStackTrace() }
    }

    private fun isEveryFieldFilled(): Boolean {
        return gameNameEditText.text.isNotBlank()
            && ::selectedPlayer.isInitialized
    }

    private fun getPlayerInfo() {
        val sharedPreferences= getSharedPreferences("token", Context.MODE_PRIVATE)
        currentPlayerName = sharedPreferences.getString("playerName", "")!!
        currentPlayerToken = sharedPreferences.getString("token", "")!!
    }
}
