package com.example.khajakhojadmin

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.khajakhojadmin.databinding.ActivityAddRestaurantBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class AddRestaurant : AppCompatActivity() {
    private var fileUri: Uri? = null
    private var profileImageUri: Uri? = null
    private lateinit var binding: ActivityAddRestaurantBinding

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference.child("restaurant_logo_uri")

    private var wifiAvailability: Boolean = false
    private var bikeParking: Boolean = false
    private var carParking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Activity created")

        val database = FirebaseDatabase.getInstance().reference.child("restaurants")
        val restaurantId = database.push().key
        Log.d(TAG, "Generated restaurantId: $restaurantId")

        binding.chooseFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/json"
            }
            startActivityForResult(intent, 1)
            Log.d(TAG, "Starting file picker intent")
        }

        binding.createRestaurantButton.setOnClickListener {
            fileUri?.let {
                if (restaurantId != null) {
                    Log.d(TAG, "Starting profile image upload and restaurant creation")
                    uploadProfileImageAndSignUp(restaurantId)
                    readJsonAndUploadToFirebase(it, restaurantId)
                }
            } ?: Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show()
        }

        binding.addRestaurantLogo.setOnClickListener {
            Log.d(TAG, "Opening Gallery")
            openGallery()
        }

        binding.openTimePicker.setOnClickListener {
            timePickerDialog(binding.openTimePicker)
        }

        binding.closeTimePicker.setOnClickListener {
            timePickerDialog(binding.closeTimePicker)
        }

        binding.wifiSwitch.setOnCheckedChangeListener { _, isChecked ->
            wifiAvailability = isChecked
            Log.d(TAG, "Wi-Fi Availability set to: $isChecked")
        }

        binding.bikeSwitch.setOnCheckedChangeListener { _, isChecked ->
            bikeParking = isChecked
            Log.d(TAG, "Bike Parking set to: $isChecked")
        }

        binding.carSwitch.setOnCheckedChangeListener { _, isChecked ->
            carParking = isChecked
            Log.d(TAG, "Car Parking set to: $isChecked")
        }

        setupCuisineSpinner()
    }

    private fun setupCuisineSpinner() {
        val cuisineSpinner: Spinner = binding.cuisineSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.cuisine_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cuisineSpinner.adapter = adapter
        }
    }

    private fun timePickerDialog(view: View) {
        val timePicker = TimePickerDialog(
            this@AddRestaurant,
            { _, hourOfDay, minute ->
                val formattedTime = formatTime(hourOfDay, minute)
                when (view.id) {
                    R.id.openTimePicker -> binding.openTimePicker.text = formattedTime
                    R.id.closeTimePicker -> binding.closeTimePicker.text = formattedTime
                }
            },
            12, 0, false
        )
        timePicker.show()
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        var hour = hourOfDay
        val amPm: String
        if (hour >= 12) {
            amPm = "PM"
            if (hour > 12) hour -= 12
        } else {
            amPm = "AM"
            if (hour == 0) hour = 12
        }
        return String.format("%02d:%02d %s", hour, minute, amPm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fileUri = data?.data
            Log.d(TAG, "Selected JSON file: $fileUri")
        }
    }

    private fun uploadProfileImageAndSignUp(restaurantId: String) {
        profileImageUri?.let { uri ->
            val fileName = "${System.currentTimeMillis()}.jpg"
            val fileRef = storageReference.child(fileName)
            Log.d(TAG, "Starting image upload to Firebase Storage")
            fileRef.putFile(uri)
                .addOnSuccessListener {
                    Log.d(TAG, "Image uploaded successfully")
                    fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val profilePictureUrl = downloadUrl.toString()
                        storeRestaurantDataInFirebase(profilePictureUrl, restaurantId)
                    }.addOnFailureListener {
                        Log.d(TAG, "Failed to get download URL: ${it.message}")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload image: ${it.message}")
                }
        }
    }

    private fun storeRestaurantDataInFirebase(profilePictureUrl: String, restaurantId: String) {
        val restaurantName: String = binding.restaurantName.text.toString()
        val restaurantPhoneNumber: String = binding.restaurantPhoneNumber.text.toString()
        val restaurantAddressLink: String = binding.restaurantAddress.text.toString()
        val restaurantCuisine: String = binding.cuisineSpinner.selectedItem.toString()
        val openTime: String = binding.openTimePicker.text.toString()
        val closeTime: String = binding.closeTimePicker.text.toString()
        val bikeParking: Boolean = binding.bikeSwitch.isChecked
        val carParking: Boolean = binding.carSwitch.isChecked
        val wifiAvailability: Boolean = binding.wifiSwitch.isChecked
        val rating: Double = 3.0 // Admin handles ratings separately
        Log.d(TAG, "Restaurant data: $restaurantName, $restaurantPhoneNumber, $restaurantAddressLink, $restaurantCuisine, $openTime, $closeTime, $bikeParking, $carParking, $wifiAvailability")
        Log.d(TAG, "Profile picture URL: $profilePictureUrl")
        Log.d(TAG, "Restaurant ID: $restaurantId")


        val restaurant = mapOf(
            "id" to restaurantId,
            "name" to restaurantName,
            "address" to restaurantAddressLink,
            "cuisineType" to restaurantCuisine,
            "openTime" to openTime,
            "closeTime" to closeTime,
            "contactNumber" to restaurantPhoneNumber,
            "bikeParking" to bikeParking,
            "carParking" to carParking,
            "wifi" to wifiAvailability,
            "restaurantLogoUrl" to profilePictureUrl,
            "location" to null,
            "rating" to rating // You might want to handle ratings separately
        )

        firebaseDatabase.reference.child("restaurants").child(restaurantId).setValue(restaurant)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Restaurant data stored in Firebase successfully")
                    Toast.makeText(this, "Restaurant added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "Failed to store restaurant data in Firebase: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to add restaurant", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                profileImageUri = data?.data
                profileImageUri?.let {
                    Picasso.get().load(it).into(binding.addRestaurantLogo)
                    Log.d(TAG, "Selected image displayed in ImageView")
                }
            }
        }

    private fun readJsonAndUploadToFirebase(fileUri: Uri, restaurantId: String) {
        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val stringBuilder = StringBuilder()
            reader.forEachLine { line ->
                stringBuilder.append(line)
            }
            val jsonString = stringBuilder.toString()
            val jsonArray = JSONArray(jsonString) // Parse as JSONArray instead of JSONObject

            val databaseRef = firebaseDatabase.getReference("menus/$restaurantId")

            val menuItemsList = mutableListOf<Map<String, Any>>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i) // Get each object from the array
                val menuItemMap = mapOf(
                    "name" to item.getString("name"),
                    "description" to item.getString("description"),
                    "price" to item.getString("price")
                )
                menuItemsList.add(menuItemMap)
            }

            databaseRef.setValue(menuItemsList)
                .addOnSuccessListener {
                    Log.d(TAG, "Menu items uploaded to Firebase successfully")
                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload menu items to Firebase: ${it.message}")
                    Toast.makeText(this, "Data upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Error reading file: ${e.message}")
            Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "AddRestaurantActivity"
    }
}
