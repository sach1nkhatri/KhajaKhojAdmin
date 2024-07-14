package com.example.khajakhojadmin.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.khajakhojadmin.R
import com.example.khajakhojadmin.databinding.ActivityAddRestaurantBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class AddRestaurant : AppCompatActivity() {
    private var fileUri: Uri? = null
    private var profileImageUri: Uri? = null
    private val selectedImages = mutableListOf<Uri>()
    private val downloadUrls = mutableListOf<String>()

    private lateinit var binding: ActivityAddRestaurantBinding
    val TAG = "AddRestaurantActivity"

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference =
        firebaseStorage.reference.child("restaurant_logo_uri")

    private var restaurantId: String? = null

    private var wifiAvailability: Boolean = false
    private var bikeParking: Boolean = false
    private var carParking: Boolean = false

    val PICK_FILE_REQUEST = 1
    val PICK_SINGLE_IMAGE_REQUEST = 2
    val PICK_MULTIPLE_IMAGES_REQUEST = 3
    val REQUIRED_IMAGE_COUNT = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Activity created")

        val database = FirebaseDatabase.getInstance().reference.child("restaurants")
        restaurantId = database.push().key
        Log.d(TAG, "Generated restaurantId: $restaurantId")

        binding.chooseFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/json"
            }
            startActivityForResult(intent, PICK_FILE_REQUEST)
            Log.d(TAG, "Starting file picker intent")
        }

        binding.createRestaurantButton.setOnClickListener {
            fileUri?.let {
                if (restaurantId != null) {
                    Log.d(TAG, "Starting profile image upload and restaurant creation")
                    uploadProfileImageAndSaveData(restaurantId!!)
                    val menuItemsList = parseJsonFile(it)
                    uploadMenuToFirebase(menuItemsList, restaurantId!!)

                }
            } ?: Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show()
        }

        binding.addRestaurantLogo.setOnClickListener {
            Log.d(TAG, "Opening Gallery")
            openSingleImagePicker()
        }

        binding.imagePickerBtn.setOnClickListener {
            Log.d(TAG, "Opening Gallery")
            openMultipleImagePicker()
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

    private fun openSingleImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select an Image"),
            PICK_SINGLE_IMAGE_REQUEST
        )
    }

    private fun openMultipleImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select 5 Images"),
            PICK_MULTIPLE_IMAGES_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                PICK_FILE_REQUEST -> handleFileSelection(data)
                PICK_SINGLE_IMAGE_REQUEST -> handleSingleImageSelection(data)
                PICK_MULTIPLE_IMAGES_REQUEST -> handleMultipleImageSelection(data)
            }
        }
    }

    private fun handleFileSelection(data: Intent) {
        fileUri = data.data
        Log.d(TAG, "Selected JSON file: $fileUri")
    }

    private fun handleSingleImageSelection(data: Intent) {
        profileImageUri = data.data
        profileImageUri?.let {
            Picasso.get().load(it).into(binding.addRestaurantLogo)
            Log.d(TAG, "Selected image displayed in ImageView")
        }
    }

    private fun handleMultipleImageSelection(data: Intent) {
        if (data.clipData != null) {
            val count = data.clipData!!.itemCount
            if (count == REQUIRED_IMAGE_COUNT) {
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
                restaurantId?.let { uploadRestaurantImagesToFirebaseStorage(it) }
            } else {
                Toast.makeText(
                    this,
                    "Please select $REQUIRED_IMAGE_COUNT images",
                    Toast.LENGTH_SHORT
                ).show()
                openMultipleImagePicker()
            }
        } else {
            Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
            openMultipleImagePicker()
        }
    }

    private fun uploadRestaurantImagesToFirebaseStorage(
        restaurantId: String
    ) {
        val storageRef = firebaseStorage.reference.child("restaurant_images")

        selectedImages.forEachIndexed { index, uri ->
            val fileName = "image_${System.currentTimeMillis()}_$index.jpg"
            val imageRef = storageRef.child(fileName)

            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val imageUrl = downloadUrl.toString()
                        Log.d(TAG, "Image $index uploaded successfully: $imageUrl")

                        // Add download URL to the mutable list
                        downloadUrls.add(imageUrl)

                        // Check if all images are uploaded before storing URLs in database
                        if (downloadUrls.size == selectedImages.size) {
                            storeRestaurantImageUrlsInDatabase(restaurantId)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to upload image $index: ${exception.message}")
                    Toast.makeText(this, "Failed to upload image $index: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun storeRestaurantImageUrlsInDatabase(
        restaurantId: String
    ) {
        val databaseRef = firebaseDatabase.reference.child("restaurant_images").child(restaurantId)

        downloadUrls.forEachIndexed { index, imageUrl ->
            val imageKey = databaseRef.push().key
            imageKey?.let {
                databaseRef.child(it).setValue(imageUrl)
                    .addOnSuccessListener {
                        Log.d(TAG, "Image URL stored in database successfully")
                        Toast.makeText(this, "Image URLs stored in database", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failed to store image URL in database: ${exception.message}")
                        Toast.makeText(this, "Failed to store image URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun parseJsonFile(
        uri: Uri
    ): List<Map<String, Any>> {
        val menuItemsList = mutableListOf<Map<String, Any>>()
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            reader.forEachLine { line -> stringBuilder.append(line) }
            val jsonArray = JSONArray(stringBuilder.toString())

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val menuItemMap = mapOf(
                    "name" to item.getString("name"),
                    "description" to item.getString("description"),
                    "price" to item.getString("price")
                )
                menuItemsList.add(menuItemMap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Error reading file: ${e.message}")
            Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return menuItemsList
    }

    private fun uploadMenuToFirebase(
        menuItemsList: List<Map<String, Any>>,
        restaurantId: String
    ) {
        val databaseRef = firebaseDatabase.getReference("menus/$restaurantId")
        databaseRef.setValue(menuItemsList)
            .addOnSuccessListener {
                Log.d(TAG, "Menu items uploaded to Firebase successfully")
                Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload menu items to Firebase: ${it.message}")
                Toast.makeText(this, "Data upload failed: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun storeRestaurantDataInFirebase(
        profilePictureUrl: String,
        restaurantId: String
    ) {
        val restaurantName: String = binding.restaurantName.text.toString()
        val restaurantPhoneNumber: String = binding.restaurantPhoneNumber.text.toString()
        val restaurantAddressLink: String = binding.restaurantAddress.text.toString()
        val restaurantCuisine: String = binding.cuisineSpinner.selectedItem.toString()
        val openTime: String = binding.openTimePicker.text.toString()
        val closeTime: String = binding.closeTimePicker.text.toString()

        val coordinates = binding.restaurantcoordinates.text.toString()
        val location = "https://www.google.com/maps?q=loc:$coordinates"

        val rating : Double = 3.0

        val restaurantData = mapOf(
            "address" to restaurantAddressLink,
            "bikeParking" to bikeParking,
            "carParking" to carParking,
            "id" to restaurantId,
            "name" to restaurantName,
            "contactNumber" to restaurantPhoneNumber,
            "cuisineType" to restaurantCuisine,
            "openTime" to openTime,
            "closeTime" to closeTime,
            "wifi" to wifiAvailability,
            "rating" to rating,
            "location" to location,
            "restaurantLogoUrl" to profilePictureUrl
        )

        firebaseDatabase.getReference("restaurants")
            .child(restaurantId)
            .setValue(restaurantData)
            .addOnSuccessListener {
                Log.d(TAG, "Restaurant data stored in Firebase successfully")
                Toast.makeText(this, "Restaurant created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to store restaurant data in Firebase: ${it.message}")
                Toast.makeText(
                    this,
                    "Failed to create restaurant: ${it.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    private fun uploadProfileImageAndSaveData(
        restaurantId: String
    ) {
        profileImageUri?.let { uri ->
            storageReference.child(restaurantId).putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUrl ->
                        storeRestaurantDataInFirebase(downloadUrl.toString(), restaurantId)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to upload image: ${exception.message}")
                    Toast.makeText(
                        this,
                        "Failed to upload image: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
