package net.noliaware.yumi_contributor.commun.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

fun String.decorateWords(
    wordsToDecorate: List<DecoratedText>
): SpannableString {
    val decoratedText = SpannableString(this)
    wordsToDecorate.forEach { decorated ->
        val startIndex = indexOf(decorated.textToDecorate).takeIf { it != -1 } ?: return@forEach
        val endIndex = decorated.endIndex ?: (startIndex + decorated.textToDecorate.length)
        decorated.color?.let {
            decoratedText.setSpan(
                ForegroundColorSpan(it),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        decorated.typeface?.let {
            decoratedText.setSpan(
                TypefaceSpanCompat(it),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    return decoratedText
}

data class DecoratedText(
    val textToDecorate: String,
    val endIndex: Int? = null,
    val color: Int? = null,
    val typeface: Typeface? = null,
)