package com.example.cartemplate2.datamodel

data class UserIDResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<UserIdResponseData>?
)

data class UserIdResponseData(
    val id: Int?,
    val username: String?,
    val email: String?,
    val first_name: String?,
    val is_active: Boolean?,
    val last_login: String?,
    val last_name: String?,
    val groups: List<Any>?,
    val password: String?,
    val verified: String?,
)
