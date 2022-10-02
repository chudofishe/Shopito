package com.chudofishe.grocerieslistapp.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.chudofishe.grocerieslistapp.R

class SharedPrefAppStore(private val appContext: Context) {

    companion object {
        private const val SHARED_PREF_WATCHED_ONBOARDING_KEY = "DID_WATCH_ONBOARDING"
    }

    private val SHARED_PREF_COLLAPSE_DONE = appContext.getString(R.string.settings_collapse_done_category)
    private val SHARED_PREF_COMPLETE_LISTS = appContext.getString(R.string.settings_complete_lists)
    private val SHARED_PREF_DELETE_LISTS_OLDER_THEN = appContext.getString(R.string.settings_storage_auto_delete)

    fun saveWatchedOnBoarding() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext).edit()
        sharedPreferences.putBoolean(SHARED_PREF_WATCHED_ONBOARDING_KEY, true)
        sharedPreferences.apply()
    }

    fun getWatchedOnBoarding(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        return sharedPreferences.getBoolean(SHARED_PREF_WATCHED_ONBOARDING_KEY, false)
    }

    fun getCollapseDone(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        return sharedPreferences.getBoolean(SHARED_PREF_COLLAPSE_DONE, true)
    }

    fun getCompleteLists(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        return sharedPreferences.getBoolean(SHARED_PREF_COMPLETE_LISTS, true)
    }

    fun getDeleteListOlderThen(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        return sharedPreferences.getString(SHARED_PREF_DELETE_LISTS_OLDER_THEN, "14")?.toInt() ?: 14
    }
}