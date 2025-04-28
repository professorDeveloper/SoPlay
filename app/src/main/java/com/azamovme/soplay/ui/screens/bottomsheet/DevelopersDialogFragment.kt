package com.azamovme.soplay.ui.screens.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.azamovme.soplay.databinding.BottomSheetDevelopersBinding
import com.azamovme.soplay.ui.screens.bottomsheet.adapter.DevelopersAdapter
import com.azamovme.soplay.utils.Developer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DevelopersDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetDevelopersBinding? = null
    private val binding get() = _binding!!

    private val developers = arrayListOf(
        Developer(
            "Azamov X ㋡",
            "https://github.com/professorDeveloper/Scraping-Tutorial/assets/108933534/b7c85044-3c9c-4d2f-8146-529e380ca3e9",
            "Owner",
            "https://t.me/stc_android"
        ),
////        Developer(
////            "KBOT09",
////            "https://avatars.githubusercontent.com/u/88382789?v=4",
////            "Designer",
////            "https://t.me/KBOT09"
////        ),
////        Developer(
////            "UzModder",
////            "https://github.com/professorDeveloper/Scraping-Tutorial/assets/108933534/baa59816-44cf-4afa-9330-9183b6682177",
////            "Ads Contributor",
////            "https://t.me/uzmodder"
////        ),
//
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDevelopersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.devsRecyclerView.adapter = DevelopersAdapter(developers,this)
        binding.devsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
