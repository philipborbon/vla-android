package com.vla.sksu.app.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vla.sksu.app.manager.APIManager
import com.vla.sksu.app.manager.AuthorizationStore
import com.vla.sksu.app.manager.UserStore


open class BaseActivity : AppCompatActivity() {
    lateinit var apiManager: APIManager
    lateinit var userStore: UserStore
    lateinit var authorizationStore: AuthorizationStore
    lateinit var main: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userStore = UserStore.getInstance(this)
        authorizationStore = AuthorizationStore.getInstance(this)
        apiManager = APIManager.getInstance(this)

        main = Handler(mainLooper)
    }

    protected fun showToast(errorString: String?) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard() {
        currentFocus?.let {
            val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }
}