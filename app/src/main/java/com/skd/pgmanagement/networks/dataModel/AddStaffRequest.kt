package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class AddStaffRequest(
    val staffData: List<StaffData>
)

data class StaffData(
    val countryCode: String,
    val designation: String,
    val name: String,
    val permanent: Boolean,
    val phone: String,

)

interface AddStaffApi {
    @POST(ApiEndPoints.ADD_STAFF)
    suspend fun addStaff(
        @Path("groupId") groupId: String,
        @Body request: AddStaffRequest, @Query("type") type: String): Response<AddStaffRequest>
}
