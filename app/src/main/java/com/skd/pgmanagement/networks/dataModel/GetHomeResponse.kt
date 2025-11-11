package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class GetHomeResponse(
    val data: List<ActivityData>
)

data class ActivityData(
    val kanActivity: String,
    val image: String? = null,
    val featureIcons: List<FeatureIcon>,
    val activity: String
)

data class FeatureIcon(
    val type: String,
    val role: String,
    val name: String,
    val kanName: String,
    val image: String,
    val id: Int,
    val isAdmissionApprover:Boolean,
    val examiner:Boolean,
    val isVerifier:Boolean,
    val isApprover:Boolean,
    val isGenerator:Boolean,
    val groupId: String,
    val teamId: String? = "",
    val details: Details,
    val count: Int = 0,
    val isTeamAdmin: Boolean,
    val isSecurity:Boolean? = false,
    val allowTeamPostCommentAll:Boolean,
    val allowTeamPostAll:Boolean,
    val category:String? = null,
    val members:Int,
    val link:String?,
    val isClass:Boolean,
    val enableGps:Boolean,
    val enableAttendance:Boolean,
    val canAddUser:Boolean,
    val isAdmin:Boolean? = false,
    val isTeacher: Boolean? = false,
    val isParent: Boolean? = false,
    val librarian:Boolean,
)

data class Details(
    val id: Int,
    val userId: String,
    val teamId: String,
    val studentName: String,
    val teamName: String,
    val teamImage: String,
    val classTeacherId: String,
    val category: String,
)


interface GetHomeApi {
    @GET(ApiEndPoints.GET_HOME_API)
     fun getHomeGroups(@Path("groupId") groupId: String): Call<GetHomeResponse>


}
