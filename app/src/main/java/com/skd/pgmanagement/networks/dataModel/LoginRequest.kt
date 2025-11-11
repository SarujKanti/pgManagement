package com.skd.pgmanagement.networks.dataModel


import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


data class LoginRequest(
    val userName: UserName,
    val password: String,
    val deviceToken: String,
    val deviceType: String,
    val deviceModel: String,
    val appVersion: String,
    val osVersion: String,
    val udid: String,
)
data class UserName(
    val phone: String,
    val countryCode: String
)

data class LoginResponse(
    val voterId: String,
    val userId: String,
    val token: String,
    val switchRole: String,
    val phone: String,
    val name: String,
    val image: String,
    val groupId: String,
    val groupCategory: String,
    val groupCount: Int,
    val countryAlpha2Code: String,
    val counryTelephoneCode: Int
)


interface LoginApi{
    @POST(ApiEndPoints.LOGIN_TRUE)
    suspend fun loginISTrue(@Query("deviceToken") deviceToken: String, @Query("deviceType") deviceType: String, @Body request: LoginRequest): Response<LoginResponse>
}