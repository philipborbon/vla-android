package com.vla.sksu.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.vla.sksu.app.R
import com.vla.sksu.app.databinding.ActivityMainBinding
import com.vla.sksu.app.databinding.NavHeaderMainBinding
import java.net.HttpURLConnection


class MainActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.main.toolbar)

        val headerView = binding.navView.getHeaderView(0)
        val headerViewBinding = NavHeaderMainBinding.bind(headerView)

        headerViewBinding.displayUsername.text = getString(R.string.text_label_username, userStore.libraryId ?: "")
        headerViewBinding.displayName.text = userStore.name

        apiManager.whoAmI {
            if (it.success && it.data != null) {
                userStore.setUser(it.data!!)

                headerViewBinding.displayUsername.text = getString(R.string.text_label_username, userStore.libraryId ?: "")
                headerViewBinding.displayName.text = userStore.name ?: ""
            }
        }

        // --

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_category,
                R.id.nav_account,
                R.id.nav_about,
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logout() {
        apiManager.clearPushToken { response ->
            if (response.success) {
                main.post {
                    userStore.clear()
                    authorizationStore.clear()

                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            } else {
                main.post {
                    if (response.status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        showToast(getString(R.string.invalid_username_password))
                    } else {
                        showToast(response.getErrorMessage())
                    }
                }
            }
        }
    }
}