package com.chudofishe.grocerieslistapp.ui.history_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.databinding.HistoryCardBinding
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.ui.common.BaseCardViewHolder
import com.chudofishe.grocerieslistapp.ui.common.ItemsListAdapter
import com.chudofishe.grocerieslistapp.ui.common.ItemsListAdapterActionsListener
import com.chudofishe.grocerieslistapp.ui.common.ItemsListAdapterItemType
import com.chudofishe.grocerieslistapp.ui.common.util.formatDateToDays

class HistoryListAdapter(private val listener: HistoryListAdapterActionsListener,
                         private val isFavoritesListAdapter: Boolean = false)
    : ListAdapter<ShoppingList, HistoryListAdapter.ViewHolder>(ShoppingListDiffCallback()), ItemsListAdapterActionsListener {

    companion object {
        private const val COLLAPSED_ITEM_COUNT = 3
    }

    class ShoppingListDiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shoppingList = getItem(position)
        holder.bind(shoppingList)
    }

    override fun onItemLongClicked(item: ShoppingItem) {
        listener.onSubListItemLongClicked(item)
    }

    inner class ViewHolder(private val binding: HistoryCardBinding) : BaseCardViewHolder(binding.root) {
        private lateinit var adapter: ItemsListAdapter
        private lateinit var item: ShoppingList
        private var collapsed: Boolean = false

        override val toggleCollapse = View.OnClickListener {
            if (collapsed) {
                adapter.setItemsList(item.items)
                binding.collapseButton.apply {
                    text = context.getString(R.string.hide)
                }
            } else {
                adapter.setItemsList(item.items.subList(0, COLLAPSED_ITEM_COUNT))
                binding.collapseButton.apply {
                    text = context.getString(R.string.n_more_items,
                        item.items.size - COLLAPSED_ITEM_COUNT
                    )
                }
            }
            collapsed = !collapsed
        }

        override val showPopUpMenu = View.OnClickListener {
            PopupMenu(it.context, binding.optionsIb).apply {
                inflate(R.menu.historized_card_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.historized_menu_delete -> {
                            showConfirmAlertDialog(R.string.question_delete) { _, _ ->
                                listener.onRemoveButtonClicked(item)
                            }
                        }
                        R.id.historized_menu_set_active -> {
                            listener.onSetActiveButtonClicked(item)
                        }
                    }
                    true
                }
            }.show()
        }

        private val favoriteIBOnClickListener = View.OnClickListener {
            item.isFavorite = !item.isFavorite

            listener.onFavoriteButtonClicked(item)

            if (isFavoritesListAdapter) {
                val newList = mutableListOf<ShoppingList>().apply {
                    addAll(currentList)
                    remove(item)
                }
                submitList(newList)
            } else {
                binding.favoriteIb.setImageResource(if (item.isFavorite) {
                    R.drawable.ic_baseline_favorite_24
                } else {
                    R.drawable.ic_baseline_favorite_border_24
                })
            }
        }

        fun bind(item: ShoppingList) {
            this.item = item
            val displayedItems = if (item.items.size > COLLAPSED_ITEM_COUNT) {
                collapsed = true
                item.items.subList(0, COLLAPSED_ITEM_COUNT)
            } else {
                item.items
            }.toMutableList()

            adapter = ItemsListAdapter(ItemsListAdapterItemType.HISTORIZED, this@HistoryListAdapter)
            adapter.setItemsList(displayedItems)

            binding.apply {
                itemsList.adapter = adapter
                if (item.items.size > COLLAPSED_ITEM_COUNT) {
                    collapseButton.apply {
                        setOnClickListener(toggleCollapse)
                        text = context.getString(R.string.n_more_items,
                            item.items.size - COLLAPSED_ITEM_COUNT
                        )
                    }
                } else {
                    collapseButton.visibility = View.GONE
                }
                if (item.title != null) {
                    title.visibility = View.VISIBLE
                    title.text = item.title
                } else {
                    title.visibility = View.GONE
                }

                optionsIb.setOnClickListener(showPopUpMenu)
                favoriteIb.setImageResource(if (item.isFavorite) {
                    R.drawable.ic_baseline_favorite_24
                } else {
                    R.drawable.ic_baseline_favorite_border_24
                })
                favoriteIb.setOnClickListener(favoriteIBOnClickListener)
                dateText.text = formatDateToDays(itemView.context, item.date)
            }
        }
    }
}
