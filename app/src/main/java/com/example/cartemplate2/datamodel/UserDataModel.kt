package com.example.cartemplate2.datamodel

data class UserDataModel(
    val user: String,
    var restaurant: String
)

data class UserRestaurant(
    val id: Int?,
    val user: Int?,
    val restaurant: Int?,
)
