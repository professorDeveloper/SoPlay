package com.azamovhudstc.soplay.ui.screens.tv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azamovhudstc.soplay.databinding.TvItemBinding
import com.azamovhudstc.soplay.tv.stv.response.Post
import com.azamovhudstc.soplay.utils.loadImage

class RandomTvAdapter : RecyclerView.Adapter<RandomTvAdapter.RandomVh>() {
    private val list = ArrayList<Post>()

    lateinit var itemClickListenerByOne :((Post)->Unit)

    fun setItemClickListener(listener:(Post)->Unit){
        itemClickListenerByOne=listener
    }

    inner class RandomVh(var binding: TvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: Post) {
            binding.apply {
                binding.root.loadImage("http://tv.musical.uz//upload/${data.channel_image}")
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

    fun submitList(newList: ArrayList<Post>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}