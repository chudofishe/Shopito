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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ActiveListViewModel @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    private val sharedPrefDataStore: SharedPrefDataStore,
    shoppingItemDao: ShoppingItemDao,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(shoppingItemDao) {

    data class ActiveStateWrapper(
        private val id: UUID = UUID.randomUUID(),
        val state: ShoppingList = ShoppingList(),
    )

    private val _didWatchOnBoarding = MutableSharedFlow<Boolean>()
    val didWatchOnBoarding = _didWatchOnBoarding.asSharedFlow()

    private val _activeListState = MutableStateFlow(ActiveStateWrapper())
    val activeListState = _activeListState.asStateFlow()

    private val _selectedItems = MutableSharedFlow<Array<ShoppingItem>>()
    val selectedItems = _selectedItems.asSharedFlow()


    init {
        val historizedList: ShoppingList? = savedStateHandle["list"]
        val currentState: ShoppingList? = savedStateHandle["currentState"]
        val selectedItems: Array<ShoppingItem>? = savedStateHandle["selectedFavItems"]
        viewModelScope.launch {
            if (historizedList != null) {
                _activeListState.value = ActiveStateWrapper(state = historizedList)
            } else if (currentState != null) {
                _activeListState.value = ActiveStateWrapper(state = currentState)
            } else {
                _activeListState.value = ActiveStateWrapper()
            }
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
//            _activeListState.emit(state)
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
        val items = _activeListState.value.state.items.toMutableList()
        items.remove(item)
        updateItemsState(items)
    }

    fun removeItemsList(list: List<ShoppingItem>) {
        val items = _activeListState.value.state.items.toMutableList()
        items.removeAll(list)
        updateItemsState(items)
    }

    fun addItem(item: ShoppingItem) {
        val items = _activeListState.value.state.items.toMutableList()
        items.add(item)
        updateItemsState(items)
    }

    private fun addItemsList(list: List<ShoppingItem>) {
        val items = _activeListState.value.state.items.toMutableList()
        items.addAll(list)
        updateItemsState(items)
    }

    fun updateItem(item: ShoppingItem) {
        val items = _activeListState.value.state.items.toMutableList()
        items.remove(item)
        items.add(item.apply { currentCategory = if (currentCategory == Category.DONE) originalCategory else Category.DONE })
        updateItemsState(items)
    }

    fun updateItemsList(list: List<ShoppingItem>) {
        removeItemsList(list)
        list.forEach {
            it.currentCategory = if (it.currentCategory == Category.DONE) it.originalCategory else Category.DONE
        }
        addItemsList(list)
    }

    fun clearState() {
        viewModelScope.launch {
            _activeListState.value = ActiveStateWrapper()
        }
    }

    private fun updateItemsState(items: List<ShoppingItem>) {
        val state = _activeListState.value.state.also { it.items = items }
        _activeListState.value = ActiveStateWrapper(state = state)
    }

    fun setTitle(title: String) {

    }

    private fun saveCurrentState(list: ShoppingList) {
        savedStateHandle["currentState"] = list
    }

    override fun onCleared() {
        saveCurrentState(_activeListState.value.state)
    }

}