package com.vla.sksu.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.vla.sksu.app.R
import com.vla.sksu.app.databinding.ActivityMainBinding
import com.vla.sksu.app.databinding.NavHeaderMainBinding
import java.net.HttpURLConnection


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.main.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.main.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        binding.navView.setNavigationItemSelectedListener(this)

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
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> logout()

            R.id.nav_home -> {
            }

            R.id.nav_category -> {
            }

            R.id.nav_account -> {
//                val intent = Intent(this, AccountActivity::class.java)
//                startActivity(intent)
            }

            R.id.nav_about -> {
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun logout(){
        apiManager.clearPushToken { response ->
            if (response.success == true) {
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