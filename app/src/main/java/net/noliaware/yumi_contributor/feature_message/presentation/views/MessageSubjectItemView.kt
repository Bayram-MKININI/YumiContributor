package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class MessageSubjectItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var subjectView: TextView

    data class MessageSubjectItemViewAdapter(
        val subject: String,
        val backgroundDrawable: Int
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        subjectView = findViewById(R.id.subject_text_view)
    }

    fun fillViewWithData(messageSubjectItemViewAdapter: MessageSubjectItemViewAdapter) {
        subjectView.text = messageSubjectItemViewAdapter.subject
        setBackgroundResource(messageSubjectItemViewAdapter.backgroundDrawable)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        subjectView.measureWrapContent()

        val viewHeight = convertDpToPx(42)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        subjectView.layoutToTopLeft(
            convertDpToPx(15),
            (viewHeight - subjectView.measuredHeight) / 2
        )
    }
}