package com.chudofishe.grocerieslistapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.chudofishe.grocerieslistapp.R
import com.chudofishe.grocerieslistapp.ui.active_list_screen.ActiveListFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navController.graph = navGraph

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.didWatchOnBoarding.collect {
                    if (!it) {
                        navController.navigate(R.id.action_active_list_destination_to_onBoardingHostFragment3)
                    }
                }
            }
        }
    }

    fun clearShoppingListHistory(deleteFavoriteLists: Boolean) {
        viewModel.clearShoppingListHistory(deleteFavoriteLists)
    }

    fun clearFavoriteProducts() {
        viewModel.clearFavoriteProducts()
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (drawerLayout.isOpen) {
            drawerLayout.close()
            true
        } else {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isOpen) {
            drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(this.javaClass.name, "onStop, isFinishing: $isFinishing")
        val activeListFragment = navHostFragment.childFragmentManager.fragments[0]
        if (activeListFragment != null && activeListFragment is ActiveListFragment) {
            activeListFragment.saveCurrentState()
        }
    }

}