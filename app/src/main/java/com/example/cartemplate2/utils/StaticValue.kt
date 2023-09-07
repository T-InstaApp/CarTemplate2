package com.example.cartemplate2.utils


object StaticValue {
    const val APP_NAME = "Insta Automation"
    private const val PRODUCTION_BASE_URL = "https://restaurant60-be-prod-xtpocjmkpa-uw.a.run.app/"
    private const val DEVELOPMENT_BASE_URL = "https://restaurant60-be-dev-xtpocjmkpa-uw.a.run.app/"
    const val BASE_URL = DEVELOPMENT_BASE_URL
    const val REST_ID = "239"
    const val TEMP_RESTRO_ID = "1"
    const val REST_USER_NAME = "admin"
    const val REST_PASSWORD = "Mphasis8"
    val SALUTATION_DATA = arrayOf("Mr.", "Mrs.", "Miss.", "Ms.")
    const val DOT = "•"

    val chipYear =
        arrayOf("2020 and newer", "2017 - 2019", "2014-2016", "2011-2013", "2011 and older")

    val chipPrice =
        arrayOf(
            "Under 3 lakh", "3 lakh - 5 lakh", "5 lakh - 10 lakh", "10 lakh - 15 lakh",
            "Above 15 lakh"
        )

    val kmsPrice =
        arrayOf(
            "Under 10,000 KMs", "10,000 - 30,000 KMs", "30,000 - 50,000 KMs", "50,000 - 70,000 KMs",
            "Above 70,000 KMs"
        )


}