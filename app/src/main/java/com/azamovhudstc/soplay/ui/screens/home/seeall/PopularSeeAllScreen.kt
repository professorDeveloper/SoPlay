package com.azamovhudstc.soplay.ui.screens.home.seeall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.PopularSeeAllScreenBinding
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.utils.animationTransaction
import com.azamovhudstc.soplay.utils.gone
import com.azamovhudstc.soplay.utils.visible
import com.azamovhudstc.soplay.viewmodel.imp.SearchViewModelImpl

class PopularSeeAllScreen : Fragment() {
    private var _binding: PopularSeeAllScreenBinding? = null
    private val binding get() = _binding!!
    private val model by viewModels<SearchViewModelImpl>()
    private var isLoading = true
    private lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.loadLastPagingData.observe(this) {
            when (it) {
                is Resource.Error -> {
                    if (isLoading) {
                        binding.nowPlayingMoviesLoading.gone()
                        binding.nowPlayingMoviesLoading.stopShimmer()
                        binding.recyclerViewNowPlayingMovies.visible()
                        isLoading = false
                    }
                }
                Resource.Loading -> {
                    if (isLoading) {
                        binding.nowPlayingMoviesLoading.visible()
                        binding.nowPlayingMoviesLoading.startShimmer()
                        binding.recyclerViewNowPlayingMovies.gone()
                    }

                }
                is Resource.Success -> {
                    if (isLoading) {
                        binding.nowPlayingMoviesLoading.gone()
                        binding.nowPlayingMoviesLoading.stopShimmer()
                        binding.recyclerViewNowPlayingMovies.visible()
                        isLoading = false
                    }

                    it.data?.let {
                        if (model.lastPageLast == 1) {
                            binding.recyclerViewNowPlayingMovies.adapter = adapter
                            model.pagingDataLast.addAll(it)
                            adapter.list.clear()
                            adapter.list.addAll(it)
                            adapter.notifyDataSetChanged()
                        } else {
                            val prev = model.pagingDataLast.size
                            model.pagingDataLast.addAll(it)
                            adapter.notifyItemRangeInserted(prev, it.size)
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
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = PopularSeeAllScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pageTitle.text = "So`ngi Qo`shilgan"
        adapter = SearchAdapter(requireActivity(), model.pagingDataLast)
        if (isLoading) {
            model.loadNextPageLast(1)
        } else {
            model.loadNextPageLast(1)
        }
        initPagination()


    }

    private fun initPagination() {
        binding.recyclerViewNowPlayingMovies.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1) && !model.isSearch) {
                    println(model.pagingDataLast.isNotEmpty())
                    if (model.pagingDataLast.isNotEmpty()) {
                        model.loadNextPageLast(model.lastPageLast + 1)
                    }
                }
                super.onScrolled(v, dx, dy)
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }


}