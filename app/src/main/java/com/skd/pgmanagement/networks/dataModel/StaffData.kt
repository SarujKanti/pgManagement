package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


data class GetTeachingStaffResponse(
    val totalNumberOfPages: Int,
    val data: List<GetTeachingStaffStaffsData>,
)

data class GetTeachingStaffStaffsData(
    val permanent: String? = null,
    val userId: String,
    val staffId: String,
    val accountant: Boolean,
    val disability: String? = null,
    val bankAccountNumber: String? = null,
    val nonteaching: Boolean,
    val aadharNumber: String? = null,
    val bloodGroup: String? = null,
    val panNumber: String,
    val doj: String? = null,
    val staffRegId: String,
    val designation: String,
    val isAllowedToPost: Boolean,
    val address: String? = null,
    val seniorNumber: Any? = null,
    val accountType: Any? = null,
    val education: Any? = null,
    val seniorSerialNumber: Any? = null,
    val religion: String? = null,
    val examiner: Boolean,
    val type: String,
    val payRollApprover: Boolean,
    val bankBranch: Any? = null,
    val classTypeId: String? = null,
    val admissionApprover: Boolean,
    val bankIfscCode: String? = null,
    val className: String? = null,
    val dob: String? = null,
    val motherName: String? = null,
    val userDownloadedApp: Boolean,
    val caste: String? = null,
    val email: String? = null,
    val country: String,
    val phone: String,
    val bankAddress: Any? = null,
    val inventoryApprover: Boolean,
    val BitmapData: Any? = null,
    val IsoTemplate: Any? = null,
    val uanNumber: String,
    val qualification: String,
    val management: Boolean,
    val gender: String? = null,
    val teaching: Boolean,
    val name: String,
    val biometric: Boolean,
    val alternatePhoneNumber: Any? = null,
    val homeAddress: Any? = null,
    val fatherName: String? = null,
    val achievements: Any? = null,
    val staffCategory: String? = null,
    val librarian: Boolean,
    val image: String? = null,
    val officeAddress: Any? = null,
    val category: String? = null,
    val emergencyContactNumber: String? = null,
    val bankName: String? = null,
    val profession: Any? = null

)

interface GetAllStaffApi {
    @GET(ApiEndPoints.GET_ALL_STAFFS)
    suspend fun getAllStaffService(@Path("groupId") groupId: String): Response<GetTeachingStaffResponse>
}