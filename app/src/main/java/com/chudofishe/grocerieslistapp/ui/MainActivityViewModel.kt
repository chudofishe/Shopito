package com.chudofishe.grocerieslistapp.ui

import androidx.lifecycle.viewModelScope
import com.chudofishe.grocerieslistapp.data.SharedPrefAppStore
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    sharedPrefAppStore: SharedPrefAppStore
) : BaseViewModel() {

    private val _didWatchOnBoarding = MutableSharedFlow<Boolean>()
    val didWatchOnBoarding = _didWatchOnBoarding.asSharedFlow()

    init {
        viewModelScope.launch {
            _didWatchOnBoarding.emit(sharedPrefAppStore.getWatchedOnBoarding())
        }
    }
}