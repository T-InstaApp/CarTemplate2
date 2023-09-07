package com.example.cartemplate2.repositories

import com.example.cartemplate2.datamodel.*
import com.example.cartemplate2.network.MyApi
import com.example.cartemplate2.network.SafeApiRequest


class AuthRepository(private val api: MyApi) : SafeApiRequest() {
    suspend fun login(data: LoginDataModel) = apiRequest { api.login(data) }

    suspend fun mainLogin(user: String, pass: String, restID: String) =
        apiRequest { api.mainLogin(LoginDataModel(user, pass, restID)) }

    suspend fun getUserID(token: String, user: String) =
        apiRequest { api.getUserID(token, user) }

    suspend fun insertUser(token: String, userId: String, restroID: String) =
        apiRequest { api.insertUser(token, UserDataModel(userId, restroID)) }

    suspend fun userLogin(user: String, pass: String, restID: String) =
        apiRequest { api.mainLogin(LoginDataModel(user, pass, restID)) }

    suspend fun resetUserName(token: String, email: String) =
        apiRequest { api.resetUserName(token, ResetUserName(email, "", "")) }

    suspend fun resetPassword(token: String, userName: String) =
        apiRequest { api.resetPassword(token, ResetUserName("", "", userName)) }

    suspend fun verifyOTP(mobile: String, otp: String) =
        apiRequest { api.verifyOtp(MobileVerificationRequest(mobile, otp)) }

    suspend fun getOTP(mobile: String) =
        apiRequest { api.getOTP(MobileVerificationRequest(mobile, "")) }

    suspend fun registerUser(token: String, userData: UserRegisterRequest) =
        apiRequest {
            api.userRegistration(token, userData)
        }

    suspend fun createUser(token: String, userData: UserRegisterRequest) =
        apiRequest {
            api.createUser(token, userData)
        }

    suspend fun getProfile(token: String, userID: String) =
        apiRequest { api.getProfile(token, userID) }

    suspend fun updateCustomerProfile(
        token: String, userID: String, data: ProfileDataRequest
    ) = apiRequest { api.updateCustomerProfile(token, userID, data) }

    suspend fun changePassword(token: String, data: ChangePassword) =
        apiRequest { api.changePassword(token, data) }

    suspend fun updateProfile(
        token: String,
        userID: String,
        data: ProfileDataRequest
    ): UpdateProfileResponse? {
        return apiRequest { api.updateProfile(token, userID, data) }
    }


}