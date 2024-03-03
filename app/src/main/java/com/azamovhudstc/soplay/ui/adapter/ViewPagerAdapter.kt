package com.azamovhudstc.soplay.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.BannerItemBinding
import com.azamovhudstc.soplay.utils.Constants.mainUrl
import com.azamovhudstc.soplay.utils.loadImage

class ViewPagerAdapter(
    private val itemList: ArrayList<MovieInfo>,
    private val itemCheckList:ArrayList<Boolean>,
    private val onClickMovie: ((movieId: MovieInfo) -> Unit)?,
    private val onClickPlay: ((movieId: MovieInfo) -> Unit)?,
    private val onClickAddList: ((movieId: Int, isBookmarked: Boolean, bookmark: MovieInfo) -> Unit)?
) : PagerAdapter() {

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {

        val itemBinding =
            BannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        with(itemBinding) {
            with(itemList[position]) {

                backDrop.loadImage(mainUrl + image)

                titleTv.text = title
                backDrop.setOnClickListener {
                    onClickMovie?.invoke(this)
                }
                playBtn.setOnClickListener {
                    onClickPlay?.invoke(this)
                }
                itemBinding.addListBtn.isChecked =itemCheckList.get(position)

                addListBtn.setOnClickListener {
                    onClickAddList?.invoke(
                        position, itemCheckList.get(position),
                        itemList.get(position)
                    )
                }
            }
        }


        parent.addView(itemBinding.root, 0)

        return itemBinding.root
    }

    override fun getCount(): Int = itemList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
        view == (`object` as ConstraintLayout)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as ConstraintLayout)
    }
}