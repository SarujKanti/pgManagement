package com.skd.pgmanagement.networks.dataModel

import com.skd.pgmanagement.networks.ApiEndPoints
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class GalleryGetRequest(
    val totalNumberOfPages: Int,
    val data: List<AlbumData>
)

data class AlbumData(
    val updatedAt: String,
    val groupId: String,
    val fileType: String?,
    val fileName: List<String>,
    val description: String,
    val createdAt: String,
    val canEdit: Boolean,
    val albumName: String,
    val albumId: String
)

interface GalleryGetApi {
    @GET(ApiEndPoints.GET_GALLERY_ITEMS)
    suspend fun getGalleryPosts(@Path("groupId") groupId: String, @Query("page") page: Int): Response<GalleryGetRequest>
}