package com.azamovhudstc.soplay.ui.screens.tv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.soplay.data.Movie
import com.azamovhudstc.soplay.databinding.TvItemBinding
import com.azamovhudstc.soplay.utils.loadImage

class TvAdapter : RecyclerView.Adapter<TvAdapter.TvViewHolder>() {
    private val list = ArrayList<Movie>()

    var onItemClick: ((Movie) -> Unit)? = null

    inner class TvViewHolder(val binding: TvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(movie: Movie) {
            binding.apply {
                binding.root.setOnClickListener {
                    onItemClick?.invoke(movie)
                }
                root.loadImage(movie.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvViewHolder {
        return TvViewHolder(
            TvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    fun setItemClickListener(newListener: (Movie) -> Unit) {
        onItemClick = newListener
    }

    fun submitList(newList: ArrayList<Movie>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TvViewHolder, position: Int) {
        holder.onBind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }
}