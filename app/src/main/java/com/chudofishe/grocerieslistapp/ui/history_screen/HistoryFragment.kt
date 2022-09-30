package com.chudofishe.grocerieslistapp.ui.history_screen

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel>(), HistoryListAdapterActionsListener {

    private var _binding: FragmentHistoryListBinding? = null
    private val binding: FragmentHistoryListBinding
        get() = _binding!!

    private lateinit var historyList: RecyclerView
    private lateinit var adapter: HistoryListAdapter

    override val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)

        historyList = binding.historyList

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyList.collect {
                    adapter.submitList(it)
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history_list_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.action_delete_all -> {
                        AlertDialog.Builder(context)
                            .setMessage(R.string.question_delete_all)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                viewModel.deleteAll()
                            }
                            .setNegativeButton(R.string.no, null)
                            .create().show()
                    }
                    else -> viewModel.navigateBack()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

    override fun onSubListItemClicked(item: ShoppingItem) {
        viewModel.addShoppingItemToFavorites(item)
    }

    override fun onSetActiveButtonClicked(list: ShoppingList) {
        val directions = HistoryFragmentDirections.actionHistoryListDestinationToCurrentListDestination(list)
        viewModel.navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}