package com.example.cartemplate2.repositories



import com.example.cartemplate2.network.MyApi
import com.example.cartemplate2.network.SafeApiRequest
import com.google.gson.JsonObject

class CarRepository(private val api: MyApi) : SafeApiRequest() {
    suspend fun getProductImages(token: String, id: Int) =
        apiRequest { api.getProductImages(token, id) }

    suspend fun getProductSpecificationId(token: String, id: Int) =
        apiRequest { api.getProductSpecificationId(token, id) }


    suspend fun bookTestDrive(token: String, data: JsonObject) =
        apiRequest { api.bookTestDrive(token, data) }

    suspend fun bookCar(token: String, data: JsonObject) =
        apiRequest { api.bookCar(token, data) }

}