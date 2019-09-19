package com.example.tictactoe.databaseArchitecture

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * klasa reprezentująca samą bazę danych
 * (zawiera między innymi wrappery do operacji na rekordach)
 */
@Database(entities = [UserAccount::class], version = 1)
abstract class UserAccountDatabase: RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDAO
    companion object{
        @Volatile
        private var INSTANCE: UserAccountDatabase? = null

        //Singleton budujący bazę danych jeżeli nie istnieje (to skomplikowane xD)
        fun getDatabase(context: Context): UserAccountDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserAccountDatabase::class.java,
                    "user_accounts")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}