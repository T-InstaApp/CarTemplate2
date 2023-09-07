package com.example.cartemplate2.network

import com.example.cartemplate2.datamodel.*
import com.example.cartemplate2.utils.StaticValue
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {
    @Headers("Content-Type: application/json")
    @GET("category/")
    suspend fun getCategory(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") id: String?
    ): Response<ArrayList<CategoryResponse>>

    // Not in Used
    @Headers("Content-Type: application/json")
    @GET("catalog/?status=ACTIVE")
    suspend fun getMenuList(
        @Header("Authorization") authorization: String,
        @Query("restaurant_id") rid: String,
        // @Query("category_id") cid: Int,
        @Query("page") page: Int
    ): MenuResponse


    @Headers("Content-Type: application/json")
    @GET("product-images/")
    suspend fun getProductImages(
        @Header("Authorization") authorization: String?,
        @Query("product") id: Int?
    ): Response<ArrayList<ProductImagesDataModel>>

    @Headers("Content-Type: application/json")
    @GET("productdetails/{product}/")
    suspend fun getProductSpecificationId(
        @Header("Authorization") authorization: String?,
        @Path("product") id: Int?
    ): Response<CarSpecificationDataModel>

    @Headers("Content-Type: application/json")
    @POST("user/")
    fun getUserRegId(
        @Header("Authorization") token: String?,
        @Body userRegistrationData: GetUserRegistrationRequestData?
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("customer/")
    fun userRegistration(
        @Header("Authorization") token: String?,
        @Body mUserRegistrationModel: CustomerDataModel
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/login/v1/")
    suspend fun login(
        @Body login: LoginDataModel
    ): Response<MainLoginDataModel>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/login/v1/")
    suspend fun mainLogin(
        @Body login: LoginDataModel
    ): Response<MainLoginDataModel>


    @Headers("Content-Type: application/json")
    @GET("user/")
    suspend fun getUserID(
        @Header("Authorization") authorization: String?,
        @Query("username") username: String?
    ): Response<UserIDResponse>


    @POST("userrestaurant/")
    suspend fun insertUser(
        @Header("Authorization") authorization: String?,
        @Body login: UserDataModel?
    ): Response<UserRestaurant>

    @Headers("Content-Type: application/json")
    @POST("forgot/user/")
    suspend fun resetUserName(
        @Header("Authorization") authorization: String?,
        @Body login: ResetUserName
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("rest-auth/password/reset/v1/")
    suspend fun resetPassword(
        @Header("Authorization") authorization: String?,
        @Body login: ResetUserName
    ): Response<ResponseBody>

    @POST("guest/verify/")
    suspend fun verifyOtp(
        @Body mobileVerification: MobileVerificationRequest
    ): Response<MobileVerifyResponse>

    @POST("send/code/")
    suspend fun getOTP(
        @Body mobileVerification: MobileVerificationRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("user/")
    suspend fun userRegistration(
        @Header("Authorization") authorization: String?,
        @Body login: UserRegisterRequest?
    ): Response<UserRegistrationResponse>

    @Headers("Content-Type: application/json")
    @POST("customer/")
    suspend fun createUser(
        @Header("Authorization") token: String?,
        @Body data: UserRegisterRequest
    ): Response<ResponseBody>

    @GET("customer/")
    suspend fun getProfile(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") customer_id: String?
    ): Response<ProfileResponse>

    @PUT("customer/{id}/")
    suspend fun updateCustomerProfile(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body rid: ProfileDataRequest
    ): Response<UpdateProfileResponse>

    @POST("rest-auth/password/reset/confirm/v1/")
    suspend fun changePassword(
        @Header("Authorization") authorization: String?,
        @Body rid: ChangePassword?
    ): Response<ChangePasswordResponse>

    @PUT("user/{id}/")
    suspend fun updateProfile(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String?,
        @Body rid: ProfileDataRequest?
    ): Response<UpdateProfileResponse>


    @GET("product-details/")
    suspend fun getProductData(
        @Header("Authorization") authorization: String?,
        @Query("type") type: String?,
        @Query("restaurant_id") restID: String?,
        @Query("category_id") catID: String?
    ): Response<ArrayList<ProductDataMode>>


    @Headers("Content-Type: application/json")
    @POST("cartestdrivedetails/")
    suspend fun bookTestDrive(
        @Header("Authorization") authorization: String?,
        @Body login: JsonObject
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("place-order/")
    suspend fun bookCar(
        @Header("Authorization") authorization: String?,
        @Body login: JsonObject
    ): Response<ResponseBody>

    @GET("cartestdrivedetails/")
    suspend fun getTestDriveBookingData(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") catID: String?
    ): Response<ArrayList<TestDriveBooking>>


    @GET("place-order/")
    suspend fun getBookingData(
        @Header("Authorization") authorization: String?,
        @Query("customer_id") catID: String?
    ): Response<ArrayList<ProductBookingListDataModel>>


    @PUT("cartestdrivedetails/{id}/")
    suspend fun updateTestDriveBookingStatus(
        @Header("Authorization") authorization: String?,
        @Path("id") id: Int?,
        @Body status: JsonObject?
    ): Response<ResponseBody>

    @GET("restaurant-details/")
    suspend fun getAppDetails(
        @Query("restaurant_id") restroID: Int,
        @Query("type") type: String?
    ): Response<CompanyData>

    @Headers("Content-Type: application/json")
    @GET("get-overview-labels/")
    suspend fun getOverviewLabelData(
        @Header("Authorization") authorization: String?,
        @Query("industry_type") industryType: String?
    ): Response<ArrayList<OverviewsLabelData>>


    @GET("product-search/")
    suspend fun searchProductData(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") restID: String?,
        @Query("search_param") type: String?,
    ): Response<ArrayList<ProductDataMode>>


    /*brand:TATA
price:254000.00
km_driven:34567
color:
size:suv
transmission:ok
model_year:12-12-2019
owner:2nd owner*/

    @GET("product-filter/")
    suspend fun getProductFilterData(
        @Header("Authorization") authorization: String?,
        @Query("restaurant_id") restID: String?,
        @Query("brand") brand: String?,
        @Query("price") price: String?,
        @Query("km_driven") km_driven: String?,
        @Query("color") color: String?,
        @Query("size") size: String?,
        @Query("transmission") transmission: String?,
        @Query("model_year") model_year: String?,
        @Query("owner") owner: String?,
        @Query("fuel") fuel: String?
    ): Response<ArrayList<ProductDataMode>>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): MyApi {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(StaticValue.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }


}