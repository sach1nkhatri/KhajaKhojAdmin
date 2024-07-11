package com.example.khajakhojadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.khajakhojadmin.activity.AddAd
import com.example.khajakhojadmin.activity.AddCoupons
import com.example.khajakhojadmin.activity.AddRestaurant
import com.example.khajakhojadmin.activity.DeleteRestaurant

class Dashboard : AppCompatActivity() {

    lateinit var addRestaurant: Button
    lateinit var addAdvertisment : Button
    lateinit var addCoupons : Button
    lateinit var deleteRestaurant: Button
    lateinit var logtBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        addRestaurant = findViewById(R.id.AddRestaurant)
        addAdvertisment = findViewById(R.id.CreateAd)
        addCoupons = findViewById(R.id.AddCoupons)
        deleteRestaurant= findViewById(R.id.DeleteRestro)
        logtBtn= findViewById(R.id.logoutBtn)

        addRestaurant.setOnClickListener {
            val intent = Intent(this@Dashboard, AddRestaurant::class.java)
            startActivity(intent)
        }
        addAdvertisment.setOnClickListener {
            val intent = Intent(this@Dashboard, AddAd::class.java)
            startActivity(intent)
        }
        addCoupons.setOnClickListener {
            val intent = Intent(this@Dashboard, AddCoupons::class.java)
            startActivity(intent)
        }
        deleteRestaurant.setOnClickListener {
            val intent = Intent(this@Dashboard, DeleteRestaurant::class.java)
            startActivity(intent)
        }
        logtBtn.setOnClickListener {
            val intent = Intent(this@Dashboard, LoginPage::class.java)
            startActivity(intent)
        }

        }
    }
