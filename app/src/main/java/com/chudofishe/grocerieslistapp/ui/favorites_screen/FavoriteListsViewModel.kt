package com.chudofishe.grocerieslistapp.ui.favorites_screen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.dao.ShoppingListDao
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListsViewModel @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    shoppingItemDao: ShoppingItemDao
) : BaseViewModel(shoppingItemDao) {

    private var _favoritesList = MutableStateFlow<List<ShoppingList>>(emptyList())
    val favoritesList = _favoritesList.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingListDao.getFavorites(isFavorite = true).collect {
                _favoritesList.value = it
            }
        }
    }

    fun update(list: ShoppingList) {
        viewModelScope.launch {
            shoppingListDao.update(list)
        }
    }

    fun delete(list: ShoppingList) {
        viewModelScope.launch {
            shoppingListDao.delete(list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(this.javaClass.name, "onCleared")
    }
}