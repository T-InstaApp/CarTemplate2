package com.example.cartemplate2.datamodel

data class MasterCategoryDataModel(
    val master_category_id: Int?,
    val Master_Category_Open_hours: ArrayList<OpenHours>?,
    val name: String?,
    val pos_id: String?,
    val status: Boolean?,
    val restaurant_id: Int?
)

data class OpenHours(
    val Hours_id: Int?,
    val day: String?,
    val start_time: String?,
    val end_time: String?,
    val status: Boolean?,
    val master_category: Int?
)
