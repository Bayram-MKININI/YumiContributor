package net.noliaware.yumi_contributor.commun.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

const val VERTICAL = 0
const val GRID = 1

class MarginItemDecoration(
    private val spacing: Int,
    private val displayMode: Int = VERTICAL,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {
        when (displayMode) {

            VERTICAL -> {

                outRect.set(
                    spacing,
                    spacing,
                    spacing,
                    if (position == itemCount - 1)
                        spacing
                    else
                        0
                )
            }

            GRID -> if (layoutManager is GridLayoutManager) {
                outRect.set(spacing, spacing, spacing, spacing)
            }
        }
    }
}