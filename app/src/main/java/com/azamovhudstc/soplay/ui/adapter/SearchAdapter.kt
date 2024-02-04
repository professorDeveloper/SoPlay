package com.azamovhudstc.soplay.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.databinding.AnimeItemBinding
import com.azamovhudstc.soplay.utils.Constants
import com.azamovhudstc.soplay.utils.loadImage
import com.azamovhudstc.soplay.utils.setAnimation

class SearchAdapter(
    private val activity: FragmentActivity
) : RecyclerView.Adapter<SearchAdapter.SearchVh>() {
    var list = ArrayList<MovieInfo>()

    inner class SearchVh(private val binding: AnimeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: MovieInfo) {
            setAnimation(activity, binding.root)
            binding.apply {
                titleItem.isSelected=true
                titleItem.text = item.title
                itemImg.loadImage(Constants.mainUrl + item.image)
                itemCompactScore.text = item.rating ?: "+0"
                yearTxt.text = item.year + "•"
                if (item.quality.isEmpty()) {
                    binding.itemFormat.isVisible=false
                } else {
                    if (item.quality.size >= 2) {
                        format1.text = item.quality.get(0) + "\n" + item.quality.get(1)
                    } else {
                        format1.text = item.quality.get(0)
                    }
                }
                movieType.text = item.genre
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVh {
        return SearchVh(
            AnimeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchVh, position: Int) {
        holder.onBind(list.get(position))
    }

    fun submitList(newList: ArrayList<MovieInfo>) {
        list.clear()
        list.addAll(
            newList
        )
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}