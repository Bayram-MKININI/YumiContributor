package net.noliaware.yumi_contributor.commun.presentation.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class DataValueView(context: Context) : ViewGroup(context) {

    private lateinit var titleTextView: TextView
    private lateinit var valueTextView: TextView

    data class DataValueViewAdapter(
        val title: String = "",
        val value: String = ""
    )

    init {
        initView()
    }

    private fun initView() {

        LayoutInflater.from(context).also {
            it.inflate(R.layout.data_value_layout, this, true)
        }

        titleTextView = findViewById(R.id.title_text_view)
        valueTextView = findViewById(R.id.value_text_view)
    }

    fun fillViewWithData(profileDataViewAdapter: DataValueViewAdapter) {
        titleTextView.text = profileDataViewAdapter.title
        valueTextView.text = profileDataViewAdapter.value
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        titleTextView.measureWrapContent()

        val valueTextViewWidth = viewWidth - (titleTextView.measuredWidth + convertDpToPx(10))

        valueTextView.measure(
            MeasureSpec.makeMeasureSpec(valueTextViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val viewHeight =
            titleTextView.measuredHeight.coerceAtLeast(valueTextView.measuredHeight) + convertDpToPx(
                20
            )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(0, convertDpToPx(10))
        valueTextView.layoutToTopLeft(
            titleTextView.measuredWidth + convertDpToPx(10),
            titleTextView.top + convertDpToPx(2)
        )
    }
}