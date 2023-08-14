package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.feature_message.presentation.adapters.MessageAdapter

class MessagesListView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    var messageAdapter
        get() = recyclerView.adapter as MessageAdapter
        set(adapter) {
            recyclerView.adapter = adapter
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recycler_view)
        emptyView = findViewById(R.id.empty_message_text_view)

        recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.addItemDecoration(MarginItemDecoration(convertDpToPx(20)))
        }
    }

    fun setEmptyMessageVisible(visible: Boolean) {
        if (visible) {
            emptyView.isVisible = true
            recyclerView.isGone = true
        } else {
            emptyView.isGone = true
            recyclerView.isVisible = true
        }
    }

    fun setEmptyMessageText(text: String) {
        emptyView.text = text
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (recyclerView.isVisible) {
            recyclerView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
            )
        }

        if (emptyView.isVisible) {
            emptyView.measureWrapContent()
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        if (emptyView.isVisible) {
            emptyView.layoutToTopLeft(
                (viewWidth - emptyView.measuredWidth) / 2,
                (viewHeight - convertDpToPx(50) - emptyView.measuredHeight) / 2
            )
        } else {
            recyclerView.layoutToTopLeft(0, 0)
        }
    }
}