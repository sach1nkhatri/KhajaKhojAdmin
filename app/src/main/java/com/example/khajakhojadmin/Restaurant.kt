package com.example.khajakhojadmin

data class Restaurant(
    var id: String = "",
    val name: String = "",
    val address: String = "",
    val cuisineType: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val contactNumber: String = "",
    val bikeParking: Boolean = false,
    val carParking: Boolean = false,
    var wifi: Boolean = false,
    val rating: Double = 0.0,
    val location: String = "",
    val restaurantLogoUrl: String = "",
)

