package com.example.cartemplate2.datamodel

data class ChangePassword(
    var old_password: String,
    var username: String,
    var new_password1: String,
    var new_password2: String
)

