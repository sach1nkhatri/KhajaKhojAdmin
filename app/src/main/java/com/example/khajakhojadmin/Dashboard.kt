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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        addRestaurant = findViewById(R.id.AddRestaurant)


        addRestaurant.setOnClickListener {
            val intent = Intent(this@Dashboard, AddRestaurant::class.java)
            startActivity(intent)
        }


        }
    }
