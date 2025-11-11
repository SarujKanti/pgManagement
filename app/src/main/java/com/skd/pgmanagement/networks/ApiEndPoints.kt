package com.skd.pgmanagement.networks

import com.skd.pgmanagement.BuildConfig

object ApiEndPoints {
    const val appName = BuildConfig.AppName
    const val appCategory = BuildConfig.AppCategory
    const val addSchool = BuildConfig.AddSchool

    const val USER_EXIST = "user/exist/category/app?appName=${appName}&category=${appCategory}&addSchool=${addSchool}"

    const val LOGIN_TRUE = "login/category/app?category=${appCategory}&appName=${appName}&addSchool=${addSchool}"

    const val GET_HOME_API = "groups/{groupId}/home"
}