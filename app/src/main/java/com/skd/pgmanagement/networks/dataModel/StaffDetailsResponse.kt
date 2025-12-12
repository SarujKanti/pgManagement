package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class StaffUserDetailsResponse(
    val data: StaffUserDetails
)

data class StaffUserDetails(
    val image: String?,
    val dob: String?,
    val caste: String?,
    val designation: String?,
    val payRollApprover: Boolean,
    val doj: String?,
    val aadharNumber: String?,
    val accountant: Boolean,
    val gender: String?,
    val bloodGroup: String?,
    val email: String?,
    val disability: String?,
    val staffRegId: String?,
    val religion: String?,
    val teams: List<String>?,
    val fatherName: String?,
    val name: String?,
    val isAllowedToPost: Boolean,
    val qualification: String?,
    val bankName: String?,
    val staffCategory: String?,
    val className: String?,
    val permanent: Boolean,
    val address: String?,
    val bankAccountNumber: String?,
    val type: String?,
    val uanNumber: String?,
    val librarian: Boolean,
    val panNumber: String?,
    val bankIfscCode: String?,
    val staffId: String?,
    val motherName: String?,
    val admissionApprover: Boolean,
    val emergencyContactNumber: String?,
    val examiner: Boolean,
    val userId: String? = null,
    val phone: String?,
    val country: String? = null,
    val category: String? = null,
    val classTypeId: String? = null,
    val profession:String?,
    val userDownloadedApp:Boolean?,
    val isAdmin: Boolean? = false
)


interface StaffDetailsApi {
    @GET(ApiEndPoints.GET_STAFF_DETAILS)
    fun getStaffDetails(@Path("groupId") groupId: String, @Path("userId") userId: String, @Query("type") type: String): Call<StaffUserDetailsResponse>
}
