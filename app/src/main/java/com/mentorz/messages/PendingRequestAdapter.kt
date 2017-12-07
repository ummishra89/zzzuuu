package com.mentorz.messages

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mentorz.adapter.BaseAdapter
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.LoadingDelegateAdapter
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.CommentListItem
import com.mentorz.model.LoadingItem


class PendingRequestAdapter( listener: PendingRequestDelegateAdapter.onViewSelectedListener) : BaseAdapter<PendingRequestAdapter, RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var loadingItem = LoadingItem()


    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, PendingRequestDelegateAdapter(this, listener))
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

    fun addRequest(requestItem: List<RequestListItem>) {
        // first remove loading and notify
        removeLoaderFromBottom()
        val initPosition = items.size - 1
        items.addAll(requestItem)
        notifyItemRangeChanged(initPosition, items.size   /* plus loading item */)

    }
    fun clearAndAddRequest(item: List<RequestListItem>) {
        setLoaded()
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }
    fun removeRequest(requestItem: RequestListItem) {
        val index = items.indexOf(requestItem)
        items.removeAt(index)
        notifyItemRemoved(index)
    }
    fun removeOnBasisOfUserId(userId: Long?) {

      //  items = items.filter { (it as CommentListItem).userId != userId } as ArrayList<ViewType>
      //  notifyDataSetChanged()
      /*  val index = items.indexOf(requestItem)
        items.removeAt(index)
        notifyItemRemoved(index)*/
    }
    fun addLoaderAtBottom(){
        items.add(loadingItem)
        notifyItemInserted(items.size-1)
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