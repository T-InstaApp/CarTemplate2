package com.example.cartemplate2.datamodel

data class CarSpecificationDataModel(
    val product_name: String?,
    val image: String?,
    val price: String?,
    val extra: String?,
    val brand_id: Int?,
    val brand_name: String?,
    val size_id: Int?,
    val car_type: String?,
    val specification: ArrayList<Specification>
)

data class Specification(
    val specification_id: Int?,
    val values: String?,
    val image_url: String?,
    val extra_data: String?,
    val type: String?,
    val addon_id: Int?,
    val addon_name: String?,
    val addon_title: String?,
    val addon_content: Int?,
    val addon_content_name: String?
)
