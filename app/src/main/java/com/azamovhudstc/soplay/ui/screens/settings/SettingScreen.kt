package com.azamovhudstc.soplay.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.azamovhudstc.soplay.BuildConfig
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.SettingScreenBinding
import com.azamovhudstc.soplay.theme.ThemeManager
import com.azamovhudstc.soplay.ui.activity.MainActivity
import com.azamovhudstc.soplay.ui.screens.bottomsheet.DevelopersDialogFragment
import com.azamovhudstc.soplay.utils.openLinkInBrowser
import com.azamovhudstc.soplay.utils.pop
import com.azamovhudstc.soplay.utils.restartApp
import com.google.android.material.snackbar.Snackbar
import eltos.simpledialogfragment.color.SimpleColorDialog
import kotlinx.coroutines.launch


class SettingsScreen : Fragment() {
    private var _binding: SettingScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingScreenBinding.inflate(inflater, container, false)
        return binding.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val db = requireActivity().getSharedPreferences("Soplay", Context.MODE_PRIVATE)
            settingsDev.setOnClickListener {
                DevelopersDialogFragment().show(parentFragmentManager, "dialog")
            }
            binding.settingBuyMeCoffee.setOnClickListener {
                lifecycleScope.launch {
                    it.pop()
                }
                openLinkInBrowser("https://www.buymeacoffee.com/chihaku", requireActivity())
            }
            binding.settingUPI.setOnClickListener {
                lifecycleScope.launch {
                    it.pop()
                }
                val upi = "https://github.com/sponsors/professorDeveloper"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(upi)
                }
                startActivity(Intent.createChooser(intent, "Donate with..."))
            }

            binding.loginTelegram.setOnClickListener {
                openLinkInBrowser(getString(R.string.telegram), requireActivity())
            }
            binding.loginGithub.setOnClickListener {
                openLinkInBrowser(getString(R.string.github), requireActivity())
            }
            binding.settingsVersion.text =
                getString(R.string.version_current, BuildConfig.VERSION_NAME)
            settingsBack.setOnClickListener {
                lifecycleScope.launch {
                    it.pop()
                    findNavController().popBackStack()
                }
            }


            settingsUseMaterialYou.isChecked = db.getBoolean("use_material_you", false)
            settingsUseMaterialYou.setOnCheckedChangeListener { _, isChecked ->
                db.edit().putBoolean("use_material_you", isChecked).apply()
                requireActivity().restartApp(binding.root)
            }

//            settingsUseCustomTheme.isChecked =
//                db.getBoolean("use_custom_theme",false)
//            settingsUseCustomTheme.setOnCheckedChangeListener { _, isChecked ->
//                db.edit().putBoolean("use_custom_theme", isChecked).apply()
//                if (isChecked) {
//                    settingsUseMaterialYou.isChecked = false
//                }
//
//               requireContext().restartApp(binding.root)
//            }
//
//            settingsUseSourceTheme.isChecked = db.getBoolean("use_source_theme",false)
//            settingsUseSourceTheme.setOnCheckedChangeListener { _, isChecked ->
//                db.edit().putBoolean("use_source_theme", isChecked).commit()
//                requireActivity().restartApp(binding.root)
//            }



            val themeString: String = db.getString("theme","YELLOW").toString()
            val themeText = themeString.substring(0, 1) + themeString.substring(1).lowercase()
            themeSwitcher.setText(themeText)

            themeSwitcher.setAdapter(
                ArrayAdapter(
                    requireActivity(),
                    R.layout.item_dropdown,
                    ThemeManager.Companion.Theme.values()
                        .map { it.theme.substring(0, 1) + it.theme.substring(1).lowercase() })
            )

            themeSwitcher.setOnItemClickListener { _, _, i, _ ->
                db.edit().putString("theme", ThemeManager.Companion.Theme.values()[i].theme).apply()
                themeSwitcher.clearFocus()
                requireActivity().restartApp(binding.root)
            }

//            customTheme.setOnClickListener {
//                val originalColor: Int = db.getInt("custom_theme_int",16712221)
//
//                class CustomColorDialog : SimpleColorDialog() { //idk where to put it
//                    override fun onPositiveButtonClick() {
//                       requireActivity(). restartApp(binding.root)
//                        super.onPositiveButtonClick()
//                    }
//                }
//
//                val tag = "colorPicker"
//                CustomColorDialog().title(R.string.custom_theme)
//                    .colorPreset(originalColor)
//                    .colors(requireActivity(), SimpleColorDialog.MATERIAL_COLOR_PALLET)
//                    .allowCustom(true)
//                    .showOutline(0x46000000)
//                    .gridNumColumn(5)
//                    .choiceMode(SimpleColorDialog.SINGLE_CHOICE)
//                    .neg()
//                    .show(requireActivity(), tag)
//            }



        }

    }

    fun restartApp() {
        Snackbar.make(
            binding.root,
            "Restart App ?", Snackbar.LENGTH_SHORT
        ).apply {
            val mainIntent = Intent(requireActivity(),MainActivity::class.java)
            setAction("Do it!") {
                requireActivity().startActivity(mainIntent)
                Runtime.getRuntime().exit(0)
                requireActivity().finishAndRemoveTask()
            }
            show()
        }
    }
}