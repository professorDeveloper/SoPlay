package com.azamovme.soplay.ui.screens.tv.randomtv

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.azamovme.soplay.data.Movie
import com.azamovme.soplay.databinding.RandomTvScreenBinding
import com.azamovme.soplay.tv.stv.parser.OntvParser
import com.azamovme.soplay.utils.slideStart
import com.azamovme.soplay.ui.screens.tv.adapter.RandomTvAdapter
import com.azamovme.soplay.ui.screens.tv.player.PlayerActivityTv
import kotlinx.coroutines.launch


class RandomTvScreen : Fragment() {

    private var _binding: RandomTvScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RandomTvAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RandomTvScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ontvParser = OntvParser()
        lifecycleScope.launch {
            ontvParser.getChannels().let {
                adapter = RandomTvAdapter().apply {
                    submitList(
                        it
                    )
                    setItemClickListener {
                        val intent = PlayerActivityTv.newIntent(
                            requireContext(),
                            Movie(
                                it.url_720!!, it.name ?: "Unknown", it.file?.url ?: "", 5
                            )
                        )
                        requireActivity().startActivity(intent)
                    }
                }
                binding.apply {
                    channelsRv.adapter = adapter
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        binding.toolbarBrowse.slideStart(700, 0)
        binding.channelsRv.slideStart(700, 0)

    }


}