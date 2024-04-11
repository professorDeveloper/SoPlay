package com.azamovhudstc.soplay.ui.screens.tv.randomtv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.soplay.data.Movie
import com.azamovhudstc.soplay.databinding.FragmentRandomTvItemScreenBinding
import com.azamovhudstc.soplay.tv.stv.response.Post
import com.azamovhudstc.soplay.ui.screens.tv.adapter.RandomTvAdapter
import com.azamovhudstc.soplay.ui.screens.tv.player.PlayerActivityTv
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.utils.hide
import com.azamovhudstc.soplay.utils.show
import com.azamovhudstc.soplay.utils.snackString
import com.azamovhudstc.soplay.viewmodel.imp.TvViewModelImpl
import kotlinx.coroutines.launch
import kotlin.random.Random

class RandomTvItemScreen : Fragment() {

    private var _binding: FragmentRandomTvItemScreenBinding? = null
    private val binding get() = _binding!!
    private var screenWidth: Float = 0f
    private var isLoading = true
    private val adapter by lazy { RandomTvAdapter() }
    private val viewModel by activityViewModels<TvViewModelImpl>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRandomTvItemScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryId = arguments?.getInt("categoryId") ?: 20
        screenWidth = resources.displayMetrics.run { widthPixels / density }

            viewModel.loadRandomTvById(categoryId)


        viewModel.tvRandomList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    snackString(it.throwable.message)
                    binding.progressBar.hide()
                }
                Resource.Loading -> {
                    if (isLoading) {
                        binding.progressBar.show()
                        binding.tvRv.hide()
                    }
                }
                is Resource.Success -> {
                    if (isLoading) {
                        binding.progressBar.hide()
                        binding.tvRv.show()
                        isLoading = false
                    }
                    binding.tvRv.layoutManager =
                        GridLayoutManager(requireContext(), (screenWidth / 124f).toInt())
                    binding.tvRv.adapter = adapter

                    adapter.submitList(it.data.posts as ArrayList<Post>)
                    adapter.setItemClickListener { movie ->
                        lifecycleScope.launch {
                            binding.progressBar.hide()
                            binding.tvRv.show()
                            val activity = PlayerActivityTv.newIntent(
                                requireActivity(),
                                Movie(
                                    movie.channel_url,
                                    movie.channel_name,
                                    "http://tv.musical.uz//upload/${movie.channel_image}",
                                    Random.nextInt(1, 5)
                                )
                            )
                            startActivity(activity)
                        }
                    }


                }
            }
            binding.apply {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val categoryId = arguments?.getInt("categoryId") ?: 20
        viewModel.loadRandomTvById(categoryId)
    }
}