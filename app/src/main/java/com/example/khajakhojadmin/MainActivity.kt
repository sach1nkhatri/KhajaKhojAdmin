package com.example.khajakhojadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Initialize progress bar
        progressBar = findViewById(R.id.progressBar)

        // Start updating progress after a delay
        Handler().postDelayed({
            updateProgress()
        }, 10)
    }

    private fun updateProgress() {
        val thread = Thread {
            while (progressStatus < 100) {
                try {
                    Thread.sleep(30)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progressStatus++
                progressBar.progress = progressStatus
            }
            // When progress reaches 100%, start the next activity
            val intent = Intent(this@MainActivity, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
        thread.start()
    }
}