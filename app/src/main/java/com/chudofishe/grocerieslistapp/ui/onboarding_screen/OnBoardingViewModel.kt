package com.chudofishe.grocerieslistapp.ui.onboarding_screen

import com.chudofishe.grocerieslistapp.data.SharedPrefAppStore
import com.chudofishe.grocerieslistapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val sharedPrefAppStore: SharedPrefAppStore
): BaseViewModel() {

    fun saveWatchedOnBoarding() {
        sharedPrefAppStore.saveWatchedOnBoarding()
    }
}