package com.azamovme.soplay.ui.screens.tv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovme.soplay.databinding.TvItemBinding
import com.azamovme.soplay.tv.stv.response.channels_ontv.Data
import com.azamovme.soplay.utils.loadImage

class RandomTvAdapter : RecyclerView.Adapter<RandomTvAdapter.RandomVh>() {
    private val list = ArrayList<Data>()

    lateinit var itemClickListenerByOne: ((Data) -> Unit)

    fun setItemClickListener(listener: (Data) -> Unit) {
        itemClickListenerByOne = listener
    }

    inner class RandomVh(var binding: TvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: Data) {
            binding.apply {
                binding.root.loadImage(data.file?.url ?: "https://clackamasriver.org/wp-content/uploads/2024/06/placeholder-fallback-1160-1024x516-1160x585.png")
                root.setOnClickListener {
                    itemClickListenerByOne.invoke(data)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomVh {
        return RandomVh(TvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RandomVh, position: Int) {
        holder.onBind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(newList: List<Data>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}