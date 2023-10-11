package net.noliaware.yumi_contributor.feature_alerts.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.feature_alerts.presentation.adapters.AlertAdapter
import net.noliaware.yumi_contributor.feature_alerts.presentation.views.AlertsView

@AndroidEntryPoint
class AlertsFragment : Fragment() {

    private var alertsView: AlertsView? = null
    private val viewModel by viewModels<AlertsFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.alerts_layout, container, false).apply {
            alertsView = this as AlertsView
            alertsView?.alertAdapter = AlertAdapter()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertsView?.setLoadingVisible(true)
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            alertsView?.alertAdapter?.loadStateFlow?.collectLatest { loadState ->
                val noAlertsLoaded = (alertsView?.alertAdapter?.itemCount ?: 0) < 1
                when {
                    handlePaginationError(loadState) -> alertsView?.stopLoading()
                    loadState.refresh is LoadState.NotLoading -> {
                        alertsView?.setLoadingVisible(false)
                        alertsView?.setEmptyMessageVisible(noAlertsLoaded)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAlerts().collectLatest {
                alertsView?.alertAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                alertsView?.alertAdapter?.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        alertsView = null
        super.onDestroyView()
    }
}