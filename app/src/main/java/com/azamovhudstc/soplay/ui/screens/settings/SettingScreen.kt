package com.azamovhudstc.soplay.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.azamovhudstc.soplay.BuildConfig
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.SettingScreenBinding
import com.azamovhudstc.soplay.ui.screens.bottomsheet.DevelopersDialogFragment
import com.azamovhudstc.soplay.utils.openLinkInBrowser
import com.azamovhudstc.soplay.utils.pop
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
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
        }

    }

}