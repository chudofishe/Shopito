package com.chudofishe.grocerieslistapp.data

import android.content.Context
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.util.Constants
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class SharedPrefDataStore(private val appContext: Context) {

    companion object {
        const val SHARED_PREF_FILE_KEY = "com.chudofishe.grocerieslistapp.SHARED_PREF_KEY"
        const val SHARED_PREF_ACTIVE_STATE_KEY = "ACTIVE_LIST_STATE"
    }

    fun saveTempState(list: ShoppingList) {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.putString(SHARED_PREF_ACTIVE_STATE_KEY, Json.encodeToString(list))
        sharedPreferences.apply()
    }

    fun getTempState(): ShoppingList? {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE)
        val state = sharedPreferences.getString(SHARED_PREF_ACTIVE_STATE_KEY, null)
        state?.let {
            return Json.decodeFromString<ShoppingList>(state)
        }
        return null
    }

    fun clearTempState() {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.remove(SHARED_PREF_ACTIVE_STATE_KEY)
        sharedPreferences.apply()
    }
}