package com.example.khajakhojadmin.model

data class Coupon(
    val id: String = "",
    val code: String = "",
    val restaurantName: String = "",
    val address: String = "", // in case a coupon is for specific outlet
    val discountPercentage: Int = 0,
    val minimumOrderPrice: Int = 0,      // minimum order price.
    val validFrom: String = "",
    val validTo: String = "",

    val couponKey: String = ""
)
