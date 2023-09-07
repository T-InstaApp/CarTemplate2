package com.example.cartemplate2.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cartemplate2.datamodel.CategoryResponse
import com.example.cartemplate2.datamodel.MenuResponseData
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.repositories.HomeRepository
import com.example.cartemplate2.utils.*
import com.google.gson.JsonObject
import kotlinx.coroutines.Job

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    var homeListener: NetworkCallListener? = null
    private lateinit var job: Job

    private val _categoryData = MutableLiveData<List<CategoryResponse>>()
    val categoryData: LiveData<List<CategoryResponse>>
        get() = _categoryData


    fun getCategory(token: String, restID: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getCategory(token.trim(), restID)
                response.let {
                    _categoryData.value = it
                    homeListener?.onSuccess(it, "getCategoryListData")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getProductData(token: String, restID: String, type: String, catID: String) {
        log("HomeFragment", " test $token $restID $type")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProduct(token.trim(), type, restID, catID)
                response.let {
                    homeListener?.onSuccess(it, type)
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun searchProduct(token: String, restID: String, type: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.searchProduct(token.trim(), restID, type)
                response.let {
                    homeListener?.onSuccess(it, "searchProduct")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun getTestDriveBookingData(token: String, custID: String) {
        log("HomeFragment", " $token $custID ")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getTestDriveBookingData(token, custID)
                response.let {
                    homeListener?.onSuccess(it, "getTestDriveBookingData")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getBookingData(token: String, custID: String) {
        log("HomeFragment", " $token $custID ")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getBookingData(token, custID)
                response.let {
                    homeListener?.onSuccess(it, "getBookingData")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateTestDriveBookingStatus(token: String, bookingID: Int, status: Boolean) {
        homeListener?.onStarted()
        val jsonObj = JsonObject()
        jsonObj.addProperty("status", status)

        log("TestDriveBookingListActivity", " data= $bookingID $jsonObj")
        Coroutines.main {
            try {
                val response = repository.updateTestDriveBookingStatus(token, bookingID, jsonObj)
                response.let {
                    homeListener?.onSuccess(it, "updateTestDriveBookingStatus")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getAppDetails(type: String, restroID: Int) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getAppDetails(restroID, type)
                response.let {
                    homeListener?.onSuccess(it, "getAppDetails")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getOverviewLabelData(token: String, type: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getOverviewLabelData(token, type)
                response.let {
                    homeListener?.onSuccess(it, "getOverviewLabelData")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getMenuData(
        token: String, restID: String, catID: Int
    ): LiveData<PagingData<MenuResponseData>> {
        return repository.getMenuData(catID, restID, token).cachedIn(viewModelScope)
    }


    fun getProductFilterData(token: String, restID: String, filterData: HashMap<String, String>) {
        log("HomeFragment", " test $token $restID $filterData")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProductFilterData(
                    token.trim(),
                    restID,
                    filterData["brand"], filterData["price"], filterData["km_driven"],
                    filterData["color"], filterData["size"], filterData["transmission"],
                    filterData["model_year"], filterData["owner"], filterData["fuel"]
                )
                response.let {
                    homeListener?.onSuccess(it, "getProductFilterData")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

}