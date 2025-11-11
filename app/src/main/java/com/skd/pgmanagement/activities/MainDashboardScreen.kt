package com.skd.pgmanagement.activities

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.skd.pgmanagement.R
import com.skd.pgmanagement.databinding.ActivityMainDashboardBinding
import com.skd.pgmanagement.networks.ApiEndPoints
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.GetHomeResponse
import com.skd.pgmanagement.views.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainDashboardScreen : BaseActivity<ActivityMainDashboardBinding>(R.layout.activity_main_dashboard) {

    private var groupId: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        groupId = intent.getStringExtra("PreferencesConstants.GROUP_ID")
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        getHomeApi()
    }

    private fun initToolbar() {
        binding.ivMore.setOnClickListener {
            showPartialFullscreenDialog()
        }
    }

    private fun showPartialFullscreenDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_partial)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set width to 80% of screen width and height to full
        val metrics = resources.displayMetrics
        val width = (metrics.widthPixels * 0.8).toInt()
        val height = WindowManager.LayoutParams.MATCH_PARENT

        dialog.window?.setLayout(width, height)
        dialog.window?.setGravity(Gravity.START)
        dialog.show()
    }

    private  fun getHomeApi(){
        val category = ApiEndPoints.appCategory
        val groupApi = RetrofitClient.homeApiService(this)

        groupId?.let {
            groupApi.getHomeGroups(it).enqueue(object : Callback<GetHomeResponse> {
                override fun onResponse(call: Call<GetHomeResponse>, response: Response<GetHomeResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        dismissProgressBar()
                        val groupResponse = response.body()
                        groupResponse?.let {

                        }

                    } else {
                        Toast.makeText(this@MainDashboardScreen, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GetHomeResponse>, t: Throwable) {
                    Toast.makeText(this@MainDashboardScreen, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}