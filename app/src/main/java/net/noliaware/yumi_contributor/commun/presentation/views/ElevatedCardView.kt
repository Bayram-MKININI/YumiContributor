package net.noliaware.yumi_contributor.commun.presentation.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx

open class ElevatedCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : MaterialCardView(context, attrs, defStyle) {
    override fun onFinishInflate() {
        super.onFinishInflate()
        cardElevation = convertDpToPx(16) * 1f
        setBackgroundResource(R.drawable.rectangle_rounded_white)
    }
}