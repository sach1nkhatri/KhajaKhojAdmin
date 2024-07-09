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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCouponsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddCoupon.setOnClickListener {
            storeCouponDataInFirebase()
        }
    }

    private fun storeCouponDataInFirebase() {
        val couponId = firebaseDatabase.reference.push().key // Fix reference push
        val couponCreatedDate: String = getCurrentDateTime()

        val coupon = mapOf(
            "id" to couponId,
            "code" to binding.restaurantDiscountCode.text.toString(),
            "restaurantName" to binding.restaurantName.text.toString(),
            "address" to binding.restaurantOutletAddress.text.toString(),
            "discountPercentage" to binding.restaurantDiscountRate.text.toString().toIntOrNull(),
            "minimumOrderPrice" to binding.restaurantMinimumOrder.text.toString().toIntOrNull(),
            "validFrom" to couponCreatedDate,
            "validTo" to binding.restaurantDiscountCodeValidTo.text.toString()
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
    }

    private fun getCurrentDateTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return formatter.format(date)
    }

    companion object {
        const val TAG = "AddCoupons"
    }
}
