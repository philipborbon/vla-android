package com.vla.sksu.app.manager

import android.content.Context
import android.content.SharedPreferences
import com.vla.sksu.app.BuildConfig
import com.vla.sksu.app.common.SingletonHolder
import com.vla.sksu.app.data.User


class UserStore private constructor(context: Context) {
    private val preference: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    companion object : SingletonHolder<UserStore, Context>(::UserStore) {
        const val PREFS_FILENAME = "${BuildConfig.APPLICATION_ID}.userstore"
        const val ID = "data-id"
        const val FIRSTNAME = "data-name"
        const val LIBRARY_ID = "data-library-id"
    }

    var id: Int
        get() = preference.getInt(ID, -1)
        set(value) = preference.edit().putInt(ID, value).apply()

    var name: String?
        get() = preference.getString(FIRSTNAME, null)
        set(value) = preference.edit().putString(FIRSTNAME, value).apply()

    var libraryId: String?
        get() = preference.getString(LIBRARY_ID, null)
        set(value) = preference.edit().putString(LIBRARY_ID, value).apply()

    fun setUser(user: User){
        id = user.id ?: -1
        name = user.name
        libraryId = user.libraryId
    }

    fun getUser(): User {
        return User (
            id = id,
            name = name,
            libraryId = libraryId,
        )
    }

    fun clear(){
        preference.edit().clear().apply()
    }
}