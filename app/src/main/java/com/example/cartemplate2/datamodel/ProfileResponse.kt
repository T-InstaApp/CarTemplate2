package com.example.cartemplate2.datamodel

data class ProfileResponse(
    var count: Int? = null,
    var next: Any? = null,
    var previous: Any? = null,
    var results: List<ProfileResult>? = null

)

data class ProfileResult(
    var customer: CustomerData? = null,
    var last_access: String? = null,
    var extra: Any? = null,
    var salutation: String? = null,
    var phone_number: String? = null
) {
    val code: String
        get() = phone_number!!.substring(0, (phone_number!!.length - 10))

    val phone: String
        get() = phone_number!!.substring((phone_number!!.length - 10), phone_number!!.length)
}
data class CustomerData(
    var id: Int? = null,
    var username: String? = null,
    var email: String? = null,
    var first_name: String? = null,
    var is_active: Boolean? = null,
    var last_login: Any? = null,
    var last_name: String? = null,
    var groups: List<Any>? = null,
    var password: String? = null,
    var verified: String? = null,

    )
