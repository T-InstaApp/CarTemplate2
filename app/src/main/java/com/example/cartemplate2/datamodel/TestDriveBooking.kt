package com.example.cartemplate2.datamodel

data class TestDriveBooking(
    val booking_id: Int?,
    val product_id: Int?,
    val product_name: String?,
    val price: String?,
    val product_url: String?,
    val status: String?,
    val in_stock: Boolean?,
    val category: Int?,
    val restaurant: Int?,
    val delivery_type: String?,
    val customer_id: Int?,
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val phone_number: String?,
    val verified: String?,
    val is_active: Boolean?,
    val created_at: String?,
    val requested_at: String?,
    val complete_address: String?,
    val door_step: Boolean = false,
    val product_specification: Map<String, String>?,
    val booking_status: Boolean = true
)


