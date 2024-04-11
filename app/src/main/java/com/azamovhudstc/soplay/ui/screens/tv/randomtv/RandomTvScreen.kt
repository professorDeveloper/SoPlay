package com.azamovhudstc.soplay.ui.screens.tv.randomtv

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.RandomTvScreenBinding
import com.azamovhudstc.soplay.ui.screens.tv.adapter.TabAdapter
import com.azamovhudstc.soplay.utils.loadTvFilters
import com.azamovhudstc.soplay.utils.slideStart
import com.google.android.material.tabs.TabLayoutMediator


class RandomTvScreen : Fragment() {

    private var _binding :RandomTvScreenBinding?=null
    private val binding  get() = _binding!!
    private  lateinit var adapter:TabAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =RandomTvScreenBinding.inflate(layoutInflater,container,false)
        return  binding .root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbarBrowse.slideStart(700, 0)
            browseType.slideStart(700, 0)
            viewPager.slideStart(700, 0)
            adapter = TabAdapter(loadTvFilters(), requireActivity())
            binding.viewPager.adapter = adapter

//            binding.genreContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin += statusBarHeight;bottomMargin += navBarHeight }

            TabLayoutMediator(browseType, viewPager) { _, _ ->
            }.attach()
            setTab()

            for (i in 0 until binding.browseType.tabCount) {
                binding.browseType.getTabAt(i)?.let { TooltipCompat.setTooltipText(it.view, null) }
            }
//            binding.genreContainer.updatePaddingRelative(bottom = navBarHeight + 36f.px)


        }
    }


    private fun setTab() {
        binding.apply {
            val tabCount = browseType.tabCount
            for (i in 0 until tabCount) {
                val tab = browseType.getTabAt(i)
                tab!!.text = loadTvFilters()[i].first
            }

        }
    }

    override fun onResume() {
        super.onResume()
        binding.toolbarBrowse.slideStart(700, 0)
        binding.browseType.slideStart(700, 0)
        binding.viewPager.slideStart(700, 0)

    }


}