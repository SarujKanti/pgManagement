package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class UserExistRequest(
    val countryCode: String,
    val phone: String
)

interface UserExist {
    @POST(ApiEndPoints.USER_EXIST)
    suspend fun checkUserExist(
        @Body request: UserExistRequest
    ): Response<UserExistResponse>
}

data class UserExistResponse(
    val data: UserData
)

data class UserData(
    val secretKey: String,
    val phone: String,
    val isUserExist: Boolean,
    val isAllowedToAccessApp: Boolean,
    val countryCode: String,
    val accessKey: String
)