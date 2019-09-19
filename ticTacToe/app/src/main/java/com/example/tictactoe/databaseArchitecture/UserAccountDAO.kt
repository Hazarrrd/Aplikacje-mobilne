package com.example.tictactoe.databaseArchitecture

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 * DAO = Data Access Object
 * Tutaj dodajemy jakieś operacje które będziemy chcieli wykonywać na rekordach tablicy i samej tablicy
 */
@Dao
interface UserAccountDAO {
    @Insert
    fun insert(userAccount: UserAccount)

    @Delete
    fun delete(userAccount: UserAccount)

    @Update
    fun update(userAccount: UserAccount)

    @Query("DELETE FROM user_accounts")
    fun dropAllAccountRecords()

    @Query("SELECT * FROM user_accounts")
    fun getAllAccounts(): LiveData<List<UserAccount>>
}