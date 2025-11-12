package com.skd.pgmanagement.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.loginPage.LoginActivity
import com.skd.pgmanagement.adapters.HomeCategoryAdapter
import com.skd.pgmanagement.databinding.ActivityMainDashboardBinding
import com.skd.pgmanagement.networks.ApiEndPoints
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.ActivityData
import com.skd.pgmanagement.networks.dataModel.GetHomeResponse
import com.skd.pgmanagement.networks.dataModel.GetIDCardResponse
import com.skd.pgmanagement.views.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainDashboardScreen : BaseActivity<ActivityMainDashboardBinding>(R.layout.activity_main_dashboard) {

    private var groupId: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var homeDataList: List<ActivityData>? = null
    private var profileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        groupId = intent.getStringExtra("PreferencesConstants.GROUP_ID")
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        showProgressBar()
        getHomeApi()
        getProfileApi()
        setupLogoutDialog()
    }

    private fun initToolbar() {
        binding.ivMore.setOnClickListener {
            homeDataList?.let {
                showPartialFullscreenDialog(it, profileName)
            } ?: Toast.makeText(this, "Data not loaded yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPartialFullscreenDialog(data: List<ActivityData>, userName: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_partial)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val metrics = resources.displayMetrics
        val width = (metrics.widthPixels * 0.7).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setGravity(Gravity.START)

        val tvDialogContent = dialog.findViewById<TextView>(R.id.tvDialogContent)
        tvDialogContent.text = userName ?: ""

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HomeCategoryAdapter(data)

        dialog.show()
    }


    private fun getHomeApi() {
        val groupApi = RetrofitClient.homeApiService(this)
        groupId?.let {
            groupApi.getHomeGroups(it).enqueue(object : Callback<GetHomeResponse> {
                override fun onResponse(call: Call<GetHomeResponse>, response: Response<GetHomeResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        dismissProgressBar()
                        val groupResponse = response.body()

                        groupResponse?.data?.let { activityList ->
                            // When ivMore is clicked, show dialog with API data
                            binding.ivMore.setOnClickListener {
                                showPartialFullscreenDialog(activityList, profileName)
                            }
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

    private fun getProfileApi() {
        val groupApi = RetrofitClient.profileApiService(this)
        groupId?.let {
            groupApi.getKidSProfile(it).enqueue(object : Callback<GetIDCardResponse> {
                override fun onResponse(call: Call<GetIDCardResponse>, response: Response<GetIDCardResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        dismissProgressBar()
                        val groupResponse = response.body()

                        groupResponse?.data?.let { profileList ->
                            profileName = profileList.firstOrNull()?.name
                        }
                    } else {
                        Toast.makeText(this@MainDashboardScreen, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GetIDCardResponse>, t: Throwable) {
                    Toast.makeText(this@MainDashboardScreen, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupLogoutDialog() {
        binding.ivDelete.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setCancelable(true)

            builder.setPositiveButton("OK") { dialog, _ ->
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


}