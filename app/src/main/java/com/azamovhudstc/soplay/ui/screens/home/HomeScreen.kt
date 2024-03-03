package com.azamovhudstc.soplay.ui.screens.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.HomeScreenBinding
import com.azamovhudstc.soplay.ui.adapter.MovieAdapter
import com.azamovhudstc.soplay.ui.adapter.SearchAdapter
import com.azamovhudstc.soplay.ui.adapter.ViewPagerAdapter
import com.azamovhudstc.soplay.utils.*
import com.azamovhudstc.soplay.viewmodel.imp.HomeScreenViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : Fragment() {

    private val model by viewModels<HomeScreenViewModelImpl>()
    private lateinit var adapter: SearchAdapter
    private var timer: MyCountDownTimer? = null
    private var isLoading = true
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

        model.loadLastNews.observe(this) {
            binding.nowPlayingMoviesLoading.gone()
            binding.nowPlayingMoviesLoading.stopShimmer()
            binding.recyclerViewNowPlayingMovies.visible()
            binding.apply {
                nowPlayingMoviesLoading.gone()
                nowPlayingMoviesLoading.stopShimmer()
                val playMoviesAdapter = MovieAdapter(requireActivity())
                recyclerViewNowPlayingMovies.adapter = playMoviesAdapter
                playMoviesAdapter.submitList(it)
                playMoviesAdapter.setItemClickListener {
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

        model.loadNeedWatch.observe(this) {

            binding.apply {
                nowPlayingSeriesLoading.gone()
                nowPlayingSeriesLoading.stopShimmer()
                recyclerViewNowPlayingSeries.visible()
                val playMoviesAdapter = MovieAdapter(requireActivity())
                recyclerViewNowPlayingSeries.adapter = playMoviesAdapter
                playMoviesAdapter.submitList(it)
                playMoviesAdapter.setItemClickListener {
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

        model.loadPopularMovies.observe(this) {
            when (it) {
                is Resource.Error -> {
                    snackString(
                        "Error"
                    )

                }
                Resource.Loading -> {
                    binding.apply {
                        popularMoviesLoading.visible()
                        popularMoviesLoading.startShimmer()

                    }
                }
                is Resource.Success -> {
                    it

                    binding.apply {
                        model.loadCheckList.observe(viewLifecycleOwner) { checkList ->
                            popularMoviesLoading.gone()
                            popularMoviesLoading.stopShimmer()
                            val pagerAdapter =
                                ViewPagerAdapter(
                                    it.data as ArrayList<MovieInfo>,
                                    checkList,
                                    ::onClickMovieItem,
                                    ::onClickMovieItemPlay,
                                    ::onClickMovieItemAddList
                                )
                            viewpagerPopularMovies.apply {
                                setScrollDurationFactor(4)
                                setPageTransformer(
                                    true,
                                    parallaxPageTransformer(
                                        R.id.movieActions
                                    )
                                )
                                adapter = pagerAdapter
                            }
                            pageSwitcher(it.data)
                        }
                    }

                }
            }
        }

    }


    private fun pageSwitcher(list: MutableList<MovieInfo>) {
        with(binding) {
            timer = MyCountDownTimer(5000, 5000) {
                try {
                    if (list.size - 1 == viewpagerPopularMovies.currentItem) viewpagerPopularMovies.currentItem =
                        0
                    else viewpagerPopularMovies.currentItem =
                        viewpagerPopularMovies.currentItem + 1
                    timer!!.start()
                } catch (t: Throwable) {
                    timer!!.cancel()
                }
            }
            timer!!.start()
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            model.loadPopularMovies()
            model.getLastNews()
            model.getNeedWatch()

            viewpagerPopularMovies.setOnTouchListener { v, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> onUserInteraction()
                }

                v?.onTouchEvent(event) ?: true
            }

            binding.searchBtn.setOnClickListener {
                findNavController().navigate(
                    R.id.searchScreen,
                    null,
                    animationTransaction().build()
                )
            }

            seeAllNowPlayingMovies.setOnClickListener {
                findNavController().navigate(
                    R.id.popularSeeAllScreen,
                    null,
                    animationTransaction().build()
                )
            }
        }


    }

    private fun onClickMovieItem(movie: MovieInfo) {
//        val action =
//            HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movie, MediaTypeEnum.MOVIE)
//        findNavController().navigate(action)
        val bundle = Bundle()
        val data = movie
        bundle.putSerializable("data", data)
        findNavController().navigate(
            R.id.detailScreen,
            bundle,
            animationTransaction().build()
        )
    }

    private fun onClickMovieItemPlay(movie: MovieInfo) {
        val bundle = Bundle()
        val data = movie
        bundle.putSerializable("data", data)
        findNavController().navigate(
            R.id.detailScreen,
            bundle,
            animationTransaction().build()
        )
//        val action =
//            HomeFragmentDirections.actionHomeFragmentToVideoPlayerFragment(
//                videoId = null,
//                id = movie,
//                mediaType = MediaTypeEnum.MOVIE
//            )
//        findNavController().navigate(action)
    }

    private fun onClickMovieItemAddList(
        movie: Int,
        isBookmarked: Boolean,
        bookmark: MovieInfo
    ) {
        if (isBookmarked){
            model.removeFavMovie(bookmark.href)

        }else{
            model.addFavMovie(bookmark)
        }
//        with(viewModel) {
//            if (isBookmarked) removeBookmark(movie)
//            else addBookmark(bookmark)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onStopTimer()
    }


    private fun onStopTimer() {
        if (timer != null) timer!!.cancel()
    }

    private fun onUserInteraction() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.start()
        }
    }

    override fun onResume() {
        super.onResume()
    }

}