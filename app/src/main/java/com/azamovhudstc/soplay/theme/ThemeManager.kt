package com.azamovhudstc.soplay.theme


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.Window
import android.view.WindowManager
import com.azamovhudstc.soplay.R
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions


class ThemeManager(private val context: Activity) {
    fun applyTheme(fromImage: Bitmap? = null) {
        val useOLED = context.getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            .getBoolean("use_oled", false) && isDarkThemeActive(context)
        val useCustomTheme = context.getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            .getBoolean("use_custom_theme", false)
        val customTheme = context.getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            .getInt("custom_theme_int", 16712221)
        val useSource = context.getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            .getBoolean("use_source_theme", false)
        val useMaterial = context.getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            .getBoolean("use_material_you", false)
        if (useSource) {
            val returnedEarly = applyDynamicColors(
                useMaterial,
                context,
                useOLED,
                fromImage,
                useCustom = if (useCustomTheme) customTheme else null
            )
            if (!returnedEarly) return
        } else if (useCustomTheme) {
            val returnedEarly =
                applyDynamicColors(useMaterial, context, useOLED, useCustom = customTheme)
            if (!returnedEarly) return
        } else {
            val returnedEarly = applyDynamicColors(useMaterial, context, useOLED, useCustom = null)
            if (!returnedEarly) return
        }
        val theme = context.getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            .getString("theme", "YELLOW")!!

        val themeToApply = when (theme) {
            "YELLOW" -> if (useOLED) R.style.Theme_SoPlay_Yellow else R.style.Theme_SoPlay_Yellow
            "BLUE" -> if (useOLED) R.style.Theme_SoPlay_BlueOLED else R.style.Theme_SoPlay_Blue
            "GREEN" -> if (useOLED) R.style.Theme_SoPlay_GreenOLED else R.style.Theme_SoPlay_Green
            "PURPLE" -> if (useOLED) R.style.Theme_SoPlay_PurpleOLED else R.style.Theme_SoPlay_Purple
            "PINK" -> if (useOLED) R.style.Theme_SoPlay_PinkOLED else R.style.Theme_SoPlay_Pink
            "SAIKOU" -> if (useOLED) R.style.Theme_SoPlay_SaikouOLED else R.style.Theme_SoPlay_Saikou
            "RED" -> if (useOLED) R.style.Theme_SoPlay_RedOLED else R.style.Theme_SoPlay_Red
            "LAVENDER" -> if (useOLED) R.style.Theme_SoPlay_LavenderOLED else R.style.Theme_SoPlay_Lavender
            "OCEAN" -> if (useOLED) R.style.Theme_SoPlay_OceanOLED else R.style.Theme_SoPlay_Ocean
            "MONOCHROME (BETA)" -> if (useOLED) R.style.Theme_SoPlay_MonochromeOLED else R.style.Theme_SoPlay_Monochrome
            else -> if (useOLED) R.style.Theme_SoPlay_PurpleOLED else R.style.Theme_SoPlay_Purple
        }

        val window = context.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0x00000000
        context.setTheme(themeToApply)
    }

    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win: Window = activity.window
        val winParams: WindowManager.LayoutParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.setAttributes(winParams)
    }

    @SuppressLint("RestrictedApi")
    private fun applyDynamicColors(
        useMaterialYou: Boolean,
        context: Context,
        useOLED: Boolean,
        bitmap: Bitmap? = null,
        useCustom: Int? = null
    ): Boolean {
        val builder = DynamicColorsOptions.Builder()
        var needMaterial = true

        // Set content-based source if a bitmap is provided
        if (bitmap != null) {
            builder.setContentBasedSource(bitmap)
            needMaterial = false
        } else if (useCustom != null) {
            val contentSourceBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            contentSourceBitmap.eraseColor(useCustom)
            builder.setContentBasedSource(contentSourceBitmap!!)
            needMaterial = false
        }

        if (useOLED) {
            builder.setThemeOverlay(R.style.AppTheme_Amoled)
        }
        if (needMaterial && !useMaterialYou) return true

        // Build the options
        val options = builder.build()

        // Apply the dynamic colors to the activity
        val activity = context as Activity
        DynamicColors.applyToActivityIfAvailable(activity, options)

        if (useOLED) {
            val options2 = DynamicColorsOptions.Builder()
                .setThemeOverlay(R.style.AppTheme_Amoled)
                .build()
            DynamicColors.applyToActivityIfAvailable(activity, options2)
        }

        return false
    }

    private fun isDarkThemeActive(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }


    companion object {
        enum class Theme(val theme: String) {
            YELLOW("YELLOW"),
            BLUE("BLUE"),
            GREEN("GREEN"),
            PURPLE("PURPLE"),
            PINK("PINK"),
            RED("RED"),
            LAVENDER("LAVENDER"),
            OCEAN("OCEAN"),
            MONOCHROME("MONOCHROME (BETA)");

            companion object {
                fun fromString(value: String): Theme {
                    return values().find { it.theme == value } ?: PURPLE
                }
            }
        }
    }
}