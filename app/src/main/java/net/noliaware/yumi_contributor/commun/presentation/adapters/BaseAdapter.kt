package net.noliaware.yumi_contributor.commun.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class BaseAdapter<T : Any>(
    private val dataSet: List<T>,
    compareItems: ((old: T, new: T) -> Boolean)? = null,
    compareContents: ((old: T, new: T) -> Boolean)? = null
) : ListAdapter<T, BaseViewHolder<T>>(DiffCallback(compareItems, compareContents)) {

    var expressionViewHolderBinding: ((T, View) -> Unit)? = null
    var expressionOnCreateViewHolder: ((ViewGroup) -> View)? = null
    var onItemClicked: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<T> {
        return expressionOnCreateViewHolder?.let {
            it(parent)
        }?.let { view ->
            BaseViewHolder(
                view = view,
                expression = expressionViewHolderBinding,
                onItemClicked = onItemClicked?.let {
                    { position ->
                        dataSet[position].let {
                            onItemClicked?.invoke(position)
                        }
                    }
                }
            )
        }!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private class DiffCallback<K : Any>(
        private val compareItems: ((old: K, new: K) -> Boolean)? = null,
        private val compareContents: ((old: K, new: K) -> Boolean)? = null
    ) : DiffUtil.ItemCallback<K>() {
        override fun areItemsTheSame(old: K, new: K) = compareItems?.invoke(old, new) ?: false
        override fun areContentsTheSame(old: K, new: K) = compareContents?.invoke(old, new) ?: false
    }
}