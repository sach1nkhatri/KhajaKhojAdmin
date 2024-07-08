package com.example.khajakhojadmin.repository

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khajakhojadmin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class TestActivity : AppCompatActivity() {
    private var fileUri: Uri? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private val menuItems = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(menuItems)
        recyclerView.adapter = menuAdapter

        val chooseFileButton = findViewById<Button>(R.id.chooseFileButton)
        val uploadFileButton = findViewById<Button>(R.id.uploadFileButton)

        chooseFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/json"
            startActivityForResult(intent, 1)
        }

        uploadFileButton.setOnClickListener {
            fileUri?.let {
                readJsonAndUploadToFirebase(it)
            } ?: Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            fileUri = data?.data
        }
    }

    private fun readJsonAndUploadToFirebase(fileUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            reader.forEachLine { line ->
                stringBuilder.append(line)
            }
            val jsonString = stringBuilder.toString()

            val jsonObject = JSONObject(jsonString)
            val restaurantId = jsonObject.getString("restaurantId")
            val menuItemsArray = jsonObject.getJSONArray("menuItems")

            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("menus/$restaurantId")

            // Convert JSONArray to List of Maps
            val menuItemsList = mutableListOf<Map<String, Any>>()
            for (i in 0 until menuItemsArray.length()) {
                val item = menuItemsArray.getJSONObject(i)
                val menuItemMap = mapOf(
                    "name" to item.getString("name"),
                    "description" to item.getString("description"),
                    "price" to item.getString("price")
                )
                menuItemsList.add(menuItemMap)
            }

            databaseRef.setValue(menuItemsList)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                    fetchMenuItemsFromFirebase(restaurantId)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Data upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchMenuItemsFromFirebase(restaurantId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("menus/$restaurantId")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                snapshot.children.forEach { itemSnapshot ->
                    val name = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = itemSnapshot.child("description").getValue(String::class.java) ?: ""
                    val price = itemSnapshot.child("price").getValue(String::class.java) ?: ""
                    val menuItem = MenuItem(name, description, price.toDouble())
                    menuItems.add(menuItem)
                }
                menuAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TestActivity, "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}