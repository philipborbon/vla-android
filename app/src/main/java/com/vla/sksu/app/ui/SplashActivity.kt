package com.vla.sksu.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vla.sksu.app.manager.AuthorizationStore

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authorizationStore: AuthorizationStore

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()

        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition { true }

        authorizationStore = AuthorizationStore.getInstance(this)

        if (authorizationStore.token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val mainIntent = Intent(this, MainActivity::class.java)

            if (intent.extras?.getString(MainActivity.DATA_ID) != null) {
                mainIntent.putExtra(MainActivity.DATA_ID, intent.extras?.getString(MainActivity.DATA_ID))
                mainIntent.putExtra(MainActivity.DATA_TYPE, intent.extras?.getString(MainActivity.DATA_TYPE))
            }

            startActivity(mainIntent)
            finish()
        }
    }
}
