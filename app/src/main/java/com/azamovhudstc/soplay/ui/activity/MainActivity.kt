package com.azamovhudstc.soplay.ui.activity

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.ActivityMainBinding
import com.azamovhudstc.soplay.theme.ThemeManager
import com.azamovhudstc.soplay.utils.*
import com.azamovhudstc.soplay.utils.AppUpdater.check
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var settingsPreferenceManager: SharedPreferences
    private var isTV: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ThemeManager(this).applyTheme()
        initActivity(this)
        hasPermission()
        setupBottomNavigationView()
        ThemeManager(this).applyTheme()
        checkUpdate()
    }


    private fun checkUpdate() {
        lifecycleScope.launch(Dispatchers.IO) {
            check(this@MainActivity)
        }

    }

    private fun hasPermission() {
        EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= 33) {
//            EasyPermissions.hasPermissions(this, android.Manifest.permission.POST_NOTIFICATIONS)
        }
        requestFilePermission()
    }

    private fun requestFilePermission() {
        EasyPermissions.requestPermissions(
            this,
            "This Permission For Download Movies",
            PERMISSION_REQUEST_CODE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= 33) {
//            EasyPermissions.requestPermissions(
//                this,
//                "This Permission For Download Movies",
//                PERMISSION_REQUEST_CODE,
//                android.Manifest.permission.POST_NOTIFICATIONS
//            )
        }
    }

    private fun setupBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)

        val context = this
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        val colorPrimary = typedValue.data
        val colorControlNormal = ContextCompat.getColor(context, R.color.uncheckedColor)
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            colorPrimary,
            colorControlNormal
        )
        val colorStateList = ColorStateList(states, colors)
        binding.bottomNavigation.itemIconTintList = colorStateList
        binding.bottomNavigation.itemTextColor = colorStateList
        // Code




        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailScreen -> {
                    binding.bottomNavigation.hideWithoutAnimation(binding.fragmentContainerView)
                }

                R.id.popularSeeAllScreen -> {
                    binding.bottomNavigation.hideWithoutAnimation(binding.fragmentContainerView)
                }

                R.id.navigation_settings -> {
                    binding.bottomNavigation.hideWithoutAnimation(binding.fragmentContainerView)
                }

                R.id.searchScreen -> {
                    binding.bottomNavigation.hideWithoutAnimation(binding.fragmentContainerView)
                }

                else -> {
                    binding.bottomNavigation.showWithAnimation(binding.fragmentContainerView)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        snackString("Permission Denied!")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        snackString("Permission Granted!")
    }


}