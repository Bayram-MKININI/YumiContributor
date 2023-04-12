package net.noliaware.yumi_contributor.feature_message.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import net.noliaware.yumi_contributor.R

class MessageSubjectsAdapter(
    private val context: Context,
    objects: List<String>
) : ArrayAdapter<String>(context, R.layout.message_subject_selected_item_layout, objects) {

    override fun getCount(): Int {
        return super.getCount() - 1
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val subjectTextView = LayoutInflater.from(context).inflate(
            R.layout.message_subject_selected_item_layout,
            parent,
            false
        ) as TextView

        if (position == count) {
            subjectTextView.text = ""
            subjectTextView.hint = getItem(count)
        } else {
            subjectTextView.text = getItem(position)
        }
        return subjectTextView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val subjectTextView = LayoutInflater.from(context).inflate(
            R.layout.message_subject_dropdown_item_layout,
            parent,
            false
        ) as TextView

        subjectTextView.text = getItem(position)
        subjectTextView.setBackgroundResource(
            if (position % 2 == 0) {
                R.drawable.rectangle_white_ripple
            } else {
                R.drawable.rectangle_grey7_ripple
            }
        )
        return subjectTextView
    }
}