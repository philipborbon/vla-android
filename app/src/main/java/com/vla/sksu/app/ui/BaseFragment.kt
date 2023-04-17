package com.vla.sksu.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vla.sksu.app.manager.APIManager
import com.vla.sksu.app.manager.AuthorizationStore
import com.vla.sksu.app.manager.UserStore

open class BaseFragment : Fragment() {
    protected var apiManager: APIManager? = null
    protected var userStore: UserStore? = null
    protected var authorizationStore: AuthorizationStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = activity ?: return

        apiManager = (activity as? BaseActivity)?.apiManager
        userStore = (activity as? BaseActivity)?.userStore
        authorizationStore = (activity as? BaseActivity)?.authorizationStore
    }

    protected fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard() {
        (activity as? BaseActivity)?.hideKeyboard()
    }
}