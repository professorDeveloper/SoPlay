package com.azamovme.soplay.ui.screens.bottomsheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.azamovme.soplay.databinding.ItemDeveloperBinding
import com.azamovme.soplay.utils.Developer
import com.azamovme.soplay.utils.loadImage
import com.azamovme.soplay.utils.openLinkInBrowser
import com.azamovme.soplay.utils.setAnimation

class DevelopersAdapter(private val developers: ArrayList<Developer>,private val activity: Fragment) :
    RecyclerView.Adapter<DevelopersAdapter.DeveloperViewHolder>() {

    inner class DeveloperViewHolder(val binding: ItemDeveloperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperViewHolder {
        return DeveloperViewHolder(
            ItemDeveloperBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, position: Int) {
        val b = holder.binding
        setAnimation(b.root.context, b.root)
        val dev = developers[position]
        b.devName.text = dev.name
        b.devProfile.loadImage(dev.pfp)
        b.devRole.text = dev.role
        b.root.setOnClickListener {
            openLinkInBrowser(developers[position].url, activity.requireActivity())
        }
    }

    override fun getItemCount(): Int = developers.size
}