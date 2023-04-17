package com.vla.sksu.app.ui

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.vla.sksu.app.manager.APIManager
import com.vla.sksu.app.manager.AuthorizationStore
import com.vla.sksu.app.manager.UserStore


open class BaseActivity : AppCompatActivity() {
    lateinit var apiManager: APIManager
    lateinit var userStore: UserStore
    lateinit var authorizationStore: AuthorizationStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userStore = UserStore.getInstance(this)
        authorizationStore = AuthorizationStore.getInstance(this)
        apiManager = APIManager.getInstance(this)
    }

    protected fun showToast(errorString: String?, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, errorString, duration).show()
    }

    protected fun showToast(@StringRes errorString: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, errorString, duration).show()
    }

    fun hideKeyboard() {
        currentFocus ?: return

        val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}