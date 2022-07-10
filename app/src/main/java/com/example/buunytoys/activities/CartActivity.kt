package com.example.buunytoys.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.example.buunytoys.R
import com.example.buunytoys.adapter.ProductAdapter
import com.example.buunytoys.model.ModelCart
import com.example.buunytoys.model.ModelOrder
import com.example.buunytoys.model.ModelProduct
import com.example.buunytoys.model.ModelUser
import com.example.buunytoys.utility.Tools
import org.w3c.dom.Text
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    var total_price : TextView? = null
    var total = 0

    var logged_user: ModelUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        logged_user = Tools.get_signed_in_user()
        if (logged_user == null){
            Toast.makeText(this, "You not logged", Toast.LENGTH_SHORT).show()
            val i = Intent(this, LoginActivity::class.java)
            this.startActivity(i)
            finish()
        }else{
            loadToolbar()
            fetch_cart_data()
        }

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

    var modelCart: List<ModelCart>? = null
    var product: MutableList<ModelProduct> = ArrayList<ModelProduct>()
    private fun fetch_cart_data() {
        modelCart = try {
            ModelCart.listAll(ModelCart::class.java)
        }catch (e: Exception){
            Toast.makeText(this, "Failed to get data" + e.message, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (modelCart == null){
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        total = 0

        for(cart in modelCart!!){
            val product_list = ModelProduct()

            product_list.product_name = cart.product_name
            product_list.category = ""
            product_list.description = ""
            product_list.quantity = cart.quantity.toString()
            product_list.price = cart.product_price.toString()
            product_list.product_id = cart.product_id
            product_list.product_image = cart.product_image

            product.add(product_list)
            total += cart.product_price
        }

        total_price = findViewById<TextView>(R.id.total_price)
        total_price?.setText(total.toString())

        load_data()

    }

    var recycler: RecyclerView? = null
    private var cartAdapter: ProductAdapter? = null
    var place_order: Button? = null
    private fun load_data() {
        recycler = findViewById(R.id.cart)
        recycler?.setLayoutManager(LinearLayoutManager(this))
        recycler?.setHasFixedSize(true)
        recycler?.setNestedScrollingEnabled(false)
        cartAdapter = ProductAdapter(product, this, "1")
        recycler?.setAdapter(cartAdapter)
        place_order = findViewById(R.id.submit_order)
        place_order?.setOnClickListener(View.OnClickListener {
            placeOrder()
        })

    }

    var database = FirebaseFirestore.getInstance()
    var progress: ProgressDialog? = null
    private fun placeOrder() {
        val modelOrder = ModelOrder()
        modelOrder.order_id = database.collection("ORDERS").document().id
        modelOrder.customer = logged_user!!
        modelOrder.cart = modelCart!!

        database.collection("ORDERS").document(modelOrder.order_id).set(modelOrder)
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                    try {
                        ModelCart.deleteAll(ModelCart::class.java)
                    }catch (e: Exception){
                        Toast.makeText(this, "Failed to remove items from cart", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                    return@OnSuccessListener
                }
            ).addOnFailureListener(
                OnFailureListener { e ->
                    Toast.makeText(this, "Order not placed " + e.message, Toast.LENGTH_SHORT).show()
                    return@OnFailureListener
                }
            )
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
}
