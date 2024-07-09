package com.example.khajakhojadmin.model

data class Restaurant(
    var id: String = "",
    val name: String = "",
    val address: String = "",  // admin input address
    val cuisineType: String = "",  // Single cuisine Type
    val openTime: String = "",
    val closeTime: String = "",
    val contactNumber: String = "",
    val bikeParking: Boolean = false,
    val carParking: Boolean = false,
    var wifi: Boolean = false,
    val rating: Double = 0.0,
    val location: String = "",  // location of restaurnat for navigantion purpose
    val restaurantLogoUrl: String = "",
    val restaurantId : String = "",
    val userId : String = ""
)

