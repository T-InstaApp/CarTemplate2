package com.example.cartemplate2.datamodel

data class UpdateProfileResponse(
    var id: Int? = null,
    var username: String? = null,
    var first_name: String? = null,
    var is_active: Boolean? = null,
    var last_login: String? = null,
    var last_name: String? = null,
    var groups: List<Any>? = null,
    var password: String? = null,
    var verified: String? = null
)