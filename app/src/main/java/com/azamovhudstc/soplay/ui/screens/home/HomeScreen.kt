package com.azamovhudstc.soplay.ui.screens.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azamovhudstc.soplay.databinding.HomeScreenBinding
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.viewmodel.imp.SearchViewModelImpl


class HomeScreen : Fragment() {

    private val model by viewModels<SearchViewModelImpl>()

    private var _binding: HomeScreenBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { SearchAdapter(requireActivity()) }
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
        model.searchData.observe(this) {
            when (it) {
                is Resource.Error -> {
                    Log.e("TAG", "onCreate:${it.throwable.message.toString()} ")
                }
                Resource.Loading -> {
                    Toast.makeText(requireActivity(), "Parsing Data..", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    binding.apply {
                        searchRv.adapter = adapter
                        adapter.submitList(
                            it.data
                            )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mainSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    model.searchMovie(query.toString())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }
    }

}