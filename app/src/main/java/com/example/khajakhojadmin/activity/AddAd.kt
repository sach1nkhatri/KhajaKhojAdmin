package com.example.khajakhojadmin.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.khajakhojadmin.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageMetadata
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class AddAd : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("ads")
    private val MAX_ADS = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ad)

        imageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.button)

        imageView.setOnClickListener {
            pickImageFromGallery()
        }

        uploadButton.setOnClickListener {
            (imageView.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                uploadAdImageToFirebase(bitmap)
            }
        }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    setImageViewFromUri(uri)
                }
            }
        }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun setImageViewFromUri(uri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        imageView.setImageBitmap(bitmap)
    }

    private fun uploadAdImageToFirebase(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val fileName = "ad_${UUID.randomUUID()}.jpg"

        // Add metadata with timestamp
        val metadata = StorageMetadata.Builder()
            .setCustomMetadata("timestamp", System.currentTimeMillis().toString())
            .build()

        val uploadTask = storageReference.child(fileName).putBytes(data, metadata)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Advertisement Image Uploaded", Toast.LENGTH_SHORT).show()
            manageAdsLimit()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun manageAdsLimit() {
        storageReference.listAll().addOnSuccessListener { listResult ->
            val items = listResult.items
            if (items.size > MAX_ADS) {
                // Retrieve metadata to find the oldest item
                val itemMetadataList = items.map { storageReference.child(it.name).metadata }
                Tasks.whenAllSuccess<StorageMetadata>(itemMetadataList).addOnSuccessListener { metadataList ->
                    val oldestItem = items.zip(metadataList).minByOrNull { (_, metadata) ->
                        metadata.getCustomMetadata("timestamp")?.toLong() ?: Long.MAX_VALUE
                    }?.first
                    oldestItem?.delete()
                }
            }
        }
    }
}
