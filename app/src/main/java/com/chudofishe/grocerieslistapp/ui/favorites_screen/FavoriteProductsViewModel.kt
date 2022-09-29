package com.chudofishe.grocerieslistapp.ui.favorites_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteProductsViewModel @Inject constructor(
    private val shoppingItemDao: ShoppingItemDao,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(shoppingItemDao) {

    private var _productsList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val productsList = _productsList.asStateFlow()
    private var _isSelectionMode: Boolean = savedStateHandle["selectionMode"] ?: false
    val isSelectionMode: Boolean
        get() = _isSelectionMode

    init {
        viewModelScope.launch {
            shoppingItemDao.getAll().collect {
                _productsList.value = it
            }
        }
    }

    fun update(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemDao.update(item)
        }
    }

    fun delete(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemDao.delete(item)
        }
    }

    fun add(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemDao.insert(item)
        }
    }

}