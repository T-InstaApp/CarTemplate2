package com.example.cartemplate2.datamodel

data class ProductBookingListDataModel(
    val order_id: Int?,
    val product: Product?,
    val product_specification: Map<String, String>?,
    val currency: String?,
    val subtotal: String?,
    val tip: String?,  // Change to String? if it can be null
    val service_fee: String?,
    val tax: String?,
    val shipping_fee: String?,
    val discount: String?,  // Change to String? if it can be null
    val created_at: String?,
    val updated_at: String?,
    val extra: String?,
    val restaurant_request: String?,
    val shipping_address_text: String?,
    val billing_address_text: String?,
    val token: String?,
    val customer: customer?,
    val total: String?,

)

data class customer(
    val user_id: Int?,
    val user_name: String?,
    val phone_number: String?,  // Change to String? if it can be null

)

data class Product(
    val product_id: Int?,
    val product_name: String?,
    val size: String?,  // Change to String? if it can be null
    val brand: String?,
    val price: Double?,
    val product_image:String?
)
