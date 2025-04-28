package com.azamovme.soplay.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.databinding.BannerItemBinding
import com.azamovme.soplay.utils.Constants.mainUrl
import com.azamovme.soplay.utils.loadImage
class ViewPagerAdapter(
    private val itemList: List<MovieInfo>,
    private val itemCheckList: List<Boolean>,
    private val onClickMovie: ((MovieInfo) -> Unit)? = null,
    private val onClickPlay: ((MovieInfo) -> Unit)? = null,
    private val onClickAddList: ((movieIndex: Int, isBookmarked: Boolean, movie: MovieInfo) -> Unit)? = null
) : PagerAdapter() {

    override fun getCount(): Int = itemList.size

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        // If no data, bail out with a placeholder
        if (itemList.isEmpty()) {
            val placeholder = TextView(parent.context).apply {
                text = "No items to display"
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            parent.addView(placeholder)
            return placeholder
        }

        val binding = BannerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val movie = itemList[position]
        binding.apply {
            backDrop.loadImage(mainUrl + movie.image)
            titleTv.text = movie.title

            backDrop.setOnClickListener { onClickMovie?.invoke(movie) }
            playBtn.setOnClickListener { onClickPlay?.invoke(movie) }

            // Safely get the bookmark state, default to false if out of bounds
            addListBtn.isChecked = itemCheckList.getOrNull(position) ?: false

            addListBtn.setOnClickListener {
                val isBookmarked = addListBtn.isChecked
                onClickAddList?.invoke(position, isBookmarked, movie)
            }
        }

        parent.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // Actually remove the view when it's no longer needed
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
        view === `object`

    override fun getItemPosition(`object`: Any): Int {
        // Force recreation on notifyDataSetChanged()
        return POSITION_NONE
    }
}
