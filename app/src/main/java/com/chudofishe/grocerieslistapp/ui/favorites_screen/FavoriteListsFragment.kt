package com.chudofishe.grocerieslistapp.ui.favorites_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.data.model.ShoppingItem
import com.chudofishe.grocerieslistapp.data.model.ShoppingList
import com.chudofishe.grocerieslistapp.databinding.FragmentHistoryListBinding
import com.chudofishe.grocerieslistapp.ui.common.BaseFragment
import com.chudofishe.grocerieslistapp.ui.history_screen.HistoryListAdapter
import com.chudofishe.grocerieslistapp.ui.history_screen.HistoryListAdapterActionsListener
import com.chudofishe.grocerieslistapp.ui.common.util.MarginItemDecoration
import com.chudofishe.grocerieslistapp.ui.common.util.fadeIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteListsFragment : BaseFragment<FavoriteListsViewModel>(), HistoryListAdapterActionsListener {

    private var _binding: FragmentHistoryListBinding? = null
    private val binding: FragmentHistoryListBinding
        get() = _binding!!

    private lateinit var favoritesList: RecyclerView
    private lateinit var adapter: HistoryListAdapter
    private lateinit var tooltip: TextView

    override val viewModel: FavoriteListsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)

        favoritesList = binding.historyList
        tooltip = binding.tooltipText

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesList.collect {
                    adapter.submitList(it)
                    if (it.isEmpty()) {
                        tooltip.setText(R.string.tooltip_favorite_list_screen)
                        tooltip.fadeIn(500)
                    } else {
                        tooltip.visibility = View.GONE
                    }
                }
            }
        }

        adapter = HistoryListAdapter(this, isFavoritesListAdapter = true)

        favoritesList.adapter = adapter
        favoritesList.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.card_spacing).toInt())
        )
    }

    override fun onFavoriteButtonClicked(item: ShoppingList) {
        viewModel.update(item)
    }

    override fun onSubListItemLongClicked(item: ShoppingItem) {
        viewModel.addShoppingItemToFavorites(item)
    }

    override fun onSetActiveButtonClicked(list: ShoppingList) {
        val directions = FavoritesFragmentDirections.actionFavoritesDestinationToActiveListDestination(list)
        viewModel.navigate(directions)
    }

    override fun onRemoveButtonClicked(list: ShoppingList) {
        viewModel.delete(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}