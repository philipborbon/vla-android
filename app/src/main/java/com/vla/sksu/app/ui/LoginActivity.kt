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

                // TODO: remove after push token is implemented
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()

//                TODO: update push token
//                FirebaseInstanceId.getInstance().instanceId
//                    .addOnCompleteListener(OnCompleteListener { task ->
//                        if (!task.isSuccessful) {
//                            main.post {
//                                binding.viewLoading.visibility = View.GONE
//                                showToast(task.exception?.localizedMessage)
//                            }
//                            return@OnCompleteListener
//                        }
//
//                        // Get new Instance ID token
//                        val token = task.result?.token
//
//                        apiManager.updatePushToken(token) { response ->
//                            main.post { binding.viewLoading.visibility = View.GONE }
//
//                            if (response.success == true) {
//                                main.post {
//                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    startActivity(intent)
//
//                                    finish()
//                                }
//                            } else {
//                                main.post {
//                                    if (response.status == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                                        showToast(getString(R.string.invalid_username_password))
//                                    } else {
//                                        showToast(response.getErrorMessage())
//                                    }
//                                }
//                            }
//                        }
//                    })
            } else {
                if (response.status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    showToast(getString(R.string.message_invalid_username_password))
                } else {
                    showToast(response.getErrorMessage())
                }
            }
        }
    }
}
