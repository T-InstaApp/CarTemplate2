package com.example.cartemplate2.datamodel

data class GetUserRegistrationRequestData(
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val verified: String,
    val restaurant_id: String
)

data class CustomerDataModel(
    val customer_id: String,
    val last_access: String,
    val extra: String,
    val salutation: String,
    val phone_number: String,
    val first_name: String,
    val last_name: String,
    val email: String
)
