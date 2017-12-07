package com.mentorz.block

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mentorz.adapter.BaseAdapter
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.LoadingDelegateAdapter
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.LoadingItem

class BlockedUserAdapter(listener: BlockedUserDelegateAdapter.onViewSelectedListener) : BaseAdapter<BlockedUserAdapter, RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var loadingItem:LoadingItem = LoadingItem()

    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, BlockedUserDelegateAdapter(this, listener))
        items = ArrayList()

    }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(position, holder, this.items[position])

    }

    override fun getItemViewType(position: Int): Int {
        return this.items[position].getViewType()
    }

    fun addUsers(item: List<UserListItem>) {
        // first remove loading and notify
        val initPosition = items.size - 1
        items.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        // insert news and the loading at the end of the list
        items.addAll(item)
        items.add(loadingItem)
        notifyItemRangeChanged(initPosition, items.size + 1  /* plus loading item */)

    }
    fun clearAndUser(item: List<UserListItem>) {
        setLoaded()
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }
    fun removeUser(item: UserListItem) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }
    fun addLoaderAtBottom(){
        items.add(loadingItem)
        notifyDataSetChanged()
    }
    fun removeLoaderFromBottom() {
        setLoaded()
        if (!items.isEmpty()&& items[items.size-1]is LoadingItem) {
            items.removeAt(items.size-1)
            notifyItemRemoved(items.size)
        }
        else{
            notifyDataSetChanged()
        }
    }

    override fun getRows(): List<Any?>? {
        return items
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it }
    }


}