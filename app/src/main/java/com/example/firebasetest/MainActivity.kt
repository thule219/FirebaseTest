package com.example.firebasetest

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasetest.model.Product
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var selectedImg: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        btnUploadImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        btnInsert.setOnClickListener {
            if(etName.text.isEmpty()&&etType.text.isEmpty()&&etPrice.text.isEmpty()){
                Toast.makeText(this,"Please enter full information",Toast.LENGTH_LONG).show()
            }else if(selectedImg == null){
                Toast.makeText(this,"Please select image",Toast.LENGTH_LONG).show()
            }else{
                uploadData()
            }
        }
    }

    private fun uploadData() {
        val reference = storage.reference.child("Product").child(Date().time.toString())
        reference.putFile(selectedImg).addOnCompleteListener{
            if(it.isSuccessful){
                reference.downloadUrl.addOnSuccessListener { task ->
                    uploadInfo(task.toString())
                }
            }
        }
    }

    private fun uploadInfo(imgUrl: String) {
        val name = etName.text.toString()
        val price = etPrice.text.toString()
        val type = etType.text.toString()
        database = FirebaseDatabase.getInstance().getReference("User")
        val product = Product(name, type, price,imgUrl)
        val key = database.push().key
        if (key != null) {
            database.child(key).setValue(product).addOnSuccessListener {
                Toast.makeText(this, "Add successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show()
                }
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.data != null){
                selectedImg = data.data!!
                etImage.setImageURI(selectedImg)

            }
        }
    }
}


//        btnInsert.setOnClickListener {
//            val name = etName.text.toString()
//            val price = etPrice.text.toString()
//            val type = etType.text.toString()
//
//
//            database = FirebaseDatabase.getInstance().getReference("User")
//            val product = Product(name, type, price)
//            val key = database.push().key
//            if (key != null) {
//                database.child(key).setValue(product).addOnSuccessListener {
//
//                    Toast.makeText(this, "Add successfully", Toast.LENGTH_SHORT).show()
//                }.addOnFailureListener {
//                    Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }