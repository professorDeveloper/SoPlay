package com.azamovme.soplay.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.databinding.ItemMovieSerieBinding
import com.azamovme.soplay.utils.Constants
import com.azamovme.soplay.utils.loadImage
import com.azamovme.soplay.utils.setAnimation

class MovieAdapter(
    private val activity: FragmentActivity,
) : RecyclerView.Adapter<MovieAdapter.MovieVh>() {
    var list = ArrayList<MovieInfo>()

    inner class MovieVh(private val binding: ItemMovieSerieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: MovieInfo) {
            setAnimation(activity, binding.root)
            binding.apply {
                titleItem.isSelected = true
                titleItem.text = item.title
                itemImg.loadImage(Constants.mainUrl + item.image)
                itemCompactScore.text = item.rating ?: "+0"
                yearTxt.text = item.year + "•"
                if (item.quality.isEmpty()) {
                    binding.itemFormat.isVisible = false
                } else {
                    if (item.quality.size >= 2) {
                        format1.text = item.quality.get(0) + "\n" + item.quality.get(1)
                    } else {
                        format1.text = item.quality.get(0)
                    }
                }
                binding.root.setOnClickListener {
                    setItemClickListener.invoke(item)
                }
                movieType.text = item.genre
            }
        }
    }

    lateinit var setItemClickListener: (MovieInfo) -> Unit

    fun setItemClickListener(block: (MovieInfo) -> Unit) {
        setItemClickListener = block
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVh {
        return MovieVh(
            ItemMovieSerieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieVh, position: Int) {
        holder.onBind(list[position])
    }


    fun submitList(newList: ArrayList<MovieInfo>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}