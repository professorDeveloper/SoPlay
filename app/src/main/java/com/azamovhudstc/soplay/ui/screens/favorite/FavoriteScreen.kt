package com.azamovhudstc.soplay.ui.screens.favorite

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.FavoriteScreenBinding
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.utils.animationTransaction
import com.azamovhudstc.soplay.viewmodel.imp.FavoriteViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteScreen : Fragment() {
    private var _binding: FavoriteScreenBinding? = null
    private val binding get() = _binding!!
    private val model by viewModels<FavoriteViewModelImpl>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FavoriteScreenBinding.inflate(layoutInflater)
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
            binding.swipeContainer.setOnRefreshListener { model.loadFav() }
        }
        binding.progressbarInMain.visibility = View.VISIBLE
        model.favoriteAnimeList.observe(viewLifecycleOwner) {
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

    override fun onResume() {
        super.onResume()
        model.loadFav()

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