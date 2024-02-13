package com.azamovhudstc.soplay.ui.screens.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.HomeScreenBinding
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.utils.*
import com.azamovhudstc.soplay.viewmodel.imp.SearchViewModelImpl


class HomeScreen : Fragment() {

    private val model by viewModels<SearchViewModelImpl>()
    private lateinit var adapter: SearchAdapter

    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = HomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.frameLayout.slideStart(900, 1)
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = resources.getColor(R.color.md_theme_light_9_onBackground)
        binding.toolbar.slideUp(900, 1)
        adapter = SearchAdapter(requireActivity(), model.pagingData)
        model.loadPagingData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Log.e("TAG", "onCreate:${it.throwable.message.toString()} ")
                }
                Resource.Loading -> {
                }
                is Resource.Success -> {
                    it.data?.let {
                        if (model.lastPage == 1) {
                            binding.searchRv.adapter = adapter
                            model.pagingData.addAll(it)
                        } else {
                            val prev = model.pagingData.size
                            model.pagingData.addAll(it)
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

        model.searchData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    binding.searchRv.hide()
                    binding.progress.hide()
                    Log.e("TAG", "onCreate:${it.throwable.message.toString()} ")
                }
                Resource.Loading -> {
                    binding.progress.show()
                    binding.searchRv.hide()
                }
                is Resource.Success -> {
                    binding.progress.hide()
                    binding.searchRv.show()
                    binding.apply {
                        adapter = SearchAdapter(requireActivity(), it.data)
                        searchRv.adapter = adapter
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

                        searchRv.slideUp(700, 1)
                    }
                }
            }
        }
        binding.apply {
            mainSearch.setOnCloseListener {
                model.isSearch = false
                model.loadNextPage(model.lastPage)
                adapter = SearchAdapter(requireActivity(), model.pagingData)
                true
            }
            mainSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    dismissKeyboard(binding.root)
                    if (query.toString().trim().isNotEmpty()) {
                        model.isSearch = true
                        model.searchMovie(query.toString())

                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }


            })
        }
        initPagination()

        model.loadNextPage(1)


    }

    private fun initPagination() {
        binding.searchRv.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1) && !model.isSearch) {
                    println(model.pagingData.isNotEmpty())
                    if (model.pagingData.isNotEmpty()) {
                        model.loadNextPage(model.lastPage + 1)
                    }
                }
                super.onScrolled(v, dx, dy)
            }
        })
    }


}