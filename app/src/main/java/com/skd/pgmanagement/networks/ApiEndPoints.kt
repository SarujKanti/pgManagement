package com.skd.pgmanagement.networks

import com.skd.pgmanagement.BuildConfig

object ApiEndPoints {
    const val appName = BuildConfig.AppName
    const val appCategory = BuildConfig.AppCategory
    const val addSchool = BuildConfig.AddSchool

    const val USER_EXIST = "user/exist/category/app?appName=${appName}&category=${appCategory}&addSchool=${addSchool}"

    const val LOGIN_TRUE = "login/category/app?category=${appCategory}&appName=${appName}&addSchool=${addSchool}"

    const val GET_HOME_API = "groups/{groupId}/home"

    const val GET_PROFILE_API = "groups/{groupId}/my/kids/profile"

    const val GET_ALL_STAFFS = "groups/{groupId}/staff/get"
    const val ADD_STAFF = "groups/{groupId}/multiple/staff/register"
    const val GET_STAFF_DETAILS = "groups/{groupId}/user/{userId}/profile/get"
    const val GET_GALLERY_ITEMS= "groups/{groupId}/gallery/get"
}