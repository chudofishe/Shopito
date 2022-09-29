package com.chudofishe.grocerieslistapp.ui.current_list_screen

import androidx.lifecycle.SavedStateHandle
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
    private val savedStateHandle: SavedStateHandle
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
            _activeListState.value = savedStateHandle["currentState"] ?: ShoppingList()
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
        clearState()
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

    fun removeItem(item: ShoppingItem) {
        val items = _activeListState.value.items.toMutableList()
        items.remove(item)
        updateItems(items)
    }

    fun removeItemsList(list: List<ShoppingItem>) {
        val items = _activeListState.value.items.toMutableList()
        items.removeAll(list)
        updateItems(items)
    }

    fun addItem(item: ShoppingItem) {
        val items = _activeListState.value.items.toMutableList()
        items.add(item)
        updateItems(items)
    }

    fun updateItem(item: ShoppingItem) {
        val items = _activeListState.value.items.toMutableList()
        items.remove(item)
        items.add(item.apply {
            currentCategory = if (currentCategory == Category.DONE) originalCategory else Category.DONE
        })
        updateItems(items)
    }

    fun updateItemsList(list: List<ShoppingItem>) {
        val items = _activeListState.value.items.toMutableList()
        items.removeAll(list)
        list.forEach {
            it.currentCategory = if (it.currentCategory == Category.DONE) it.originalCategory else Category.DONE
        }
        items.addAll(list)
        updateItems(items)
    }

    fun clearState() {
        viewModelScope.launch {
            _activeListState.value = ShoppingList()
        }
    }

    private fun updateItems(items: List<ShoppingItem>) {
        viewModelScope.launch {
            _activeListState.update { it.copy(items = items) }
        }
    }

    fun setTitle(title: String) {

    }

    private fun saveCurrentState(list: ShoppingList) {
        savedStateHandle["currentState"] = list
    }

    override fun onCleared() {
        saveCurrentState(_activeListState.value)
    }

}