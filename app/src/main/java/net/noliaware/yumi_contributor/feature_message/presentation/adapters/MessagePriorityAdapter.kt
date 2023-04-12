package net.noliaware.yumi_contributor.feature_message.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessagePriorityDropdownItemView
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessagePrioritySelectedItemView
import net.noliaware.yumi_contributor.feature_message.presentation.views.PriorityUI

class MessagePriorityAdapter(
    private val context: Context,
    objects: List<PriorityUI>
) : ArrayAdapter<PriorityUI>(context, R.layout.message_priority_selected_item_layout, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val selectedView = LayoutInflater.from(context).inflate(
            R.layout.message_priority_selected_item_layout,
            parent,
            false
        ) as MessagePrioritySelectedItemView

        getItem(position)?.resIcon?.let {
            selectedView.setIconDrawable(it)
        }
        return selectedView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.message_priority_dropdown_item_layout,
            parent,
            false
        ) as MessagePriorityDropdownItemView

        getItem(position)?.let { priorityUI ->
            itemView.fillViewWithData(
                MessagePriorityDropdownItemView.MessagePriorityDropdownItemViewAdapter(
                    title = priorityUI.label,
                    iconDrawable = priorityUI.resIcon
                )
            )
        }
        itemView.setBackgroundResource(
            if (position % 2 == 0) {
                R.drawable.rectangle_white_ripple
            } else {
                R.drawable.rectangle_grey7_ripple
            }
        )
        return itemView
    }
}