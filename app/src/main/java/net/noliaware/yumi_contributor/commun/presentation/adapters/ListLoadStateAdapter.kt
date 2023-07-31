package net.noliaware.yumi_contributor.commun.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.inflate

class ListLoadStateAdapter : LoadStateAdapter<ListLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = LoadStateViewHolder(
        parent.inflate(R.layout.load_state_layout)
    )

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.itemView.findViewById<CircularProgressIndicator>(R.id.load_state_progress).apply {
            isVisible = loadState is LoadState.Loading
        }
    }

    class LoadStateViewHolder(view: View) : RecyclerView.ViewHolder(view)
}