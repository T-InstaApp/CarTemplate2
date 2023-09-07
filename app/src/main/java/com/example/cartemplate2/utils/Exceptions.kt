package com.example.cartemplate2.utils

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ApiException(message: String) : IOException(message)
class NoInternetException(message: String,) : IOException(message)
class HttpExceptionHandle(message: Response<String>) : HttpException(message)
