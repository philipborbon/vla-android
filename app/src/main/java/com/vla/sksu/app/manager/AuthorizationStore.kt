package com.vla.sksu.app.manager

import android.content.Context
import android.content.SharedPreferences
import com.vla.sksu.app.BuildConfig
import com.vla.sksu.app.common.SingletonHolder
import com.vla.sksu.app.data.Authorization

class AuthorizationStore private constructor(context: Context){
    private val preference: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    companion object : SingletonHolder<AuthorizationStore, Context>(::AuthorizationStore) {
        const val PREFS_FILENAME = "${BuildConfig.APPLICATION_ID}.authentication.store"
        const val TOKEN_TYPE = "data-token-type"
        const val TOKEN = "data-token"
        const val USERNAME = "data-username"
        const val PASSWORD = "data-password"
    }

    var tokenType: String?
        get() = preference.getString(TOKEN_TYPE, null)
        set(value) = preference.edit().putString(TOKEN_TYPE, value).apply()

    var token: String?
        get() = preference.getString(TOKEN, null)
        set(value) = preference.edit().putString(TOKEN, value).apply()

    var username: String?
        get() = preference.getString(USERNAME, null)
        set(value) = preference.edit().putString(USERNAME, value).apply()

    var password: String?
        get() = preference.getString(PASSWORD, null)
        set(value) = preference.edit().putString(PASSWORD, value).apply()

    fun getAuthorization(): Authorization {
        return Authorization (
            tokentype = tokenType,
            token = token,
        )
    }

    fun setAuthorization(authorization: Authorization){
        tokenType = authorization.tokentype
        token = authorization.token
    }

    fun clear(){
        preference.edit().clear().apply()
    }
}