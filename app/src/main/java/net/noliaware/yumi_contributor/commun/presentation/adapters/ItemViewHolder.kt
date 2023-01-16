package net.noliaware.yumi_contributor.commun.presentation.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder<T : View> internal constructor(
    view: View,
    private val onItemClicked: ((Int) -> Unit)? = null
) : RecyclerView.ViewHolder(view) {

    @Suppress("UNCHECKED_CAST")
    val heldItemView
        get() = itemView as T

    init {
        itemView.setOnClickListener {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onItemClicked?.invoke(bindingAdapterPosition)
            }
        }
    }
}