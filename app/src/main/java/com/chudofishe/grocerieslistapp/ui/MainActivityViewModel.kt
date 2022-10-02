package com.chudofishe.grocerieslistapp.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.SharedPrefAppStore
import com.chudofishe.grocerieslistapp.data.dao.ShoppingListDao
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sharedPrefAppStore: SharedPrefAppStore,
    private val shoppingListDao: ShoppingListDao
) : BaseViewModel() {

    private val _didWatchOnBoarding = MutableSharedFlow<Boolean>()
    val didWatchOnBoarding = _didWatchOnBoarding.asSharedFlow()

    init {
        viewModelScope.launch {
            _didWatchOnBoarding.emit(sharedPrefAppStore.getWatchedOnBoarding())
            deleteOldLists()
        }
    }

    private suspend fun deleteOldLists() {
        val value = sharedPrefAppStore.getDeleteListOlderThen()
        Log.d(javaClass.name, "deleteOldLists value: $value")
        shoppingListDao.deleteOldLists(epochDay = LocalDate.now().toEpochDay() - value)
    }
}