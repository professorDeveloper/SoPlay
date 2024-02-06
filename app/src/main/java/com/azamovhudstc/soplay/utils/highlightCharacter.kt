package com.azamovhudstc.soplay.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavOptions
import com.azamovhudstc.soplay.R
import com.azamovhudstc.soplay.app.App

fun highlightCharacter(text: String, numberToFind: Int): Spannable {
    val spannableText = SpannableString(text)

    // Regex orqali matndan raqamni izlash
    val regex = "(?<=\\d)[a-zA-Z](?=\\d)".toRegex()
    val matches = regex.findAll(text)

    for (match in matches) {
        val matchText = match.value
        val startIndex = match.range.first + 1 // Raqamdan keyin kelgan harfning indeksi
        val endIndex = match.range.first + 2 // Raqam bilan harfning oldindagi indeksi

        // Raqamni izlayotganimizdan emas, izlanayotgan harfni qizil rangda qo'yamiz
        val colorRed = ForegroundColorSpan(Color.RED)
        spannableText.setSpan(colorRed, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return spannableText
}

fun Fragment.animationTransactionClearStack(clearFragmentID: Int): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()
    navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right)
        .setPopUpTo(clearFragmentID, true)
    return navBuilder
}

fun Fragment.animationTransaction(): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()
    navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right)
    return navBuilder
}