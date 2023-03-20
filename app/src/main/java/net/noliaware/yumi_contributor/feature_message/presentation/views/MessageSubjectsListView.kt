package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.BaseAdapter
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft

class MessageSubjectsListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var contentView: View
    private lateinit var recyclerView: RecyclerView
    private val messageSubjectAdapters = mutableListOf<MessageSubjectItemView.MessageSubjectItemViewAdapter>()
    private var callback: MessageSubjectsListViewCallback? = null

    interface MessageSubjectsListViewCallback {
        fun onSubjectClickedAtIndex(index: Int)
        fun onClickOutside()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        setOnClickListener {
            callback?.onClickOutside()
        }
        contentView = findViewById(R.id.content_layout)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            BaseAdapter(messageSubjectAdapters).apply {
                expressionViewHolderBinding = { eachItem, view ->
                    (view as MessageSubjectItemView).fillViewWithData(eachItem)
                }
                expressionOnCreateViewHolder = { viewGroup ->
                    viewGroup.inflate(R.layout.message_subject_item_layout, false)
                }
                onItemClicked = { position ->
                    callback?.onSubjectClickedAtIndex(position)
                }
                it.adapter = this
            }
        }
    }

    fun setMessageSubjectsListViewCallback(callback: MessageSubjectsListViewCallback) {
        this.callback = callback
    }

    fun fillViewWithData(adapters: List<MessageSubjectItemView.MessageSubjectItemViewAdapter>) {
        if (messageSubjectAdapters.isNotEmpty())
            messageSubjectAdapters.clear()
        messageSubjectAdapters.addAll(adapters)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        val contentViewWidth = viewWidth * 9 / 10
        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        recyclerView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            (viewHeight - contentView.measuredHeight) / 2
        )

        recyclerView.layoutToTopLeft(0, 0)
    }
}