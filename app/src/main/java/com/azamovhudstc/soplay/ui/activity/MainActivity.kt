package com.azamovhudstc.soplay.ui.activity

import android.app.PictureInPictureParams
import android.app.UiModeManager
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.Menu
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.ActivityMainBinding
import com.azamovhudstc.soplay.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var settingsPreferenceManager: SharedPreferences
    private var isPipEnabled: Boolean = true
    private var isTV: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        settingsPreferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        // Check TV
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        isTV = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        isPipEnabled = settingsPreferenceManager.getBoolean("pip", true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity(this)

        setSupportActionBar(binding.toolbar)
        val bottomNavView: BottomNavigationView = binding.navView
        val railView: NavigationRailView = binding.navRail

        val navController = findNavController(R.id.nav_host_fragment_activity_main_bottom_nav)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_favorite, R.id.navigation_latest, R.id.navigation_trending
            )
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNavTransition = Slide(Gravity.BOTTOM).apply {
                duration = 200
                addTarget(bottomNavView)
            }
            TransitionManager.beginDelayedTransition(
                bottomNavView.parent as ViewGroup,
                bottomNavTransition
            )
            val railViewNavTransition = Slide(Gravity.START).apply {
                duration = 200
                addTarget(railView)
            }
            TransitionManager.beginDelayedTransition(
                railView.parent as ViewGroup,
                railViewNavTransition
            )
            when (destination.id) {
                R.id.detailScreen -> {
                    bottomNavView.isVisible = false
                    railView.isVisible = false
                    binding.toolbar.isVisible = false
                }
                R.id.navigation_search, R.id.navigation_settings -> {
                    bottomNavView.isVisible = false
                    railView.isVisible = isLandscape
                }
                else -> {
                    railView.isVisible = isLandscape
                    bottomNavView.isVisible = !isLandscape
                }
            }
            if (destination.id == R.id.detailScreen) {
                binding.toolbar.hide()
                println("tuhdiiii !!")
            } else {
                binding.toolbar.show()
            }
            binding.toolbar.isVisible = !isLandscape
            isPipEnabled = destination.id == R.id.detailScreen
            println("Destination is player = ${destination.id == R.id.detailScreen}")
            preparePip()
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
        railView.setupWithNavController(navController)

        binding.toolbar.isVisible =
            !isLandscape && navController.currentDestination?.id != R.id.detailScreen
        disableToolbar {
            if (
                it
            ) {
                binding.toolbar.hide()
            }
        }
        changeToolbarColor {
            binding.toolbar.setNavigationIconTint(this.getColor(R.color.white))

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val search = menu.findItem(R.id.search)
        val settings = menu.findItem(R.id.setting)
        val navController = findNavController(R.id.nav_host_fragment_activity_main_bottom_nav)

        search.setOnMenuItemClickListener {
            navController.navigate(R.id.navigation_search)
            return@setOnMenuItemClickListener true
        }
        settings.setOnMenuItemClickListener {
//            navController.navigate(R.id.navigation_settings)
            return@setOnMenuItemClickListener true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val notThis =
                destination.id == R.id.detailScreen || destination.id == R.id.navigation_search
                        || destination.id == R.id.navigation_settings
            search.isVisible = !notThis
            settings.isVisible = !notThis
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main_bottom_nav)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun preparePip() {
        if (isTV || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        if (isPipEnabled) {
            println("PIP enabled")
            setPictureInPictureParams(
                PictureInPictureParams.Builder()
                    .setAutoEnterEnabled(true)
                    .build()
            )

        } else {
            println("PIP disabled")
            setPictureInPictureParams(
                PictureInPictureParams.Builder()
                    .setAutoEnterEnabled(false)
                    .build()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (isPipEnabled && !isTV && Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }
}