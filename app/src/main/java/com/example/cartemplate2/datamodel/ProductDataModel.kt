package com.example.cartemplate2.datamodel

data class ProductDataMode(
    val product_id: Int?,
    val restaurant: Int?,
    val product_name: String?,
    val product_url: String?,
    val price: String?,
    val media: String?,
    val caption: String?,
    val extra: String?,
    val image: Any?, // You can replace Any? with a more appropriate type if you know the expected type
    val status: String?,
    val tax_exempt: Boolean?,
    val source_id: String?,
    val optional: Boolean?,
    val sequence: Int?,
    val available_time: String?,
    val delivery_type: String?,
    val brand: String?,
    val model: String?,
    val sub_category: String?,
    val category: Category?,
    val product_size: String?,
    val product_specification: Map<String, String>? // You can adjust the type if you know the expected structure
)

data class Category(
    val category_id: Int?,
    val category_name: String?,
    val category_available_time: String?
)
