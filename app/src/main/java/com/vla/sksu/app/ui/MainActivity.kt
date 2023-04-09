package com.vla.sksu.app.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.vla.sksu.app.R
import com.vla.sksu.app.databinding.ActivityMainBinding
import com.vla.sksu.app.databinding.NavHeaderMainBinding
import timber.log.Timber

private const val LOG_TAG = "MainActivity"

class MainActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted.not()) {
            Timber.tag(LOG_TAG).w("Notification permission request denied.")
        }
    }

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

        binding.navView.menu.findItem(R.id.action_logout).setOnMenuItemClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            logout()

            return@setOnMenuItemClickListener true
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

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (! task.isSuccessful) {
                Timber.tag(LOG_TAG).w(task.exception)
                return@OnCompleteListener
            }

            apiManager.updatePushToken(task.result) { response ->
                if (response.success) {
                    Timber.tag(LOG_TAG).v("Push token updated.")
                } else {
                    Timber.tag(LOG_TAG).e(response.error)
                    Timber.tag(LOG_TAG).e(response.errorString)
                }
            }
        })

        askNotificationPermission()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder(this)
                    .setMessage(R.string.message_ask_notification)
                    .setPositiveButton(R.string.text_ok) { dialog, id  ->
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton(R.string.text_no_thanks) {_, _, -> }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun logout() {
        binding.main.content.loading.visibility = View.VISIBLE

        apiManager.logout { response ->
            binding.main.content.loading.visibility = View.GONE

            if (response.success) {
                userStore.clear()
                authorizationStore.clear()

                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)

                finish()
            } else {
                Timber.tag(LOG_TAG).e(response.error)
                showToast(response.getErrorMessage())
            }
        }
    }
}