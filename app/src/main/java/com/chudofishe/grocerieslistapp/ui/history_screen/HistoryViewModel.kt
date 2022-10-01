package com.chudofishe.grocerieslistapp.ui.history_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.dao.ShoppingListDao
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    shoppingItemDao: ShoppingItemDao
) : BaseViewModel(shoppingItemDao) {

    private var _historyList = MutableStateFlow<List<ShoppingList>>(emptyList())
    val historyList = _historyList.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingListDao.getAll().collect {
                _historyList.value = it
            }
        }
    }

    fun delete(list: ShoppingList) {
        viewModelScope.launch {
            shoppingListDao.delete(list)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            shoppingListDao.deleteAll()
        }
    }

    fun update(list: ShoppingList) {
        viewModelScope.launch {
            shoppingListDao.update(list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(this.javaClass.name, "onCleared")
    }
}