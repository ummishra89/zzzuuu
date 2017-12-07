package com.mentorz.adapter


import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.LoadingDelegateAdapter
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.CommentListItem
import com.mentorz.model.LoadingItem

class CommentListAdapter(listener: CommentDelegateAdapter.onViewSelectedListener) : BaseAdapter<CommentListAdapter, RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var loadingItem: LoadingItem = LoadingItem()


    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, CommentDelegateAdapter(this, listener))
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

    fun addComment(comment: List<CommentListItem>) {
        removeLoaderFromBottom()
        val initPosition = items.size - 1
        items.addAll(comment)
        notifyItemRangeInserted(initPosition, items.size)

    }

    fun addComment(comment: CommentListItem) {
        items.add(comment)
        notifyItemInserted(items.size-1)
    }

    fun removeCommentsByUserID(userId: Long?){
        items = items.filter { (it as CommentListItem).userId != userId } as ArrayList<ViewType>
        notifyDataSetChanged()
    }

    fun removeComment(commentListItem: CommentListItem) {
        val index = items.indexOf(commentListItem)
        items.removeAt(index)
        notifyItemRemoved(index)
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