package com.chudofishe.grocerieslistapp.ui.history_screen

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.chudofishe.grocerieslistapp.ui.common.util.MarginItemDecoration
import com.chudofishe.grocerieslistapp.ui.common.util.fadeIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel>(), HistoryListAdapterActionsListener {

    private var _binding: FragmentHistoryListBinding? = null
    private val binding: FragmentHistoryListBinding
        get() = _binding!!

    private lateinit var historyList: RecyclerView
    private lateinit var adapter: HistoryListAdapter
    private lateinit var tooltip: TextView

    override val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)

        historyList = binding.historyList
        tooltip = binding.tooltipText

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.historyList.collect {
                    adapter.submitList(it)
                    if (it.isEmpty()) {
                        tooltip.setText(R.string.tooltip_history_list_screen)
                        tooltip.fadeIn(500)
                    } else {
                        tooltip.visibility = View.GONE
                    }
                }
            }
        }

        adapter = HistoryListAdapter(this)
        historyList.adapter = adapter
        historyList.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.card_spacing).toInt())
        )
    }

    override fun onRemoveButtonClicked(list: ShoppingList) {
        viewModel.delete(list)
    }

    override fun onFavoriteButtonClicked(item: ShoppingList) {
        viewModel.update(item)
    }

    override fun onSubListItemLongClicked(item: ShoppingItem) {
        viewModel.addShoppingItemToFavorites(item)
    }

    override fun onSetActiveButtonClicked(list: ShoppingList) {
        val directions = HistoryFragmentDirections.actionHistoryListDestinationToActiveListDestination(list)
        viewModel.navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}