package com.example.buunytoys.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.buunytoys.R
import com.example.buunytoys.model.ModelUser

class CategoryActivity : AppCompatActivity() {


    var Colour: ImageView? = null
    var Brand: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        onButtonClick()
    }

    private fun onButtonClick() {
        Colour = findViewById(R.id.colour)
        Brand = findViewById(R.id.brand)


        Colour?.setOnClickListener(View.OnClickListener {
            val  i = Intent(this, ResultsActivity::class.java)
            i.putExtra("cat", "Colour")
            this.startActivity(i)
        })

        Brand?.setOnClickListener(View.OnClickListener {
            val  i = Intent(this, ResultsActivity::class.java)
            i.putExtra("cat", "Brand")
            this.startActivity(i)
        })
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

}
