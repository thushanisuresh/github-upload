package com.example.buunytoys.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buunytoys.R
import com.example.buunytoys.adapter.ProductAdapter
import com.example.buunytoys.model.ModelProduct
import com.example.buunytoys.model.ModelUser
import com.google.firebase.firestore.FirebaseFirestore

class ResultsActivity : AppCompatActivity(){

    private var recyclerView: RecyclerView? = null
    private var productAdapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        load_toolbar()
        get_data()
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

    private fun load_toolbar() {
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById<View>(R.id.product_view_recycler) as RecyclerView
        recyclerView!!.visibility = View.GONE
        val toolbar = findViewById(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        toolbar.setNavigationIcon(R.drawable.menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Soft Toys Shop"
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
    var products: List<ModelProduct> = ArrayList()
    var database = FirebaseFirestore.getInstance()

    private fun get_data() {
        database.collection("PRODUCT_TABLE").get().addOnSuccessListener { queryDocumentSnapshots ->
            products = queryDocumentSnapshots.toObjects(ModelProduct::class.java)
            initComponents()
        }.addOnFailureListener { initComponents() }
    }

    var progressBar: ProgressBar? = null
    private fun initComponents() {
        recyclerView!!.layoutManager = GridLayoutManager(this, 2)
        //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        progressBar!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        productAdapter = ProductAdapter(products, this, "0")
        recyclerView!!.adapter = productAdapter
        productAdapter!!.SetOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, obj: ModelProduct?, pos: Int) {
                val i = Intent(this@ResultsActivity, ViewProductActivity::class.java)
                i.putExtra("p_id", obj?.product_id)
                this@ResultsActivity.startActivity(i)


            }
        })
    }

}
