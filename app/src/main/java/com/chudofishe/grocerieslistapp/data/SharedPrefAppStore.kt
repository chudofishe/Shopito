package com.chudofishe.grocerieslistapp.data

import android.content.Context

class SharedPrefAppStore(private val appContext: Context) {

    companion object {
        const val SHARED_PREF_FILE_KEY = "com.chudofishe.grocerieslistapp.SHARED_PREF_KEY"
        const val SHARED_PREF_WATCHED_ONBOARDING_KEY = "DID_WATCH_ONBOARDING"
    }

    fun saveWatchedOnBoarding() {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.putBoolean(SHARED_PREF_WATCHED_ONBOARDING_KEY, true)
        sharedPreferences.apply()
    }

    fun getWatchedOnBoarding(): Boolean {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(SHARED_PREF_WATCHED_ONBOARDING_KEY, false)
    }
}