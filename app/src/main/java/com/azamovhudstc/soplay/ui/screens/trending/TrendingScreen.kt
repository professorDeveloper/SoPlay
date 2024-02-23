package com.azamovhudstc.soplay.ui.screens.trending

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.TrendingScreenBinding
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.utils.Refresh
import com.azamovhudstc.soplay.utils.animationTransaction
import com.azamovhudstc.soplay.utils.loaded
import com.azamovhudstc.soplay.viewmodel.imp.TrendingViewModelImpl
import kotlinx.coroutines.launch

class TrendingScreen : Fragment() {

    private var _binding: TrendingScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TrendingViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.trendingMovieList.observe(this) {

            binding.progressbarInMain.visibility = View.GONE
            if (it.isNotEmpty()) {
                binding.errorCard.visibility = View.GONE
            } else {
                binding.errorCard.visibility = View.VISIBLE
            }
            val adapter = SearchAdapter(requireActivity(), it)
            binding.favoriteRv.adapter = adapter
            if (binding.swipeContainer.isRefreshing) {
                binding.swipeContainer.isRefreshing = false
            }

            adapter.setItemClickListener {
                val bundle = Bundle()
                val data = it
                bundle.putSerializable("data", data)
                findNavController().navigate(
                    R.id.detailScreen,
                    bundle,
                    animationTransaction().build()
                )
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = TrendingScreenBinding.inflate(inflater, container, false)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.favoriteRv.layoutManager = GridLayoutManager(activity as Context, 3)
        } else {
            binding.favoriteRv.layoutManager = GridLayoutManager(activity as Context, 2)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            binding.swipeContainer.setOnRefreshListener {
                lifecycleScope.launch {
                    Refresh.activity[1]!!.postValue(true)
                }
            }

            val live = Refresh.activity.getOrPut(
                1
            ) { MutableLiveData(false) }
            live.observe(viewLifecycleOwner) {
                if (it) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        loaded = true
                        viewModel.loadTrendMovies()
                        live.postValue(false)
                    }
                }
            }
        }
        binding.progressbarInMain.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (!loaded) Refresh.activity[1]!!.postValue(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loaded = false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (activity != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.favoriteRv.layoutManager = GridLayoutManager(activity as Context, 2)
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.favoriteRv.layoutManager = GridLayoutManager(activity as Context, 3)
            }
        }
    }
}