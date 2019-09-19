package com.example.tictactoe

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.tictactoe.databaseArchitecture.UserAccount
import com.example.tictactoe.databaseArchitecture.UserAccountViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    private lateinit var userAccountViewModel: UserAccountViewModel
    private val REGISTER_REQUEST_CODE = 9
    private val LOGIN_REQUEST_CODE = 1
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Picasso.with(this) // Context
            .load("https://cdn.sixthandi.org/wp/wp-content/uploads/2019/01/MansAttemptAtMindfulness.jpg") // URL or file
            .into(image2);

        userAccountViewModel = ViewModelProviders.of(this).get(UserAccountViewModel::class.java)
        userAccountViewModel.allUserAccounts.observe(this, Observer<List<UserAccount>> {})

        auth = FirebaseAuth.getInstance()
    }



    fun login(v: View){
      /*  var broken = false
        val allUserAccounts: ArrayList<UserAccount> = userAccountViewModel.allUserAccounts.value as ArrayList<UserAccount>
        for(account in allUserAccounts){
            if(account.login.compareTo(letUsername.text.toString()) == 0){
                if(account.password.compareTo(letPassword.text.toString().sha512()) == 0){
                    val sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit().apply {
                        putString("token", "")
                        putString("playerName", account.login)
                    }
                    editor.apply()
                    val intent: Intent = Intent(this, BrowseGamesActivity::class.java)
                        //putExtra("CURRENT_USER", account)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
                broken = true
                break
            }
        }
        if(!broken)
            Toast.makeText(applicationContext, "Incorrect username", Toast.LENGTH_SHORT).show()
*/
        val docRef = db.collection("players").document(letUsername.text.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                var userData = document.data
                if (document.data != null) {
                    if(userData!!["password"].toString().compareTo(letPassword.text.toString().sha512()) == 0){
                        val sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit().apply {
                            putString("token", "")
                            putString("playerName", letUsername.text.toString())
                        }
                        editor.apply()
                        val intent: Intent = Intent(this, BrowseGamesActivity::class.java)
                        //putExtra("CURRENT_USER", account)
                        startActivity(intent)
                    }else{
                        Toast.makeText(applicationContext, "Nieprawidłowe hasło", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(applicationContext, "Nieprawidłowy login", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, "Something wrong", Toast.LENGTH_SHORT).show()
            }
    }

    fun register(v: View){
        var intent: Intent = Intent(this, RegisterActivity::class.java)
            .putExtra("ACCOUNTS", userAccountViewModel.allUserAccounts.value as ArrayList<UserAccount>)
        startActivityForResult(intent, REGISTER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REGISTER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data == null){
                Toast.makeText(applicationContext, "No data received from register activity", Toast.LENGTH_SHORT).show()
            }else{
                val accountString = data.getStringExtra("NEW_ACCOUNT").split(";-")
                DoAsync {
                    FirestoreHandler().createPlayer(
                        accountString[0],
                        "",
                        accountString[2]
                    )
                }
             /*   db.collection("players")
                    .add(mapOf("name" to accountString[0], "token" to ""))
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(applicationContext, "jest jest ", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                    }*/


                userAccountViewModel.insert(
                    UserAccount(
                        accountString[0],
                        accountString[1],
                        accountString[2],
                        accountString[3],
                        accountString[4],
                        null
                    ))
            }
        }
    }

    fun String.sha512(): String {
        var toHash = this + "f1nd1ngn3m0"
        return toHash.hashWithAlgorithm("SHA-512")
    }

    private fun String.hashWithAlgorithm(algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        val bytes = digest.digest(this.toByteArray(Charsets.UTF_8))
        return bytes.fold("", { str, it -> str + "%02x".format(it) })
    }

    class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        init {
            execute()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
}
