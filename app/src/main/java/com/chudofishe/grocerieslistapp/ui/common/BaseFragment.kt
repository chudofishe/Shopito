package com.chudofishe.grocerieslistapp.ui.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.chudofishe.grocerieslistapp.ui.MainActivity
import com.chudofishe.grocerieslistapp.ui.common.util.NavigationCommand
import kotlinx.coroutines.launch


abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeNavigation()
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigation.collect {
                   when(it) {
                       is NavigationCommand.To -> findNavController().navigate(it.directions)
                       is NavigationCommand.Back -> (activity as MainActivity).onSupportNavigateUp()
                   }
                }
            }
        }
    }
}