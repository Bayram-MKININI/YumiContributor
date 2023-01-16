package net.noliaware.yumi_contributor.commun.presentation.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T> internal constructor(
    private val view: View,
    private val expression: (T, View) -> Unit
) : RecyclerView.ViewHolder(view) {
    fun bind(item: T) {
        expression(item, view)
    }
}