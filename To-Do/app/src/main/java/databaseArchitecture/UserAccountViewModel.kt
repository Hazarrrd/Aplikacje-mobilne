package com.example.asklikethat.login.databaseArchitecture

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

/**
 * ViewModel wrappujący wszystkie funkcje z repozytorium
 */
class UserAccountViewModel(application: Application): AndroidViewModel(application) {
    private val repository: UserAccountRepository = UserAccountRepository(application)
    val allUserAccounts: LiveData<List<UserAccount>> = repository.allAccounts

    fun insert(userAccount: UserAccount){
        repository.insert(userAccount)
    }

    fun delete(userAccount: UserAccount){
        repository.delete(userAccount)
    }

    fun update(userAccount: UserAccount){
        repository.update(userAccount)
    }

    fun dropAllAccounts(){
        repository.deleteAll()
    }
}