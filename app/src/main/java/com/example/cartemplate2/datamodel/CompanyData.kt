package com.example.cartemplate2.datamodel

data class CompanyData(
    val Token: String?,
    val compony_name: String?,
    val compony_image: String?,
    val country: String?,
    val currency: String?,
    val currency_symbol: String?,
    val IS_CASH_AVAILABLE: Boolean?,
    val IS_CARD_AVAILABLE: Boolean?,
    val IS_PICKUP_AVAILABLE: Boolean?,
    val IS_DELIVERY_AVAILABLE: Boolean?,
    val REST_PICKUP_ID: String?,
    val REST_DELIVERY_ID: String?
)
