package com.chudofishe.grocerieslistapp.ui.current_list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.SharedPrefDataStore
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.dao.ShoppingListDao
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActiveListViewModel @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    private val sharedPrefDataStore: SharedPrefDataStore,
    shoppingItemDao: ShoppingItemDao,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(shoppingItemDao) {

    private val _didWatchOnBoarding = MutableSharedFlow<Boolean>()
    val didWatchOnBoarding = _didWatchOnBoarding.asSharedFlow()

    private val _activeListState = MutableStateFlow(ShoppingList())
    val activeListState = _activeListState.asStateFlow()

    private val _selectedItems = MutableSharedFlow<Array<ShoppingItem>>()
    val selectedItems = _selectedItems.asSharedFlow()


    init {
        val historyListId: Long? = savedStateHandle["listId"]
        val selectedItems: Array<ShoppingItem>? = savedStateHandle["selectedFavItems"]
        viewModelScope.launch {
//            _activeListState.value = historyListId?.let { getHistorizedListState(it) } ?: getTempState()
            selectedItems?.let {
                this@ActiveListViewModel._selectedItems.emit(it)
            }
        }
    }

    fun saveList(title: String?, list: List<ShoppingItem>) {
        val newList = ShoppingList(
            title = title,
            date = LocalDate.now(),
            items = list
        )

        viewModelScope.launch {
            shoppingListDao.insert(newList)
        }
    }

    fun saveTempState(title: String?, items: List<ShoppingItem>) {
        viewModelScope.launch {
            val state = ShoppingList(
                title = title,
                items = items
            )
            sharedPrefDataStore.saveTempState(state)
            _activeListState.emit(state)
        }
    }

    private suspend fun getHistorizedListState(id: Long): ShoppingList? {
        val list = shoppingListDao.getById(id)
        list?.let { it.items.forEach { item ->
            if (item.currentCategory == Category.DONE) item.currentCategory = item.originalCategory
        } }
        return list
    }

    private fun getTempState(): ShoppingList {
        return sharedPrefDataStore.getTempState() ?: ShoppingList()
    }

    override fun onCleared() {
        super.onCleared()
    }

}