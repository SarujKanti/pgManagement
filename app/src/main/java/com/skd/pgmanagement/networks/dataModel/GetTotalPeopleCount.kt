package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class GetTotalPeopleCount(
    val data: TotalCountData
)

data class TotalCountData(
    val totalNoOfStudentsPresent: Int,
    val totalNoOfStudentsAbsent: Int,
    val totalNoOfStudents: Int,
    val totalNoOfStaffsPresent: Int,
    val totalNoOfStaffs: Int,
    val totalNoOfClasses: Int,
    val totalNoOfAttendanceTakenClasses: Int
)

interface TotalPeopleApi {
    @GET(ApiEndPoints.GET_PEOPLE_COUNT)
    suspend fun getTotalPeople(@Path("groupId") groupId: String): Response<GetTotalPeopleCount>
}