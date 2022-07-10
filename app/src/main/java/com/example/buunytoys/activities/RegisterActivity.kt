package com.example.buunytoys.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
import com.example.buunytoys.R
import com.example.buunytoys.model.ModelUser
import com.example.buunytoys.utility.Tools
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var modelUser = ModelUser()
    var signed_in_user: ModelUser? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        signed_in_user = Tools.get_signed_in_user()

        if (signed_in_user != null) {
            Toast.makeText(this, "you are logged", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        initComponent()
    }

        var first_name: EditText? = null
        var last_name: EditText? = null
        var email: EditText? = null
        var password: EditText? = null
        var confirm_password: EditText? = null
        var profile_photo: ImageView? = null
        var register: Button? = null

    fun initComponent() {
        modelUser.customer_id = db.collection("CUSTOMER_TABLE").document().id
        first_name = findViewById<EditText>(R.id.et_first_name)
        last_name = findViewById<EditText>(R.id.et_last_name)
        email = findViewById<EditText>(R.id.et_email)
        password = findViewById<EditText>(R.id.et_password)
        confirm_password = findViewById<EditText>(R.id.et_confirm_password)
        profile_photo = findViewById(R.id.iv_add_photo)
        register = findViewById(R.id.btn_register)

        register?.setOnClickListener(View.OnClickListener {
            validate()
            val i = Intent(this, MainActivity::class.java)
            this.startActivity(i)
        })

        profile_photo?.setOnClickListener(View.OnClickListener {
            selectImage()
        })
    }

        private fun selectImage() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE_REQUEST
            )
        }

        var imageurl: Uri? = null
        var storage_ref: StorageReference? = null

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                imageurl = data.data

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageurl)
                    profile_photo?.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        private fun validate() {
            modelUser.first_name = first_name!!.text.toString()
            if (modelUser.first_name.isEmpty()) {
                Toast.makeText(this, "first name cannot be empty", Toast.LENGTH_SHORT).show()
            }

            modelUser.last_name = last_name!!.text.toString()
            if (modelUser.last_name.isEmpty()) {
                Toast.makeText(this, "last name cannot be empty", Toast.LENGTH_SHORT).show()
            }

            modelUser.email = email!!.text.toString()
            if (modelUser.email.isEmpty()) {
                Toast.makeText(this, "email cannot be empty", Toast.LENGTH_SHORT).show()
            }

            modelUser.password = password!!.text.toString()
            if (modelUser.password.isEmpty()) {
                Toast.makeText(this, "password cannot be empty", Toast.LENGTH_SHORT).show()
            }

            modelUser.confirm_password = confirm_password!!.text.toString()
            if (modelUser.confirm_password.isEmpty()) {
                Toast.makeText(this, "Retype your password", Toast.LENGTH_SHORT).show()
            }


            storage_ref = FirebaseStorage.getInstance().reference
            storage_ref!!.child("customers/" + modelUser.customer_id).putFile(imageurl!!)
                .addOnSuccessListener(
                    OnSuccessListener {
                        Toast.makeText(this, "Uploaded successfully!", Toast.LENGTH_SHORT).show()

                        storage_ref!!.child("customers/" + modelUser.customer_id).downloadUrl.addOnSuccessListener(
                            OnSuccessListener { uri ->

                                modelUser.profile_photo = uri.toString()
                                send_data()

                                return@OnSuccessListener

                            }
                        ).addOnFailureListener(OnFailureListener {
                            modelUser.profile_photo =
                                "https://www.pexels.com/photo/person-taking-a-photo-using-iphone-36675/"
                            send_data()
                            return@OnFailureListener
                        })

                    }
                ).addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(this, "failed to upload photo " + e.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.d("err", e.message.toString())
                    modelUser.profile_photo =
                        "https://www.pexels.com/photo/person-taking-a-photo-using-iphone-36675/"
                    send_data()
                    return@OnFailureListener
                })
            }

    private fun send_data() {
        db.collection("CUSTOMER_TABLE").whereEqualTo("email", modelUser.email).get()
            .addOnSuccessListener(
                OnSuccessListener { queryDocumentSnapshots ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        Toast.makeText(
                            this,
                            "Email already exist",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@OnSuccessListener
                    }
                    modelUser.customer_id = db.collection("CUSTOMER_TABLE").document().id

                    db.collection("CUSTOMER_TABLE").document(modelUser.customer_id).set(modelUser)
                        .addOnSuccessListener(
                            OnSuccessListener {
                                Toast.makeText(this, "User Account Created Successfully",
                                    Toast.LENGTH_SHORT).show()

                                if(logged_user_to_sql()){
                                    Toast.makeText(this, "login successfully", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show()
                                }


                            }).addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Failed to create an account",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                }).addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to create an account",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }


    }

    private fun logged_user_to_sql(): Boolean {
        return try {
            ModelUser.save(modelUser)
            true
        }catch (e: Exception) {
            false
        }
    }
}






