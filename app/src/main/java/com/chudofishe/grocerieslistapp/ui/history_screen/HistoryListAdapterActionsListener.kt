package com.chudofishe.grocerieslistapp.ui.history_screen

import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList

interface HistoryListAdapterActionsListener {
    fun onRemoveButtonClicked(list: ShoppingList) {}
    fun onFavoriteButtonClicked(item: ShoppingList)
    fun onSubListItemClicked(item: ShoppingItem)
    fun onSetActiveButtonClicked(list: ShoppingList)
}