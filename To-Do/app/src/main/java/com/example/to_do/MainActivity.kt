package com.example.to_do

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders

import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.asklikethat.login.databaseArchitecture.UserAccount
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.Observer
import android.os.AsyncTask
import com.example.asklikethat.login.databaseArchitecture.UserAccountViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast

import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    var array = arrayListOf<data>()
    var listView: ListView? = null;
    var adapter : MyAdapter? = null;
    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var userAccountViewModel: UserAccountViewModel
    private var counter = 0
   // val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
   // private var mydate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userAccountViewModel = ViewModelProviders.of(this).get(UserAccountViewModel::class.java)
        userAccountViewModel.allUserAccounts.observe(this, Observer<List<UserAccount>> {})

        //userAccountViewModel.insert(UserAccount(2, "xx", "xxx","1",counter))




        listView = findViewById(R.id.listView);
        adapter = MyAdapter(
            this, array
        );

        listView!!.setAdapter(adapter)
        listView!!.onItemLongClickListener = object : AdapterView.OnItemLongClickListener {

            override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {

                // value of item that is clicked


                // Toast the values
                Toast.makeText(
                    applicationContext,
                    "Zadanie usunięte", Toast.LENGTH_LONG

                )
                    .show()
                var id = array[position].queue
                val allUserAccounts: ArrayList<UserAccount> = userAccountViewModel.allUserAccounts.value as ArrayList<UserAccount>
                for(account in allUserAccounts){
                    if(account.queue.compareTo(id) == 0){

                        userAccountViewModel.delete(account)

                        break
                    }
                }

                array.removeAt(position)

                Thread({
                    Thread.sleep(100)
                    runOnUiThread {
                        adapter!!.notifyDataSetChanged();
                    }

                }).start()
                return true;
            }



        }


        val myspinner = ArrayAdapter.createFromResource(
            this,
            R.array.filtr, android.R.layout.simple_spinner_item
        )
        myspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myspinner)

        val btn = findViewById(R.id.button3) as Button

        btn.setOnClickListener {
            //val intent = Intent(this, SecondActivity::class.java)
            startActivityForResult(Intent(this, SecondActivity::class.java), 1)
            // startActivity(Intent(this, SecondActivity::class.java))
        }

        Thread({
            Thread.sleep(100)
                val allUserAccounts: ArrayList<UserAccount> =
                    userAccountViewModel.allUserAccounts.value as ArrayList<UserAccount>
            if(allUserAccounts.size > 0) {
                counter = allUserAccounts.last().queue + 1
                // array.removeAll(array)
                for (account in allUserAccounts) {
                    array.add(data(account.priority, account.time, account.text, account.image, account.queue))
                }
                runOnUiThread {
                    adapter!!.notifyDataSetChanged();
                }
            }

        }).start()

    }

    fun sortIt(view: View) {
        when (spinner.selectedItem.toString()) {
            "Dodanie" -> array.sortBy { it.queue }
            "Data" -> array.sortBy { it.time }
            "Priorytet" -> array.sortBy { it.priority }
            "Obrazek" -> array.sortBy { it.image }
        }
        adapter!!.notifyDataSetChanged();
        Toast.makeText(this, "Posortowano według : " + spinner.selectedItem.toString() , Toast.LENGTH_SHORT).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            userAccountViewModel.insert(
                UserAccount(
                    data!!.extras!!.getInt("priority"), data!!.extras!!.getString("date"), data.extras!!.getString("note"),data.extras!!.getString("image"),counter
                ))

            array.add(data(data!!.extras!!.getInt("priority"), data!!.extras!!.getString("date"), data.extras!!.getString("note"),data.extras!!.getString("image"),counter))

            Thread({
                Thread.sleep(100)
                runOnUiThread {
                    adapter!!.notifyDataSetChanged();
                }

            }).start()

            counter ++;
            Toast.makeText(this, "Dodano zadanie", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(this, "Nie dodano zadania", Toast.LENGTH_SHORT).show()
    }



}
