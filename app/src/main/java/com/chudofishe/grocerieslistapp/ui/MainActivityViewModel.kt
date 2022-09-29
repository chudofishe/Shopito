package com.chudofishe.grocerieslistapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import com.chudofishe.grocerieslistapp.ui.common.NavigationCommand
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : BaseViewModel() {

    private val _didWatchOnBoarding = MutableSharedFlow<Boolean>()
    val didWatchOnBoarding = _didWatchOnBoarding.asSharedFlow()

    init {
        viewModelScope.launch {
            _didWatchOnBoarding.emit(false)
        }
    }
}