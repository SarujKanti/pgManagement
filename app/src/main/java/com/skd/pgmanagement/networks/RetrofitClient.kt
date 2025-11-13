package com.skd.pgmanagement.networks

import android.content.Context
import com.google.gson.GsonBuilder
import com.skd.pgmanagement.constants.Constants
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.networks.dataModel.AddStaffApi
import com.skd.pgmanagement.networks.dataModel.GetAllStaffApi
import com.skd.pgmanagement.networks.dataModel.GetHomeApi
import com.skd.pgmanagement.networks.dataModel.GetKidSProfile
import com.skd.pgmanagement.networks.dataModel.LoginApi
import com.skd.pgmanagement.networks.dataModel.UserExist
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val sharedPreferences = context.getSharedPreferences(StringConstants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(StringConstants.AUTH_TOKEN, null)
        val requestBuilder = chain.request().newBuilder()

        // Add Authorization header with Bearer token if available
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}

object RetrofitClient {
    private const val URL = Constants.BASE_URL

    private fun getRetrofit(context: Context): Retrofit {
        // Logging interceptor to log the request and response bodies
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Build OkHttpClient with both logging and auth interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(logging) // For logging
            .addInterceptor(AuthInterceptor(context)) // For attaching Bearer token
            .build()

        val builder = GsonBuilder().disableHtmlEscaping().create()
        // Build and return Retrofit instance
        return Retrofit.Builder()
            .baseUrl(URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(builder))
            .build()
    }

    fun loginApiService(context: Context): UserExist {
        return getRetrofit(context).create(UserExist::class.java)
    }

    fun loginPasswordApiService(context: Context): LoginApi {
        return getRetrofit(context).create(LoginApi::class.java)
    }

    fun homeApiService(context: Context): GetHomeApi {
        return getRetrofit(context).create(GetHomeApi::class.java)
    }

    fun profileApiService(context: Context): GetKidSProfile {
        return getRetrofit(context).create(GetKidSProfile::class.java)
    }

    fun getStaffApiService(context: Context): GetAllStaffApi {
        return getRetrofit(context).create(GetAllStaffApi::class.java)
    }

    fun addStaffApiService(context: Context): AddStaffApi {
        return getRetrofit(context).create(AddStaffApi::class.java)
    }
}