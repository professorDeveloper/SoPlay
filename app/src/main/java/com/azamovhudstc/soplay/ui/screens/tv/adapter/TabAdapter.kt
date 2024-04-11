package com.azamovhudstc.soplay.ui.screens.tv.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.azamovhudstc.soplay.ui.screens.tv.randomtv.RandomTvItemScreen

class TabAdapter(var arrayList: ArrayList<Pair<String, Int>>, fragmentManager: FragmentActivity) :
    FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        val screen = RandomTvItemScreen()
        val bundle = Bundle()
        bundle.putInt("categoryId", arrayList.get(position).second)
        screen.arguments = bundle
        return screen
    }
}