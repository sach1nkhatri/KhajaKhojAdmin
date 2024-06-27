package com.example.khajakhojadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
