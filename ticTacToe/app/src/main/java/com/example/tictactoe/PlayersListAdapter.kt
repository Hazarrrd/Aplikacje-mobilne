package com.example.tictactoe

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tictactoe.FirebasePlayer
import com.example.tictactoe.R
import kotlinx.android.synthetic.main.player_list_item_layout.view.*

class PlayersListAdapter(
    private val dataset: List<FirebasePlayer>,
    val context: Context,
    private val clickListener: (FirebasePlayer) -> Unit
) : RecyclerView.Adapter<PlayersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.player_list_item_layout, parent, false))
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataset[position], clickListener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(player: FirebasePlayer, clickListener: (FirebasePlayer) -> Unit) {
            val playerNameTextView = itemView.playerNameTextView.apply {
                text = player.name
            }
            itemView.setOnClickListener { clickListener(player) }
        }
    }
}