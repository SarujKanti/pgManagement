package com.skd.pgmanagement.activities

import android.app.AlertDialog
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.loginPage.LoginActivity
import com.skd.pgmanagement.adapters.HomeCategoryAdapter
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.ActivityMainDashboardBinding
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.ActivityData
import com.skd.pgmanagement.utils.showToast
import com.skd.pgmanagement.views.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainDashboardScreen : BaseActivity<ActivityMainDashboardBinding>(R.layout.activity_main_dashboard) {

    private var groupId: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var homeDataList: List<ActivityData>? = null
    private var profileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(StringConstants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        groupId = intent.getStringExtra(StringConstants.GROUP_ID)
        initToolbar()
        setupLogoutDialog()
        showProgressBar()
        loadDashboardData()
    }

    /**  Handles Toolbar Actions */
    private fun initToolbar() {
        binding.ivMore.setOnClickListener {
            homeDataList?.let {
                showPartialFullscreenDialog(it, profileName)
            } ?: showToast(getString(R.string.data_not_loaded))
        }
    }

    /**  Loads both home & profile data asynchronously */
    private fun loadDashboardData() {
        lifecycleScope.launch {
            try {
                val homeDeferred = launch { loadHomeData() }
                val profileDeferred = launch { loadProfileData() }

                homeDeferred.join()
                profileDeferred.join()
            } finally {
                dismissProgressBar()
            }
        }
    }

    /**  Fetch Home Data using coroutine */
    private suspend fun loadHomeData() {
        groupId?.let { id ->
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.homeApiService(this@MainDashboardScreen).getHomeGroupsSuspend(id)
                }
                if (response.isSuccessful && response.body() != null) {
                    homeDataList = response.body()?.data
                } else {
                    showToast("${getString(R.string.txt_error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("${getString(R.string.txt_failure)}:: ${e.localizedMessage}")
            }
        }
    }

    /**  Fetch Profile Data using coroutine */
    private suspend fun loadProfileData() {
        groupId?.let { id ->
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.profileApiService(this@MainDashboardScreen).getKidSProfileSuspend(id)
                }
                if (response.isSuccessful && response.body() != null) {
                    profileName = response.body()?.data?.firstOrNull()?.name
                } else {
                    showToast("${getString(R.string.txt_error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("${getString(R.string.txt_failure)}:: ${e.localizedMessage}")
            }
        }
    }

    /**  Shows Home Dialog with Data */
    private fun showPartialFullscreenDialog(data: List<ActivityData>, userName: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_partial)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout((resources.displayMetrics.widthPixels * 0.7).toInt(), WindowManager.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.START)
        }

        val tvDialogContent = dialog.findViewById<TextView>(R.id.tvDialogContent)
        tvDialogContent.text = userName ?: ""

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HomeCategoryAdapter(data)

        dialog.show()
    }

    /**  Logout Dialog */
    private fun setupLogoutDialog() {
        binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.txt_Logout))
                setMessage(getString(R.string.txt_sure_logout))
                setCancelable(true)

                setPositiveButton(getString(R.string.txt_ok)) { dialog, _ ->
                    sharedPreferences.edit().clear().apply()
                    showToast(getString(R.string.txt_success_logout))
                    dialog.dismiss()

                    val intent = Intent(this@MainDashboardScreen, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                setNegativeButton(getString(R.string.txt_Cancel)) { dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }
}
