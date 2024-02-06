package com.azamovhudstc.soplay.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.azamovhudstc.soplay.databinding.OverlapLoadingViewLayoutBinding

class OverlapLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    val binding = OverlapLoadingViewLayoutBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {

    }

    fun loadingStateType(stateType: STATETYPE) {
        when (stateType) {
            STATETYPE.LOADING -> {
                binding.loadingView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.VISIBLE
                binding.ivIcon.visibility = View.GONE
            }
            STATETYPE.ERROR -> {
                binding.loadingView.visibility = View.VISIBLE
                binding.   progressBar.visibility = View.GONE
                binding.   ivIcon.visibility = View.VISIBLE
            }
            STATETYPE.DONE -> {
                binding.loadingView.visibility = View.GONE
            }
        }
    }

    enum class STATETYPE {
        ERROR, LOADING, DONE
    }

}