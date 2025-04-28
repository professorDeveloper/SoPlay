package com.azamovme.soplay.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.azamovme.soplay.R
import com.azamovme.soplay.app.getDeviceData
import com.azamovme.soplay.databinding.ActivityMainBinding
import com.azamovme.soplay.theme.ThemeManager
import com.azamovme.soplay.utils.*
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding
    private var isTV: Boolean = false
    private val allowedLiveData: MutableLiveData<Boolean> = MutableLiveData()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ThemeManager(this).applyTheme()
        initActivity(this)
        ensurePhonePermission(this@MainActivity)
        hasPermission()
        ThemeManager(this@MainActivity).applyTheme()
        allowedLiveData.observe(this@MainActivity) { allowed ->
            if (!allowed) {
                makeAlert(
                    this@MainActivity, this@MainActivity
                )
            }
        }
        setupBottomNavigationView()
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
    }

    private val REQ_READ_PHONE = 1001

    @SuppressLint("InlinedApi")
    fun ensurePhonePermission(activity: Activity) {
        val perm = Manifest.permission.READ_PHONE_NUMBERS
        if (ContextCompat.checkSelfPermission(
                activity,
                perm
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(perm), REQ_READ_PHONE)
        }
    }

    @SuppressLint("NewApi")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_READ_PHONE &&
            grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            val phoneData = getDeviceData(this@MainActivity)
            savePhoneDataIfNotExists(phoneData!!)
        } else {
            snackString("Permission Denied!")
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
        lifecycleScope.launch {
            allowedLiveData.postValue(
                AccessManager.initAndCheckDostup(this@MainActivity)
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        snackString("Permission Denied!")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        snackString("Permission Granted!")
    }


}