package com.chudofishe.grocerieslistapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseViewModel (
    private val shoppingItemDao: ShoppingItemDao? = null
) : ViewModel(){

    private val _navigation = MutableSharedFlow<NavigationCommand>()
    val navigation = _navigation.asSharedFlow()

    fun navigate(navDirections: NavDirections) {
        viewModelScope.launch {
            _navigation.emit(NavigationCommand.To(navDirections))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigation.emit(NavigationCommand.Back)
        }
    }

    fun addShoppingItemToFavorites(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemDao?.insert(item)
        }
    }
}