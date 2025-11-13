package com.skd.pgmanagement.activities.staffRegister

import android.os.Bundle
import android.util.Log
import com.skd.pgmanagement.R
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.CommonActivityBinding
import com.skd.pgmanagement.fragments.staffRegister.StaffRegisterFragment
import com.skd.pgmanagement.views.BaseActivity

class StaffRegisterActivity : BaseActivity<CommonActivityBinding>(R.layout.common_activity) {

    private var groupId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        getBundleData()
        if (savedInstanceState == null) {
            getFragmentBundleData()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.childToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = resources.getString(R.string.staff_register)
    }

    private fun getBundleData() {
        groupId = intent.getStringExtra(StringConstants.GROUP_ID)
    }

    private fun getFragmentBundleData() {
        val fragment = StaffRegisterFragment()
        val bundle = Bundle().apply {
            putString(StringConstants.GROUP_ID, groupId)
        }
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}