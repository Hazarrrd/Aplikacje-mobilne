package com.example.tictactoe

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.tictactoe.databaseArchitecture.UserAccount
import kotlinx.android.synthetic.main.activity_register.*
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {
    private lateinit var accounts: ArrayList<UserAccount>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        accounts = intent.getSerializableExtra("ACCOUNTS") as ArrayList<UserAccount>
    }

    fun cancel(v: View){
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    fun register(v: View){
        if(retUsername.text.toString().compareTo("") == 0 ||
            retPassword.text.toString().compareTo("") == 0 ||
            retConfirmPassword.text.toString().compareTo(retPassword.text.toString()) != 0){
            Toast.makeText(applicationContext, "Incorrect Data", Toast.LENGTH_SHORT).show()
        }else{
            var alreadyUsed = false
            for (account in accounts){
                if(account.login.compareTo(retUsername.text.toString()) == 0){
                    alreadyUsed = true
                    Toast.makeText(applicationContext, "Username already in use", Toast.LENGTH_SHORT).show()
                    break
                }/*else if(account.email.compareTo(retEmail.text.toString()) == 0){
                    alreadyUsed = true
                    Toast.makeText(applicationContext, "Email already in use", Toast.LENGTH_SHORT).show()
                    break
                }*/
            }
            if(!alreadyUsed) {
                val newAccount = String.format("%s;-%s;-%s;-%s;-%s", retUsername.text.toString(), "No email implemented", retPassword.text.toString().sha512()
                ,"This user didn't write description", "0")
                intent.putExtra("NEW_ACCOUNT", newAccount)
                setResult(Activity.RESULT_OK, intent)
                finish()
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
}
