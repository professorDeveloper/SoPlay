package com.azamovhudstc.soplay.ui.screens.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.DetailScreenBinding
import com.azamovhudstc.soplay.utils.Constants.mainUrl
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.utils.hide
import com.azamovhudstc.soplay.utils.loadImage
import com.azamovhudstc.soplay.utils.show
import com.azamovhudstc.soplay.viewmodel.imp.DetailViewModelImpl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

class DetailScreen : Fragment() {

    private val viewModel by viewModels<DetailViewModelImpl>()

    private var _binding: DetailScreenBinding? = null
    private val binding get() = _binding!!
    lateinit var data: MovieInfo

    private lateinit var bottomSheet: com.google.android.material.bottomsheet.BottomSheetDialog
    private lateinit var epList: MutableList<Pair<String, String>>
    private lateinit var epType: String
    private lateinit var epIndex: String

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
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = resources.getColor(R.color.transparent)
        viewModel.movieDetailData.observe(this) {
            when (it) {
                is Resource.Error -> {

                }
                Resource.Loading -> {
                    binding.progress.show()
                    binding.container.hide()
                }
                is Resource.Success -> {

                    binding.progress.hide()
                    binding.container.show()
                    val response = it.data
                    binding.tvMovieTitleValue.text = data.title
                    val banner = binding.ivBackdrop
                    Glide.with(context as Context)
                        .load(GlideUrl(mainUrl + response.posterImageSrc))
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(400)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(2, 3)))
                        .into(banner)
                    binding.ivPoster.loadImage(mainUrl + response.posterImageSrc)
                    binding.tvVoteAverage.text = response.IMDB_rating
                    val listResponse = response.genres.map {
                        it.first

                    }
                    binding.tvGnreValue.text = listResponse.joinToString(", ")
                    binding.tvMovieTitleValue.isSelected = true


                    binding.epTextView.text = response.options.get(0).first
                    binding.tvDescriptionValue.text = response.description
                    binding.durationValue.text = response.duration

                    binding.yearValue.text = response.year
                    binding.countryValue.text = response.country


                    binding.epCard.setOnClickListener {

                        bottomSheet =
                            com.google.android.material.bottomsheet.BottomSheetDialog(
                                requireContext()
                            )
                        bottomSheet.setContentView(R.layout.select_season_bottom_sheet_layout)
                        bottomSheet.behavior.peekHeight = bottomSheet.behavior.maxHeight
                        bottomSheet.behavior.state =
                            com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                        bottomSheet.behavior.isDraggable = false


                        val list =
                            bottomSheet.findViewById<android.widget.ListView>(R.id.listView)
                        val editText =
                            bottomSheet.findViewById<android.widget.EditText>(R.id.text_input_edit_text)
                        val ascDscImageBtn =
                            bottomSheet.findViewById<android.widget.ImageView>(R.id.asc_dsc_image_button)
                        val normalOrderIcon =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.sort_numeric_normal
                            )
                        val reversedOrderIcon =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.sort_numeric_reversed
                            )
                        epList = response.options.toMutableList()
                        epIndex = epList.first().first
                        var orderPref = "normal"
                        if (orderPref == "reversed") {
                            epList.reverse()
                            ascDscImageBtn?.setImageDrawable(reversedOrderIcon)
                        }

                        binding.epTextView.text =
                            resources.getString(R.string.episode_text, epIndex)

                        // Episodes aye
                        epList = response.options.toMutableList()
                        epIndex = epList.first().first


                        if (orderPref == "reversed") {
                            epList.reverse()
                            ascDscImageBtn?.setImageDrawable(reversedOrderIcon)
                        }

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


                        // Toggle Asc/Desc
                        ascDscImageBtn?.setOnClickListener {
                            epList.reverse()
                            adapterForEpList.notifyDataSetChanged()
                            ascDscImageBtn.apply {
                                if (this.drawable == reversedOrderIcon) this.setImageDrawable(
                                    normalOrderIcon
                                )
                                else this.setImageDrawable(reversedOrderIcon)
                            }
                            list?.setSelection(0)
                            val order =
                                if (ascDscImageBtn.drawable == reversedOrderIcon) "reversed" else "normal"
                            orderPref = order
                        }


                        val pos = adapterForEpList.getPosition(epIndex)
                        list?.setSelection(pos)
                        list?.setOnItemClickListener { _, view, _, _ ->
                            val episodeString = (view as android.widget.TextView).text.toString()
                            epIndex = episodeString
                            binding.epTextView.text =
                                resources.getString(R.string.episode_text, epIndex)
                            bottomSheet.dismiss()
                        }


                        bottomSheet.show()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.parseDetailByMovieInfo(data)

    }


}