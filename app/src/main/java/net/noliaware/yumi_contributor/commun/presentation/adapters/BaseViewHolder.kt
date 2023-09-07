package net.noliaware.yumi_contributor.commun.presentation.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T> internal constructor(
    private val view: View,
    private val expression: ((T, View) -> Unit)? = null,
    private val onItemClicked: ((Int) -> Unit)? = null
) : RecyclerView.ViewHolder(view) {
    init {
        onItemClicked?.let {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked.invoke(bindingAdapterPosition)
                }
            }
        }
    }
    fun bind(item: T) {
        expression?.let { it(item, view) }
    }
}