package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.inflate

class VoucherRequestsAdapter(
    context: Context,
    objects: List<String>
) : ArrayAdapter<String>(context, 0, objects) {

    override fun getCount(): Int {
        return super.getCount() - 1
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ) = View(context)

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val labelTextView = (convertView ?: parent.inflate(
            R.layout.label_dropdown_item_layout,
        )) as TextView

        labelTextView.text = getItem(position)
        labelTextView.setBackgroundResource(
            if (position % 2 == 0) {
                R.drawable.rectangle_white_ripple
            } else {
                R.drawable.rectangle_grey7_ripple
            }
        )
        return labelTextView
    }
}