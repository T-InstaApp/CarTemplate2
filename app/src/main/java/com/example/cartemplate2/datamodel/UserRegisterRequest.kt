package com.example.cartemplate2.datamodel

import java.util.*

data class UserRegisterRequest(
    var username: String? = null,
    var email: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var password: String? = null,
    var restaurant_id: String? = null,
    var verified: String? = null,
    var customer_id: String? = null,
    var last_access: String? = null,
    var extra: String? = null,
    var salutation: String? = null,
    var phone_number: String? = null
)

data class UserCreateResponse(
    val salutation: String?,
    val last_access: String?,
    val first_name: String?,
    val email: String?,
    val last_name: Objects?,
    val phone_number: String?,
    val customer_id: Int?,
    val extra: String
)

data class UserRegistrationResponse(
    val id: Int?,
    val username: String?,
    val email: String?,
    val is_active: Boolean?,
    val last_login: Objects?,
    val verified: String?,
)

