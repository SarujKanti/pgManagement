package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class GetIDCardResponse(
    val data: List<IdItem>
)

data class IdItem(
    val type: String,
    val name: String,
    val image: String,
    val groupId: String,
    val doj: String?,
    val dob: String?,
    val `class`: String?
)

interface GetKidSProfile {
    @GET(ApiEndPoints.GET_PROFILE_API)
    suspend fun getKidSProfileSuspend(@Path("groupId") groupId: String): Response<GetIDCardResponse>
}