package com.chudofishe.grocerieslistapp.ui.active_list_screen

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
    savedStateHandle: SavedStateHandle
) : BaseViewModel(shoppingItemDao) {

    /* Wrap state to emit data on each update and keep track of list status */

    data class ActiveStateWrapper(
        private val id: UUID = UUID.randomUUID(),
        val state: ShoppingList = ShoppingList(),
        val listStatus: ListStatus = ListStatus.EMPTY
    )

    enum class ListStatus {
        EMPTY, ACTIVE
    }

    private var status = ListStatus.EMPTY

    private val _activeListState = MutableStateFlow(ActiveStateWrapper())
    val activeListState = _activeListState.asStateFlow()

    private val _showOnCompletedDialog = MutableSharedFlow<Boolean>()
    val showOnCompletedDialog = _showOnCompletedDialog.asSharedFlow()

    /* Get data/state from savedStateHandle via nav args and serialized state from shared prefs */
    init {
        val currentState = getCurrentStateFromPrefs()
        val historizedList: ShoppingList? = savedStateHandle["list"]
        val selectedItems: Array<ShoppingItem>? = savedStateHandle["selectedFavItems"]
        viewModelScope.launch {
            if (historizedList != null) {
                _activeListState.value = ActiveStateWrapper(
                    state = historizedList.apply { id = 0; resetDoneItems() },
                    listStatus = ListStatus.ACTIVE)
            } else {
                _activeListState.value = ActiveStateWrapper(
                    state = currentState,
                    listStatus = if (currentState.items.isEmpty()) ListStatus.EMPTY else ListStatus.ACTIVE)
            }
            selectedItems?.let {
                addItemsList(it.toList())
            }
        }
    }

    private fun saveStateToDB(list: ShoppingList) {
        viewModelScope.launch {
            shoppingListDao.insert(list.apply { date = LocalDate.now() })
        }
    }

    fun saveCurrentStateToPrefs() {
        sharedPrefDataStore.saveTempState(_activeListState.value.state)
    }

    private fun getCurrentStateFromPrefs(): ShoppingList {
        return sharedPrefDataStore.getTempState() ?: ShoppingList()
    }

    /* START MODIFY STATE ITEMS LIST */

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
        val items = _activeListState.value.state.items.toMutableList()
        items.removeAll(list)
        list.forEach {
            it.currentCategory = if (it.currentCategory == Category.DONE) it.originalCategory else Category.DONE
        }
        items.addAll(list)
        updateItemsState(items)
    }

    fun clearItemsList() {
        updateItemsState(emptyList())
    }

    fun completeItemsList() {
        onListCompleted(_activeListState.value.state)
    }

    private fun updateItemsState(items: List<ShoppingItem>) {
        updateState(_activeListState.value.state.also { it.items = items })
    }

    fun updateTitle(title: String?) {
        updateState(_activeListState.value.state.also { it.title = title }, false)
    }

    /* END MODIFY STATE ITEMS LIST */

    private fun onListCleared() {
        viewModelScope.launch {
            _activeListState.value = ActiveStateWrapper(listStatus = ListStatus.EMPTY)
        }
    }

    private fun onListCompleted(state: ShoppingList) {
        viewModelScope.launch {
            saveStateToDB(state)
            _activeListState.value = ActiveStateWrapper()
            _showOnCompletedDialog.emit(true)
        }
    }

    private fun updateState(state: ShoppingList, updateListStatus: Boolean = true) {
        if (state.items.isEmpty()) {
            onListCleared()
        } else if (state.isCompleted()) {
            onListCompleted(state)
        } else if (updateListStatus){
            _activeListState.value = ActiveStateWrapper(state = state, listStatus = ListStatus.ACTIVE)
        } else {
            _activeListState.value = ActiveStateWrapper(state = state, listStatus = _activeListState.value.listStatus)
        }
    }
}