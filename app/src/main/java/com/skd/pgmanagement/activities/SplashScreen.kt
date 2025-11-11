package com.skd.pgmanagement.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.loginPage.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<TextView>(R.id.txt_appName).text = getString(R.string.app_name)

        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            finish()
        }
    }
}