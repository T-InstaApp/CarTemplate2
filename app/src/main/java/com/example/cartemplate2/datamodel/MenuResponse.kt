package com.example.cartemplate2.datamodel

data class MenuResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<MenuResponseData>?
)

data class MenuResponseData(
    val product_id: Int?,
    val product_name: String?,
    val product_url: String?,
    val price: String?,
    val extra: String?,
    val category: String?,
    val productId: String?,
    val productName: String?,
    val productUrl: String?,
    val foodType: Boolean = false,
    val in_stock: Boolean?,
    val available_time: String?,
    val delivery_type: String?,
    val category_available_time: String?,
    val brand: String?
)
