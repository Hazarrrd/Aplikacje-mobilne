package com.example.asklikethat.login.databaseArchitecture

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

/**
 * Repozytorium dla naszej bazy danych z kontami
 * (coś jakby interfejs dla naszej aplikacji, żeby nie komunikować się z nią bezpośrednio)
 * zawiera wrappery wszystkich funkcji DAO oraz obiekt allAccounts()
 */
class UserAccountRepository(application: Application) {
    val database: UserAccountDatabase = UserAccountDatabase.getDatabase(application)
    val userAccountDAO: UserAccountDAO = database.userAccountDao()
    val allAccounts: LiveData<List<UserAccount>> = userAccountDAO.getAllAccounts()

    fun insert(userAccount: UserAccount){
        InsertUserAsyncTask(userAccountDAO).execute(userAccount)
    }

    fun delete(userAccount: UserAccount){
        DeleteUserAsyncTask(userAccountDAO).execute(userAccount)
    }

    fun update(userAccount: UserAccount){
        UpdateUserAsyncTask(userAccountDAO).execute(userAccount)
    }

    //Poza usuwaniem od razu czyści pamięć (skubany 2 w 1)
    fun deleteAll(){
        DeleteAllUsersAsyncTask(userAccountDAO).execute()
    }

    //Wątki do obsługi zapytań na bazę danych (UI nie pozwoli na zapytania z wątka głównego, bo mogą byc czasochłonne)
    private class InsertUserAsyncTask(val userAccountDAO: UserAccountDAO): AsyncTask<UserAccount, Void, Void>(){
        override fun doInBackground(vararg params: UserAccount): Void? {
            userAccountDAO.insert(params[0])
            return null
        }
    }

    private class DeleteUserAsyncTask(val userAccountDAO: UserAccountDAO): AsyncTask<UserAccount, Void, Void>(){
        override fun doInBackground(vararg params: UserAccount): Void? {
            userAccountDAO.delete(params[0])
            return null
        }
    }

    private class UpdateUserAsyncTask(val userAccountDAO: UserAccountDAO): AsyncTask<UserAccount, Void, Void>(){
        override fun doInBackground(vararg params: UserAccount): Void? {
            userAccountDAO.update(params[0])
            return null
        }
    }

    private class DeleteAllUsersAsyncTask(val userAccountDAO: UserAccountDAO): AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void): Void? {
            userAccountDAO.dropAllAccountRecords()
            return null
        }
    }
}