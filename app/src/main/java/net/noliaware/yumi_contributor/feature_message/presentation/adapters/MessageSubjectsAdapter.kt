package net.noliaware.yumi_contributor.feature_message.presentation.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.inflate

class MessageSubjectsAdapter(
    context: Context,
    objects: List<String>
) : ArrayAdapter<String>(context, 0, objects) {

    override fun getCount(): Int {
        return super.getCount() - 1
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val subjectTextView = (convertView ?: parent.inflate(
            R.layout.message_subject_selected_item_layout
        )) as TextView

        if (position == count) {
            subjectTextView.text = ""
            subjectTextView.hint = getItem(count)
        } else {
            subjectTextView.text = getItem(position)
        }
        return subjectTextView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val subjectTextView = (convertView ?: parent.inflate(
            R.layout.message_subject_dropdown_item_layout
        )) as TextView

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