package com.example.asklikethat.login.databaseArchitecture

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import java.io.Serializable

/**
 * Klasa odpowiadająca rekordowi z tablicy kont użytkownika
 */

@Entity(tableName = "user_accounts")
class UserAccount(
    val priority: Int,
    val time: String,
    val text: String,
    val image: String,
    val queue: Int
): Serializable {
    @PrimaryKey(autoGenerate = true)
    public var id: Int = 0
}