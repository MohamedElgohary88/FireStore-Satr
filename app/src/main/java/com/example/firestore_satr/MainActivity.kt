package com.example.firestore_satr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val userCollectionRef = FirebaseFirestore.getInstance().collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        update.setOnClickListener {
            val user = getOldUserData()
            val newUserMap = getNewUserData()
            updateUserData(user,newUserMap)
            Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show()
        }

        add.setOnClickListener {
            val user = getOldUserData()
            saveUser(user)
        }

        retrieve.setOnClickListener {
            retrieveData()
        }

        delete.setOnClickListener {
            val user = getOldUserData()
            deleteUserData(user)
            Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show()
        }

    }


    private fun deleteUserData(user: User){
        userCollectionRef
            .whereEqualTo("firstName",user.firstName)
            .whereEqualTo("lastName",user.lastName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (task.result!!.documents.isNotEmpty()){
                        for (document in task.result.documents){
                            userCollectionRef.document(document.id).delete()
                        }
                    }else{
                        Toast.makeText(this,"No matching documents",Toast.LENGTH_SHORT).show()
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }

    }


    private fun getNewUserData():Map<String,Any>{
        val firstName = new_first_name.text.toString()
        val lastName = new_last_name.text.toString()

        val map = mutableMapOf<String,Any>()
        if (firstName.isNotEmpty()){
            map["firstName"]=firstName
        }
        if (lastName.isNotEmpty()){
            map["lastName"]=lastName
        }
        return map
    }


    private fun updateUserData(user: User,newUserMap:Map<String,Any>){
        userCollectionRef
            .whereEqualTo("firstName",user.firstName)
            .whereEqualTo("lastName",user.lastName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (task.result!!.documents.isNotEmpty()){
                        for (document in task.result.documents){
                            userCollectionRef.document(document.id).set(
                                newUserMap , SetOptions.merge()
                            )
                        }
                    }else{
                        Toast.makeText(this,"No matching documents",Toast.LENGTH_SHORT).show()
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }

    }

    private fun getOldUserData(): User {
        val firstName = first_name.text.toString()
        val lastName = last_name.text.toString()

        return User(firstName, lastName)
    }


    private fun retrieveData (){
        userCollectionRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val sb = StringBuilder()
                for (document in task.result!!.documents){
                    val user = document.toObject<User>()
                    sb.append("$user \n")
                }
                findViewById<TextView>(R.id.tv_user).text = sb
                Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            println(it.message)
        }
    }

    private fun saveUser(user: User) {
        userCollectionRef.add(user).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            println(it.message)
        }
    }
}