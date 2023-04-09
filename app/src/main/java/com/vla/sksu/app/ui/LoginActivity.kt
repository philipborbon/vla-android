package com.vla.sksu.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.vla.sksu.app.R
import com.vla.sksu.app.data.Authorization
import com.vla.sksu.app.databinding.ActivityLoginBinding
import java.net.HttpURLConnection

private const val LOG_TAG = "LoginActivity"

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.inputPassword.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doLogin()

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        binding.buttonLogin.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        hideKeyboard()

        binding.viewLoading.visibility = View.VISIBLE

        val username = binding.inputUsername.text.toString()
        val password = binding.inputPassword.text.toString()

        apiManager.login(username, password) { response ->
            binding.viewLoading.visibility = View.GONE

            if (response.success) {
                val data = response.data

                val authorization = Authorization(data)

                authorizationStore.username = username
                authorizationStore.password = password

                authorizationStore.setAuthorization(authorization)

                binding.viewLoading.visibility = View.VISIBLE

                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)

                finish()
            } else {
                if (response.status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    showToast(R.string.message_invalid_username_password)

                    binding.inputUsername.requestFocus()
                } else {
                    showToast(response.getErrorMessage())
                }
            }
        }
    }
}
