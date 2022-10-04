package com.chudofishe.grocerieslistapp.ui.active_list_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.SharedPrefAppStore
import com.chudofishe.grocerieslistapp.data.SharedPrefDataStore
import com.chudofishe.grocerieslistapp.data.dao.ShoppingItemDao
import com.chudofishe.grocerieslistapp.data.dao.ShoppingListDao
import com.chudofishe.grocerieslistapp.data.model.Category
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ActiveListViewModel @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    private val sharedPrefDataStore: SharedPrefDataStore,
    shoppingItemDao: ShoppingItemDao,
    savedStateHandle: SavedStateHandle,
    sharedPrefAppStore: SharedPrefAppStore
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

    companion object {
        private const val SELECTED_ITEMS = "selectedFavItems"
        private const val HISTORY_LIST = "historyList"
    }

    /* Nav arg */
    private val historizedList: ShoppingList? = savedStateHandle[HISTORY_LIST]
    /* Nav arg */
    private val selectedItems: Array<ShoppingItem>? = savedStateHandle[SELECTED_ITEMS]

    val collapseDoneCategory: Boolean = sharedPrefAppStore.getCollapseDone()
    private val autoCompleteList: Boolean = sharedPrefAppStore.getCompleteLists()

    private val _activeListState = MutableStateFlow(ActiveStateWrapper())
    val activeListState = _activeListState.asStateFlow()

    private val _showOnCompletedDialog = MutableSharedFlow<Boolean>()
    val showOnCompletedDialog = _showOnCompletedDialog.asSharedFlow()

    /* Get data/state from savedStateHandle and serialized state from shared prefs */
    init {
        viewModelScope.launch {
            if (historizedList != null) {
                _activeListState.value = ActiveStateWrapper(
                    state = historizedList.apply { id = 0; isFavorite = false; resetDoneItems() },
                    listStatus = if (historizedList.items.isEmpty()) ListStatus.EMPTY else ListStatus.ACTIVE
                )
            } else {
                val state = getSavedStateFromPrefs()
                _activeListState.value = ActiveStateWrapper(
                    state = state,
                    listStatus = if (state.items.isEmpty()) ListStatus.EMPTY else ListStatus.ACTIVE
                )
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

    private fun getSavedStateFromPrefs(): ShoppingList {
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
        items.add(item.apply {
            currentCategory =
                if (currentCategory == Category.DONE) originalCategory else Category.DONE
        })
        updateItemsState(items)
    }

    fun updateItemsList(list: List<ShoppingItem>) {
        val items = _activeListState.value.state.items.toMutableList()
        items.removeAll(list)
        list.forEach {
            it.currentCategory =
                if (it.currentCategory == Category.DONE) it.originalCategory else Category.DONE
        }
        items.addAll(list)
        updateItemsState(items)
    }

    fun clearItemsList() {
        updateItemsState(emptyList())
    }

    fun completeItemsList() {
        if (_activeListState.value.state.items.isNotEmpty()) {
            onListCompleted(_activeListState.value.state)
        }
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
            _activeListState.value = ActiveStateWrapper(listStatus = ListStatus.EMPTY)
            _showOnCompletedDialog.emit(true)
        }
    }

    private fun updateState(state: ShoppingList, updateListStatus: Boolean = true) {
        if (state.items.isEmpty()) {
            onListCleared()
        } else if (state.isCompleted() && autoCompleteList) {
            onListCompleted(state)
        } else if (updateListStatus) {
            _activeListState.value =
                ActiveStateWrapper(state = state, listStatus = ListStatus.ACTIVE)
        } else {
            _activeListState.value =
                ActiveStateWrapper(state = state, listStatus = _activeListState.value.listStatus)
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveCurrentStateToPrefs()
        Log.d(javaClass.name, "onCleared")
    }
}
