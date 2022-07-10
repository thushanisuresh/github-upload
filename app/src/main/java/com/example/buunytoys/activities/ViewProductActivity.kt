package com.example.buunytoys.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.example.buunytoys.R
import com.example.buunytoys.model.ModelCart
import com.example.buunytoys.model.ModelProduct
import com.example.buunytoys.model.ModelUser
import com.example.buunytoys.utility.Tools
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class ViewProductActivity : AppCompatActivity() {

    var p_id: String? = null
    var logged_user: ModelUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        val i = intent
        p_id = i.getStringExtra("p_id")

        ctx = this

        loadToolbar()
        initComponent()
        fetch_data()
    }

    var product_title: TextView? = null
    var product_price: TextView? = null
    var product_details: TextView? = null
    var product_quantity: TextView? = null
    var user_quantity: EditText? = null
    var product_image: ImageView? = null
    var Add_cart: Button? = null

    private fun initComponent() {
        product_title = findViewById(R.id.product_title)
        product_price = findViewById(R.id.product_price)
        product_details = findViewById(R.id.product_info)
        product_quantity = findViewById(R.id.product_quantity)
        user_quantity = findViewById(R.id.user_qty)
        product_image = findViewById(R.id.product_image)
        Add_cart = findViewById(R.id.add_cart)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.root_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_product){
            val i = Intent(this, AddProductActivity::class.java)
            this.startActivity(i)
        }else if (item.itemId == R.id.menu_register){
            val i = Intent(this, RegisterActivity::class.java)
            this.startActivity(i)

        }else if (item.itemId == R.id.menu_login){
            val i = Intent(this, LoginActivity::class.java)
            this.startActivity(i)

        }else if (item.itemId == R.id.menu_cart){
            val i = Intent(this, CartActivity::class.java)
            this.startActivity(i)

        }else if (item.itemId == R.id.menu_category){
            val i = Intent(this, CategoryActivity::class.java)
            this.startActivity(i)

        }else if (item.itemId == R.id.menu_notification){
            val i = Intent(this, NotificationActivity::class.java)
            this.startActivity(i)

        }else if (item.itemId == R.id.menu_signout){
            try{
                ModelUser.deleteAll(ModelUser::class.java)
                Toast.makeText(this, "sign out successfully", Toast.LENGTH_SHORT).show()
                val i = Intent(this, MainActivity::class.java)
                this.startActivity(i)
            }catch (e: Exception){
                Toast.makeText(this,"logout unsuccessfully", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        toolbar.setNavigationIcon(R.drawable.menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Product Details"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSystemBarColor(this)
    }

    private fun setSystemBarColor(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    var product: ModelProduct? = ModelProduct()
    var database = FirebaseFirestore.getInstance()
    var ctx: Context? = null
    private fun fetch_data(){
        database.collection("PRODUCT_TABLE").document(p_id!!).get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()){
                    Toast.makeText(ctx, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@OnSuccessListener
                }
                product = documentSnapshot.toObject(ModelProduct::class.java)
                set_data()
            }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(ctx, "Data getting error" + e.message, Toast.LENGTH_SHORT).show()
                finish()
                return@OnFailureListener
            })
    }

    private fun set_data() {
        product_image?.let{
            Glide.with(ctx!!)
                .load(product?.product_image)
                .into(it)
        }

        product_title?.setText(product?.product_name)
        product_details?.setText(product?.description)
        product_price?.setText("Rs: "+product?.price)
        product_quantity?.setText("In Stock: "+product?.quantity)

        Add_cart?.setOnClickListener(View.OnClickListener {
            logged_user = Tools.get_signed_in_user()
            if (logged_user == null){
                Toast.makeText(ctx, "You not logged", Toast.LENGTH_SHORT).show()
                val i = Intent(this, LoginActivity::class.java)
                this.startActivity(i)
                finish()
            }else{
                val i = Intent(this, CartActivity::class.java)
                this.startActivity(i)
            }

        })
    }

    private fun add_cart(){
        var modelCart = ModelCart()

        modelCart.product_id = product!!.product_id
        modelCart.product_name = product!!.product_name
        modelCart.product_price = Integer.valueOf(product!!.price) * Integer.valueOf(user_quantity?.getText().toString())
        modelCart.quantity = Integer.valueOf(user_quantity?.getText().toString())
        modelCart.product_image = product!!.product_image

        try {
            ModelCart.save(modelCart)
            Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show()
        }
    }


}