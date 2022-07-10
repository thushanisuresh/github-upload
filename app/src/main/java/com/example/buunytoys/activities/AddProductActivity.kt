package com.example.buunytoys.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.buunytoys.R
import com.example.buunytoys.model.ModelProduct
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class AddProductActivity : AppCompatActivity() {

    var product_name: EditText? = null
    var product_description: EditText? = null
    var product_category: EditText? = null
    var product_price: EditText? = null
    var product_quantity: EditText? = null
    var product_image: ImageView? = null
    var save_product: Button? = null

    private val IMAGE_REQUEST = 1
    var productModel: ModelProduct = ModelProduct()

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initComponent()
    }

    private fun initComponent() {

        productModel.product_id = db.collection("PRODUCT_TABLE").document().id
        product_name = findViewById(R.id.et_product_name)
        product_description = findViewById(R.id.et_description)
        product_category = findViewById(R.id.et_category)
        product_price = findViewById(R.id.et_price)
        product_quantity = findViewById(R.id.et_quantity)
        product_image = findViewById(R.id.iv_add_product_photo)
        save_product = findViewById(R.id.btn_save_product)

        product_image?.setOnClickListener(View.OnClickListener {
            selectImage()
        })

        save_product?.setOnClickListener(View.OnClickListener {
            save_item()
        })
    }

    private fun save_item() {
        productModel.product_name = product_name!!.text.toString()
        if (productModel.product_name.isEmpty()){
            Toast.makeText(this, "Fill product name", Toast.LENGTH_SHORT).show()
        }

        productModel.description =  product_description!!.text.toString()
        if (productModel.description.isEmpty()){
            Toast.makeText(this, "Fill product description", Toast.LENGTH_SHORT).show()
        }
        productModel.price = product_price!!.text.toString()

        if (productModel.price.isEmpty()){
            Toast.makeText(this, "Fill product price", Toast.LENGTH_SHORT).show()
        }

        productModel.quantity = product_quantity!!.text.toString()
        if (productModel.quantity.isEmpty()){
            Toast.makeText(this, "Fill product quantity", Toast.LENGTH_SHORT).show()
        }

        productModel.category = product_category!!.text.toString()
        if (productModel.category.isEmpty()){
            Toast.makeText(this, "Fill product category", Toast.LENGTH_SHORT).show()
        }

        if(imageurl == null){
            Toast.makeText(this, "Select Product Image", Toast.LENGTH_SHORT).show()
        }
        else {
            storage_ref = FirebaseStorage.getInstance().reference
            storage_ref?.child("products/" + productModel.product_id)?.putFile(imageurl!!)
                ?.addOnSuccessListener(
                    OnSuccessListener {
                        Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                        storage_ref!!.child("products/" + productModel.product_id).downloadUrl.addOnSuccessListener(
                            OnSuccessListener { uri ->
                                productModel.product_image = uri.toString()
                                save_data()
                                return@OnSuccessListener
                            }
                        ).addOnFailureListener(OnFailureListener {
                            productModel.product_image =
                                "https://www.pinterest.com/pin/230176230929946711/"
                            save_data()
                            return@OnFailureListener
                        })
                    }
                )?.addOnFailureListener(OnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    productModel.product_image =
                        "https://www.pinterest.com/pin/230176230929946711/"
                    save_data()
                    return@OnFailureListener
                })

        }
    }

    private fun save_data() {
        db.collection("PRODUCT_TABLE").document(productModel.product_id).set(productModel)
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(this,"Product saved", Toast.LENGTH_SHORT).show()
                    finish()
                    return@OnSuccessListener
                }
            ).addOnFailureListener(
                OnFailureListener {
                    Toast.makeText(this,"Product not saved", Toast.LENGTH_SHORT).show()
                }
            )
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST)
    }

    private var imageurl: Uri? = null
    var storage_ref: StorageReference? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageurl = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageurl)
                product_image?.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}