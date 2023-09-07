package com.example.cartemplate2.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cartemplate2.datamodel.CategoryResponse
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.repositories.CarRepository
import com.example.cartemplate2.utils.ApiException
import com.example.cartemplate2.utils.CommonKey
import com.example.cartemplate2.utils.Coroutines
import com.example.cartemplate2.utils.NoInternetException
import com.google.gson.JsonObject
import kotlinx.coroutines.Job

class CarViewModel(private val repository: CarRepository) : ViewModel() {

    var networkCallListener: NetworkCallListener? = null
    private lateinit var job: Job

    private val _categoryData = MutableLiveData<List<CategoryResponse>>()
    val categoryData: LiveData<List<CategoryResponse>>
        get() = _categoryData

    fun getProductImages(token: String, id: Int) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProductImages(token, id)
                response.let {
                    networkCallListener?.onSuccess(response, "getProductImages")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getProductSpecificationId(token: String, id: Int) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProductSpecificationId(token, id)
                response.let {
                    networkCallListener?.onSuccess(response, "getProductSpecificationId")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun bookTestDrive(token: String, data: JsonObject) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.bookTestDrive(token, data)
                response.let {
                    networkCallListener?.onSuccess(response, "bookTestDrive")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun bookCar(token: String, data: JsonObject) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.bookCar(token, data)
                response.let {
                    networkCallListener?.onSuccess(response, "bookCar")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


}