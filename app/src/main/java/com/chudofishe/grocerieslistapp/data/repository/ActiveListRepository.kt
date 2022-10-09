package com.chudofishe.grocerieslistapp.data.repository

import android.content.Context
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ActiveListRepository(private val appContext: Context) {

    companion object {
        private const val SHARED_PREF_FILE_KEY = "com.chudofishe.grocerieslistapp.SHARED_PREF_KEY"
        private const val SHARED_PREF_ACTIVE_STATE_KEY = "ACTIVE_LIST_STATE"
    }

    fun saveTempState(list: ShoppingList) {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE).edit()
        sharedPreferences.putString(SHARED_PREF_ACTIVE_STATE_KEY, Json.encodeToString(list))
        sharedPreferences.apply()
    }

    suspend fun getTempState() = flow {
        val sharedPreferences =
            appContext.getSharedPreferences(SHARED_PREF_FILE_KEY, Context.MODE_PRIVATE)
        val state = sharedPreferences.getString(SHARED_PREF_ACTIVE_STATE_KEY, null)
        if (state != null) {
            emit(Json.decodeFromString<ShoppingList>(state))
        } else {
            emit(null)
        }
    }
}