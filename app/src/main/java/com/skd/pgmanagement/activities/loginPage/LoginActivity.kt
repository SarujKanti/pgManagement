package com.skd.pgmanagement.activities.loginPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.MainDashboardScreen
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.LoginActivityBinding
import com.skd.pgmanagement.fragments.login.LoginFragment
import com.skd.pgmanagement.views.BaseActivity

class LoginActivity : BaseActivity<LoginActivityBinding>(R.layout.login_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences(StringConstants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val token = sharedPrefs.getString(StringConstants.AUTH_TOKEN, null)
        val groupId = sharedPrefs.getString(StringConstants.GROUP_ID, null)

        if (!token.isNullOrEmpty()) {
            val intent = Intent(this, MainDashboardScreen::class.java)
            intent.putExtra(StringConstants.GROUP_ID, groupId)
            startActivity(intent)
            finish()
        } else {
            if (savedInstanceState == null) {
                loadLoginFragment()
            }
        }
    }

    private fun loadLoginFragment() {
        val fragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
