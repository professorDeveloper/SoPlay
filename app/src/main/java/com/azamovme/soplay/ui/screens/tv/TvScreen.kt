package com.azamovme.soplay.ui.screens.tv

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azamovme.soplay.R
import com.azamovme.soplay.databinding.TvScreenBinding
import com.azamovme.soplay.ui.screens.tv.adapter.TvAdapter
import com.azamovme.soplay.ui.screens.tv.player.PlayerActivityTv
import com.azamovme.soplay.utils.Resource
import com.azamovme.soplay.utils.hide
import com.azamovme.soplay.utils.show
import com.azamovme.soplay.utils.snackString
import com.azamovme.soplay.viewmodel.imp.TvViewModelImpl
import kotlinx.coroutines.launch

class TvScreen : Fragment() {
    private var _binding: TvScreenBinding? = null
    private val binding get() = _binding!!
    private val tvAdapter by lazy { TvAdapter() }
    private val model by viewModels<TvViewModelImpl>()
    private var screenWidth: Float = 0f
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.loadPagingData.observe(this) {
            when (it) {
                is Resource.Error -> {
                    snackString(it.throwable.message)
                    binding.progressBar.hide()
                }
                Resource.Loading -> {
                    if (isLoading){
                        binding.progressBar.show()
                        binding.tvRv.hide()
                    }
                }
                is Resource.Success -> {
                    if (isLoading){
                        binding.progressBar.hide()
                        binding.tvRv.show()
                        isLoading=false
                    }
                    binding.tvRv.layoutManager =
                        GridLayoutManager(requireContext(), (screenWidth / 124f).toInt())
                    binding.tvRv.adapter = tvAdapter
                    if (model.lastPage == 1) {
                        binding.tvRv.adapter = tvAdapter
                        model.pagingData.addAll(it.data)
                        tvAdapter.submitList(it.data)
                    } else {
                        val prev = model.pagingData.size
                        model.pagingData.addAll(it.data)
                        tvAdapter.notifyItemRangeInserted(prev, it.data.size)
                    }
                    tvAdapter.setItemClickListener { movie ->
                        lifecycleScope.launch {
                            binding.progressBar.hide()
                            binding.tvRv.show()
                            val activity = PlayerActivityTv.newIntent(requireActivity(), movie)
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
        if (isLoading) {
            model.loadTvNextPage(1)
        } else {
            model.loadTvNextPage(1)
        }
//        initPagination()
    }

    private fun initPagination() {
        binding.tvRv.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1) && !model.isSearch) {
                    if (model.pagingData.isNotEmpty() && model.lastPage != 4) {
                        model.loadTvNextPage(model.lastPage + 1)
                    }
                }
                super.onScrolled(v, dx, dy)
            }
        })
    }

}