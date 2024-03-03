package com.azamovhudstc.soplay.ui.screens.tv

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.TvScreenBinding
import com.azamovhudstc.soplay.ui.screens.tv.adapter.TvAdapter
import com.azamovhudstc.soplay.ui.screens.tv.player.PlayerActivityTv
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.utils.hide
import com.azamovhudstc.soplay.utils.show
import com.azamovhudstc.soplay.utils.snackString
import com.azamovhudstc.soplay.viewmodel.imp.TvViewModelImpl
import kotlinx.coroutines.launch

class TvScreen : Fragment() {
    private var _binding: TvScreenBinding? = null
    private val binding get() = _binding!!
    private val tvAdapter by lazy { TvAdapter() }
    private val model by viewModels<TvViewModelImpl>()
    private var screenWidth: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.tvList.observe(this) {
            when (it) {
                is Resource.Error -> {
                    snackString(it.throwable.message)
                    binding.progressBar.hide()
                }
                Resource.Loading -> {
                    binding.progressBar.show()
                    binding.tvRv.hide()

                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    binding.tvRv.show()
                    binding.tvRv.layoutManager =
                        GridLayoutManager(requireContext(), (screenWidth / 124f).toInt())
                    binding.tvRv.adapter = tvAdapter
                    tvAdapter.submitList(it.data)
                    tvAdapter.setItemClickListener { movie ->
                        lifecycleScope.launch {
                            binding.progressBar.hide()
                            binding.tvRv.show()
                            val activity =PlayerActivityTv.newIntent(requireActivity(),movie)
                            startActivity(activity)
                        }
                    }


                }
            }
        }

    }

    private fun startExternalPlayer(
        liveTvLink: String,
        animeName: String,
    ) {
        val title = "$animeName"

        val customIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(liveTvLink), "video/*")
            putExtra("title", title)
        }
        startMX(customIntent)

    }

    private fun startMX(
        customIntent: Intent
    ) {
        try {
            customIntent.apply {
                setPackage("com.mxtech.videoplayer.pro")
                startActivity(this)
            }
        } catch (e: ActivityNotFoundException) {
            Log.i(
                R.string.app_name.toString(),
                "MX Player pro isn't installed, falling back to MX player Ads"
            )
            try {
                Intent(Intent.ACTION_VIEW).apply {
                    customIntent.apply {
                        setPackage("com.mxtech.videoplayer.ad")
                        startActivity(this)
                    }
                }
            } catch (e: ActivityNotFoundException) {
                Log.i(
                    R.string.app_name.toString(),
                    "No version of MX Player is installed, falling back to other external player"
                )
                startActivity(Intent.createChooser(customIntent, "Play using"))
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TvScreenBinding.inflate(inflater, container, false)
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenWidth = resources.displayMetrics.run { widthPixels / density }
        model.loadTv()
    }

}