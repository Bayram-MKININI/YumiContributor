package net.noliaware.yumi_contributor.feature_alerts.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ItemViewHolder
import net.noliaware.yumi_contributor.commun.presentation.mappers.PriorityMapper
import net.noliaware.yumi_contributor.commun.util.parseTimeString
import net.noliaware.yumi_contributor.commun.util.parseToShortDate
import net.noliaware.yumi_contributor.feature_alerts.domain.model.Alert
import net.noliaware.yumi_contributor.feature_alerts.presentation.views.AlertItemView
import net.noliaware.yumi_contributor.feature_alerts.presentation.views.AlertItemView.AlertItemViewAdapter

class AlertAdapter : PagingDataAdapter<Alert, ItemViewHolder<AlertItemView>>(AlertComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder<AlertItemView>(
        LayoutInflater.from(parent.context).inflate(R.layout.alert_item_layout, parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder<AlertItemView>, position: Int) {
        getItem(position)?.let { alert ->
            holder.heldItemView.fillViewWithData(
                mapAdapter(alert, holder)
            )
        }
    }

    private fun mapAdapter(
        alert: Alert,
        holder: ItemViewHolder<AlertItemView>
    ) = AlertItemViewAdapter(
        priorityIconRes = PriorityMapper().mapPriorityIcon(alert.alertPriority),
        time = holder.itemView.context.getString(
            R.string.received_at,
            parseToShortDate(alert.alertDate),
            parseTimeString(alert.alertTime)
        ),
        body = alert.alertText
    )

    object AlertComparator : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(
            oldItem: Alert,
            newItem: Alert
        ): Boolean {
            return oldItem.alertId == newItem.alertId
        }

        override fun areContentsTheSame(
            oldItem: Alert,
            newItem: Alert
        ): Boolean {
            return oldItem == newItem
        }
    }
}