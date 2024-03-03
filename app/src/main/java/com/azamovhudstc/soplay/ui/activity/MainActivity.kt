package com.azamovhudstc.soplay.ui.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.ActivityMainBinding
import com.azamovhudstc.soplay.utils.hideWithoutAnimation
import com.azamovhudstc.soplay.utils.initActivity
import com.azamovhudstc.soplay.utils.showWithAnimation
import com.azamovhudstc.soplay.utils.snackString
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint

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
        initActivity(this)
        hasPermission()
        setupBottomNavigationView()
    }

    private fun hasPermission() {
        EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= 33) {
            EasyPermissions.hasPermissions(this, android.Manifest.permission.POST_NOTIFICATIONS)
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
            EasyPermissions.requestPermissions(
                this,
                "This Permission For Download Movies",
                PERMISSION_REQUEST_CODE,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun setupBottomNavigationView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)


        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailScreen -> {
                    binding.bottomNavigation.hideWithoutAnimation(binding.fragmentContainerView)
                }

                R.id.popularSeeAllScreen -> {
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