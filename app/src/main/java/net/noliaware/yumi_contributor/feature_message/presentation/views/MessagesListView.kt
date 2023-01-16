package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.feature_message.presentation.adapters.MessageAdapter

class MessagesListView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    val getMessageAdapter get() = adapter as MessageAdapter

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(MarginItemDecoration(convertDpToPx(20)))
    }
}