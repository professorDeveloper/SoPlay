package com.azamovhudstc.soplay.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.databinding.SearchScreenBinding
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.utils.*
import com.azamovhudstc.soplay.viewmodel.imp.SearchViewModelImpl
import com.google.android.material.snackbar.Snackbar

class SearchScreen : Fragment() {
    private var _binding: SearchScreenBinding? = null
    private val binding get() = _binding!!
    private val model by viewModels<SearchViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeToolbarColorListener.invoke(true)
        model.searchData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        it.throwable.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).setAction("Reload") {
                        model.searchMovie(binding.mainSearch.query.toString())

                    }.show()
                    binding.searchRv.hide()
                    binding.progress.hide()
                }
                Resource.Loading -> {
                    binding.progress.show()
                    binding.searchRv.hide()
                }
                is Resource.Success -> {
                    binding.progress.hide()
                    binding.searchRv.show()
                    binding.apply {
                        val adapter = SearchAdapter(requireActivity(), it.data)
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
    }

}