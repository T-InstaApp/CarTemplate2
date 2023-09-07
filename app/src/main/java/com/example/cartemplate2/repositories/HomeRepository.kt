package com.example.cartemplate2.repositories


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.cartemplate2.network.MyApi
import com.example.cartemplate2.network.SafeApiRequest
import com.example.cartemplate2.paging.DataPagingSource
import com.google.gson.JsonObject

class HomeRepository(private val api: MyApi) : SafeApiRequest() {
    suspend fun getCategory(token: String, restID: String) =
        apiRequest { api.getCategory(token, restID) }

    fun getMenuData(catId: Int, restID: String, token: String) = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100),
        pagingSourceFactory = { DataPagingSource(api, catId, restID, token) }
    ).liveData

    suspend fun getProduct(token: String, type: String, restID: String, catId: String) =
        apiRequest { api.getProductData(token, type, restID, catId) }

    suspend fun getTestDriveBookingData(token: String, custID: String) =
        apiRequest { api.getTestDriveBookingData(token, custID) }


    suspend fun updateTestDriveBookingStatus(token: String, bookingID: Int, status: JsonObject) =
        apiRequest { api.updateTestDriveBookingStatus(token, bookingID, status) }


    suspend fun getBookingData(token: String, custID: String) =
        apiRequest { api.getBookingData(token, custID) }


    suspend fun getAppDetails(restorID: Int, type: String) =
        apiRequest { api.getAppDetails(restorID, type) }

    suspend fun getOverviewLabelData(token: String, type: String) =
        apiRequest { api.getOverviewLabelData(token, type) }


    suspend fun searchProduct(token: String, restID: String, type: String) =
        apiRequest { api.searchProductData(token, restID, type) }


    /* filterData.get("brand"), filterData.get("price"), filterData.get("km_driven"),
                    filterData.get("color"), filterData.get("size"), filterData.get("transmission"),
                    filterData.get("model_year"), filterData.get("owner"), filterData.get("fuel")*/

    suspend fun getProductFilterData(
        token: String, restID: String, brand: String?, price: String?,
        kmDriven: String?, color: String?, size: String?, transmission:
        String?, modelYear: String?, owner: String?, fuel: String?
    ) =
        apiRequest {
            api.getProductFilterData(
                token, restID, brand, price, kmDriven, color, size, transmission,
                modelYear, owner, fuel
            )
        }

}