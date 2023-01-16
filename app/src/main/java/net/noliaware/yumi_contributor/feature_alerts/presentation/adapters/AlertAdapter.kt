package net.noliaware.yumi_contributor.feature_alerts.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ItemViewHolder
import net.noliaware.yumi_contributor.commun.util.parseTimeString
import net.noliaware.yumi_contributor.commun.util.parseToShortDate
import net.noliaware.yumi_contributor.feature_alerts.domain.model.Alert
import net.noliaware.yumi_contributor.feature_alerts.domain.model.AlertPriority
import net.noliaware.yumi_contributor.feature_alerts.presentation.views.AlertItemView

class AlertAdapter : PagingDataAdapter<Alert, ItemViewHolder<AlertItemView>>(AlertComparator) {

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
    ) = AlertItemView.AlertItemViewAdapter(
        priority = resolveAlertPriority(alert.alertLevel),
        time = holder.itemView.context.getString(
            R.string.received_at,
            parseToShortDate(alert.alertDate),
            parseTimeString(alert.alertTime)
        ),
        body = alert.alertText
    )

    private fun resolveAlertPriority(alertLevel: Int) = when (alertLevel) {
        2 -> AlertPriority.WARNING
        3 -> AlertPriority.IMPORTANT
        4 -> AlertPriority.CRITICAL
        else -> AlertPriority.INFORMATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder<AlertItemView>(
            LayoutInflater.from(parent.context).inflate(R.layout.alert_item_layout, parent, false)
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