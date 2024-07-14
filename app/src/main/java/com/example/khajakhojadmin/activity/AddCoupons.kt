package com.example.khajakhojadmin.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.khajakhojadmin.databinding.ActivityAddCouponsBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class AddCoupons : AppCompatActivity() {
    private lateinit var binding: ActivityAddCouponsBinding
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    val TAG = "AddCoupons"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCouponsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddCoupon.setOnClickListener {
            storeCouponDataInFirebase()
        }
    }

    private fun getCurrentDateTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return formatter.format(date)
    }

    private fun generateCouponKey(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun checkCouponKeyUniqueness(couponKey: String, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Checking uniqueness for coupon key: $couponKey")
        firebaseDatabase.reference.child("coupons").orderByChild("couponKey").equalTo(couponKey)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val isUnique = result.children.count() == 0
                    Log.d(TAG, "Coupon key $couponKey is unique: $isUnique")
                    callback(isUnique)
                } else {
                    Log.e(TAG, "Failed to check coupon key uniqueness: ${task.exception?.message}")
                    callback(false)
                }
            }
    }

    private fun storeCouponDataInFirebase() {
        val couponCreatedDate: String = getCurrentDateTime()
        val couponKey = generateCouponKey(10) // Generate the unique coupon key

        checkCouponKeyUniqueness(couponKey) { isUnique ->
            if (isUnique) {
                val couponId = firebaseDatabase.reference.push().key

                val coupon = mapOf(
                    "id" to couponId,
                    "code" to binding.restaurantDiscountCode.text.toString(),
                    "restaurantName" to binding.restaurantName.text.toString(),
                    "address" to binding.restaurantOutletAddress.text.toString(),
                    "discountPercentage" to binding.restaurantDiscountRate.text.toString().toIntOrNull(),
                    "minimumOrderPrice" to binding.restaurantMinimumOrder.text.toString().toIntOrNull(),
                    "validFrom" to couponCreatedDate,
                    "validTo" to binding.restaurantDiscountCodeValidTo.text.toString(),
                    "couponKey" to couponKey
                )

                couponId?.let {
                    firebaseDatabase.reference.child("coupons").child(it).setValue(coupon)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Coupon data stored in Firebase successfully")
                                Toast.makeText(this, "Coupon added successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d(TAG, "Failed to store coupon data in Firebase: ${task.exception?.message}")
                                Toast.makeText(this, "Failed to add coupon", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Log.d(TAG, "Generated coupon key is not unique. Regenerating...")
                storeCouponDataInFirebase() // Retry with a new key
            }
        }
    }



//    private fun storeCouponDataInFirebase() {
//        val couponId = firebaseDatabase.reference.push().key
//        val couponCreatedDate: String = getCurrentDateTime()
//        val couponKey = generateCouponKey(10) // Generate the unique coupon key
//
//        checkCouponKeyUniqueness(couponKey) { isUnique ->
//            if (isUnique) {
//                Log.d(TAG, "Coupon key is unique")
//            } else {
//                Log.d(TAG, "Coupon key is not unique")
//            }
//            val coupon = mapOf(
//                "id" to couponId,
//                "code" to binding.restaurantDiscountCode.text.toString(),
//                "restaurantName" to binding.restaurantName.text.toString(),
//                "address" to binding.restaurantOutletAddress.text.toString(),
//                "discountPercentage" to binding.restaurantDiscountRate.text.toString()
//                    .toIntOrNull(),
//                "minimumOrderPrice" to binding.restaurantMinimumOrder.text.toString().toIntOrNull(),
//                "validFrom" to couponCreatedDate,
//                "validTo" to binding.restaurantDiscountCodeValidTo.text.toString(),
//                "couponKey" to couponKey
//            )
//
//            couponId?.let {
//                firebaseDatabase.reference.child("coupons").child(it).setValue(coupon)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Log.d(TAG, "Coupon data stored in Firebase successfully")
//                            Toast.makeText(this, "Coupon added successfully", Toast.LENGTH_SHORT)
//                                .show()
//
//                        } else {
//                            Log.d(
//                                TAG,
//                                "Failed to store coupon data in Firebase: ${task.exception?.message}"
//                            )
//                            Toast.makeText(this, "Failed to add coupon", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//            }
//        }
//    }
}
