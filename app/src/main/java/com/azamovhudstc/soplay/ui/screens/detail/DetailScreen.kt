package com.azamovhudstc.soplay.ui.screens.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.DetailScreenBinding
import com.azamovhudstc.soplay.ui.activity.PlayerActivity
import com.azamovhudstc.soplay.utils.*
import com.azamovhudstc.soplay.utils.Constants.mainUrl
import com.azamovhudstc.soplay.viewmodel.imp.DetailViewModelImpl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation

@AndroidEntryPoint
class DetailScreen : Fragment() {

    private val viewModel by viewModels<DetailViewModelImpl>()

    private var _binding: DetailScreenBinding? = null
    private val binding get() = _binding!!
    lateinit var data: MovieInfo

    private lateinit var bottomSheet: com.google.android.material.bottomsheet.BottomSheetDialog
    private lateinit var epList: MutableList<Pair<String, String>>
    private lateinit var epIndex: String
    private var epIndexForEp: Int = 0
    private fun inFav() {
        println("In Fav")
        binding.buttonFavorite.setImageResource(R.drawable.ic_heart_minus)
    }

    private fun notInFav() {
        println("Not in Fav")
        binding.buttonFavorite.setImageResource(R.drawable.ic_heart_plus)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getSerializable("data") as MovieInfo
        val window = requireActivity().window
        window.statusBarColor = resources.getColor(R.color.transparent)
        viewModel.movieDetailData.observe(this) {
            when (it) {
                is Resource.Error -> {
                    binding.progress.hide()
                    binding.container.show()
                    Snackbar.make(
                        binding.root,
                        it.throwable.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).setAction("Reload") {
                        viewModel.parseDetailByMovieInfo(data)
                    }

                }
                Resource.Loading -> {
                    binding.progress.show()
                    binding.container.hide()
                }
                is Resource.Success -> {
                    // Check Favorite
                    data.href.let { viewModel.isFavMovie(it) }

                    viewModel.isFavMovieData.observe(this) {
                        if (it) {
                            inFav()
                            binding.favCard.setOnClickListener {
                                viewModel.removeFavMovie(data.href)
                            }
                        } else {
                            notInFav()
                            binding.favCard.setOnClickListener {
                                viewModel.addFavMovie(
                                    data
                                )
                            }
                        }

                    }



                    binding.progress.hide()
                    binding.container.show()
                    binding.tvDescriptionValue.slideUp(700, 1)
                    binding.linearLayout2.slideUp(700, 1)
                    binding.mainContainer.slideStart(700, 1)
                    binding.cardView2.slideUp(700, 1)

                    val response = it.data
                    binding.tvMovieTitleValue.text = data.title
                    val banner = binding.ivBackdrop
                    Glide.with(context as Context)
                        .load(GlideUrl(mainUrl + response.posterImageSrc))
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(400)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(2, 3)))
                        .into(banner)
                    binding.ivPoster.loadImage(mainUrl + response.posterImageSrc)
                    binding.tvVoteAverage.text = data.rating
                    val listResponse = response.genres.map {
                        it.first

                    }
                    binding.tvGnreValue.text = listResponse.joinToString(", ")
                    binding.tvMovieTitleValue.isSelected = true

                    binding.playCard.setOnClickListener {
                        val hrefList = response.options.map {
                            it.second
                        }
                        PlayerActivity.currentEpIndex = epIndexForEp
                        PlayerActivity.epCount = if (hrefList.size > 3) epList.size else 1
                        PlayerActivity.epList = hrefList as ArrayList<String>
                        PlayerActivity.epListByName = epList as ArrayList<Pair<String, String>>
                        PlayerActivity.movieInfo = data
                        PlayerActivity.pipStatus = true
                        val intent = PlayerActivity.newIntent(requireContext(), response)
                        startActivity(intent)

                    }


                    binding.epTextView.text = response.options.get(0).first
                    binding.tvDescriptionValue.text = response.description
                    binding.durationValue.text = response.duration

                    binding.yearValue.text = response.year
                    binding.countryValue.text = response.country
                    epList = response.options.toMutableList()
                    epIndex = epList.first().first
                    setUpEpisodeSheet(it.data)

                    binding.epCard.setOnClickListener {
                        bottomSheet.show()
                    }
                }
            }
        }
    }

    private fun setUpEpisodeSheet(response: FullMovieData) {

        bottomSheet =
            com.google.android.material.bottomsheet.BottomSheetDialog(
                requireContext()
            )
        bottomSheet.setContentView(R.layout.select_season_bottom_sheet_layout)
        bottomSheet.behavior.peekHeight = bottomSheet.behavior.maxHeight
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.behavior.isDraggable = false
        val list =
            bottomSheet.findViewById<android.widget.ListView>(R.id.listView)
        val editText =
            bottomSheet.findViewById<android.widget.EditText>(R.id.text_input_edit_text)

        binding.epTextView.text =
            resources.getString(R.string.episode_text, epIndex)

        // Episodes aye
        epList = response.options.toMutableList()
        epIndex = epList.first().first


        // Setup the views that uses the above
        // 1. Ep text
        binding.epTextView.text =
            resources.getString(R.string.episode_text, epIndex)


        val adapterForEpList = android.widget.ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            epList.map { it.first }
        ).apply {
            list?.adapter = this
        }

        // Search
        editText?.addTextChangedListener {
            val searchedText = it.toString()
            adapterForEpList.filter.filter(searchedText)
        }


        val pos = adapterForEpList.getPosition(epIndex)
        list?.setSelection(pos)
        list?.setOnItemClickListener { _, view, position, _ ->
            val episodeString = (view as android.widget.TextView).text.toString()
            epIndex = episodeString
            epIndexForEp = position

            binding.epTextView.text =
                resources.getString(R.string.episode_text, epIndex)
            bottomSheet.dismiss()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.parseDetailByMovieInfo(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }


}