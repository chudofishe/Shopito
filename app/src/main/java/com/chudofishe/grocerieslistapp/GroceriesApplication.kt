package com.chudofishe.grocerieslistapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GroceriesApplication : Application() {

    companion object {
        lateinit var applicationContext: Context
            private set
    }
}