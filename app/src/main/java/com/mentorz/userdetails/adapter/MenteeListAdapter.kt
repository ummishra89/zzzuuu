package com.mentorz.userdetails.adapter

import android.content.Context
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


class MenteeListAdapter(context: Context,listener: MenteeDelegateAdapter.onViewSelectedListener) : BaseAdapter<MenteeListAdapter,RecyclerView.ViewHolder>() {

    private var items: MutableList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    var context =context
    private var loadingItem = LoadingItem()

    init {

        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, MenteeDelegateAdapter(context,this, listener))
        items = mutableListOf()

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

    fun addMentors(news: MutableList<UserListItem>) {
        // first remove loading and notify
        removeLoaderFromBottom()
        val initPosition = items.size - 1
        items.addAll(news)
        notifyItemRangeChanged(initPosition, items.size)
    }

    fun addLoaderAtBottom(){
        items.add(loadingItem)
        notifyItemInserted(items.size-1)
    }
    fun removeLoaderFromBottom() {
        setLoaded()
        if (!items.isEmpty()&& items[items.size-1] is LoadingItem) {
            items.removeAt(items.size-1)
            notifyItemRemoved(items.size)
        }
        else{
            notifyDataSetChanged()
        }
    }

    fun clearAndMentors(news: MutableList<UserListItem>) {
        setLoaded()
        items.clear()
        items.addAll(news)
        notifyDataSetChanged()
    }
    override fun getRows(): List<Any?>? {
        return items
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it }
    }

}