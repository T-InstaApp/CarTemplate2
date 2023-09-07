package com.example.cartemplate2.model


import androidx.lifecycle.ViewModel
import com.example.cartemplate2.datamodel.ChangePassword
import com.example.cartemplate2.datamodel.LoginDataModel
import com.example.cartemplate2.datamodel.ProfileDataRequest
import com.example.cartemplate2.datamodel.UserRegisterRequest
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.repositories.AuthRepository
import com.example.cartemplate2.utils.*

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var networkCallListener: NetworkCallListener? = null

    fun login(data: LoginDataModel) {
        networkCallListener!!.onStarted()
        Coroutines.main {
            try {
                val response = repository.login(data)
                response.let {
                    networkCallListener?.onSuccess(response, "login")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun mainLogin(user: String, pass: String, restID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.mainLogin(user, pass, restID)
                mainLoginResponse.let {
                    networkCallListener?.onSuccess(mainLoginResponse, "mainLogin")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getUserID(token: String, userName: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getUserID(token, userName)
                response.let {
                    networkCallListener?.onSuccess(response, "getUserID")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun insertUser(token: String, userId: String, restID: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.insertUser(token, userId, restID)
                response.let {
                    networkCallListener?.onSuccess(response, "insertUser")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun checkLogin(restID: String, userName: String, password: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val authResponse =
                    repository.userLogin(userName, password, restID)
                authResponse.let {
                    networkCallListener?.onSuccess(it, "Login")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun resetUserName(token: String, email: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.resetUserName(token, email)
                response.let {
                    networkCallListener?.onSuccess(response, "resetUserName")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun resetPassword(token: String, username: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.resetPassword(token, username)
                response.let {
                    networkCallListener?.onSuccess(response, "resetPassword")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun verifyOTP(otp: String, code: String, mobile: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.verifyOTP(code + mobile, otp)
                response.let {
                    networkCallListener?.onSuccess(response, "verifyOTP")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun generateOtp(mobile: String) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getOTP(mobile)
                response.let {
                    networkCallListener?.onSuccess(response, "generateOtp")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun registerUser(token: String, data: UserRegisterRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val authResponse = repository.registerUser("Token $token", data)
                authResponse.let {
                    networkCallListener?.onSuccess(it, "registerUser")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun createUser(token: String, data: UserRegisterRequest) {
        Coroutines.main {
            try {
                val authResponse = repository.createUser("Token $token", data)
                authResponse.let {
                    networkCallListener?.onSuccess(it, "createUser")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getUserProfile(token: String, userId: String) {

        log("ProfileActivity", " $token, id $userId")
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getProfile(token, userId)
                response.let {
                    networkCallListener?.onSuccess(response, "getUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateCustomerProfile(token: String, userId: String, data: ProfileDataRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.updateCustomerProfile(token, userId, data)
                response.let {
                    networkCallListener?.onSuccess(response, "updateCustomerProfile")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun changePassword(token: String, data: ChangePassword) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.changePassword(
                    token, data
                )
                response.let {
                    networkCallListener?.onSuccess(response, "changePassword")
                    return@main
                }
            } catch (e: ApiException) {
                networkCallListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                networkCallListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun onUpdateUserProfile(token: String, userId: String, data: ProfileDataRequest) {
        networkCallListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.updateProfile(
                    token, userId, data
                )
                response.let {
                    networkCallListener?.onSuccess(response, "onUpdateUserProfile")
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