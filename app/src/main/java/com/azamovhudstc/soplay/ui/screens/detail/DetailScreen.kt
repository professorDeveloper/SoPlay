package com.azamovhudstc.soplay.ui.screens.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azamovhudstc.soplay.databinding.DetailScreenBinding
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.viewmodel.imp.DetailViewModelImpl

class DetailScreen : Fragment() {

    private val viewModel by viewModels<DetailViewModelImpl>()

    private var _binding: DetailScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.movieDetailData.observe(this) {
            when (it) {
                is Resource.Error -> {

                }
                Resource.Loading -> {

                }
                is Resource.Success -> {

                }
            }
        }
    }

}