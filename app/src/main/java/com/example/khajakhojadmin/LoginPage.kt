package com.example.khajakhojadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.khajakhojadmin.databinding.ActivityAddCouponsBinding
import com.example.khajakhojadmin.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "LoginPage"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            Log.i(TAG, "username: $username and Password : $password")

            // Check if both fields are not empty
//            if (username.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            if (username.isNotEmpty() && password.isNotEmpty()) {
//                signInWithEmailAndPassword(username, password)
            }
            val intent = Intent(this, Dashboard::class.java) // Use appContext
            startActivity(intent)
            finish()

        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        val appContext = this@LoginPage.applicationContext // Get application context

        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                withContext(Dispatchers.Main) {
                    val intent = Intent(appContext, Dashboard::class.java) // Use appContext
                    startActivity(intent)
                    finish()
                    Toast.makeText(appContext, "Login Successful", Toast.LENGTH_SHORT)
                        .show() // Use appContext
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Invalid email or password", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "User already exists", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Login failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}