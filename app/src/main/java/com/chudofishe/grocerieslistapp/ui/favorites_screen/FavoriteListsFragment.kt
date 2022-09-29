package com.chudofishe.grocerieslistapp.ui.favorites_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteListsFragment : BaseFragment<FavoriteListsViewModel>() {

    private var _binding: FragmentHistoryListBinding? = null
    private val binding: FragmentHistoryListBinding
        get() = _binding!!

    private lateinit var favoritesList: RecyclerView
    private lateinit var adapter: HistoryListAdapter

    override val viewModel: FavoriteListsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)

        favoritesList = binding.historyList

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesList.collect {
                    adapter.submitList(it)
                }
            }
        }

        adapter = HistoryListAdapter(object : HistoryListAdapterActionsListener {
            override fun update(item: ShoppingList) {
                viewModel.update(item)
            }

            override fun onSubListItemClicked(item: ShoppingItem) {
                viewModel.addShoppingItemToFavorites(item)
            }

            override fun navigate(itemId: Long) {
                val directions = FavoriteListsFragmentDirections.actionFavoriteListsDestinationToCurrentListDestination(itemId)
                viewModel.navigate(directions)
            }
        }, isFavoritesListAdapter = true)

        favoritesList.adapter = adapter
        favoritesList.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.card_spacing).toInt())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}