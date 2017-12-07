package com.mentorz.messages.adapter

import android.content.Context
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.LoadingDelegateAdapter
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.LoadingItem


class MyMentorMenteeListAdapter(chatView:View, emptyView:View, context: Context, recyclerView: RecyclerView, val listener: MyMentorMenteeDelegateAdapter.onViewSelectedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onLoadMoreListener: OnLoadMoreListener? = null
    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener
    }
    private var items: MutableList<ViewType>
    private var searchItems: MutableList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var isLoading: Boolean = false
    var context =context
    private var loadingItem = LoadingItem()
    private var visibleThreshold = 5
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, MyMentorMenteeDelegateAdapter(context,this, listener))
        items = mutableListOf()
        searchItems = mutableListOf()
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading && dy > 0) {
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener!!.onLoadMore()
                        }
                        isLoading = true
                    }
                }
            }
        })
        registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
                if(!getItems().isEmpty()){
                    emptyView.visibility= View.GONE
                    chatView.visibility =View.VISIBLE
                }
                else{
                    chatView.visibility =View.GONE
                    emptyView.visibility= View.VISIBLE
                }
            }
        })
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

    fun setLoaded() {
        isLoading = false
    }
    override fun getItemViewType(position: Int): Int {
        return this.items[position].getViewType()
    }

    fun addMentors(news: MutableList<UserListItem>) {
        // first remove loading and notify
        removeLoaderFromBottom()
        val initPosition = items.size - 1
        items.addAll(news)
        searchItems.addAll(news)
        notifyItemRangeChanged(initPosition, items.size   /* plus loading item */)
    }

    fun refreshData(list: MutableList<UserListItem>) {
        items.clear()
        searchItems.clear()
        items.addAll(list)
        searchItems.addAll(list)
        notifyDataSetChanged()
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

    fun clearAndMentors(news: MutableList<UserListItem>) {
        setLoaded()
        items.clear()
        searchItems.clear()
        items.addAll(news)
        searchItems.addAll(news)
        notifyDataSetChanged()
    }
    fun getItems(): List<UserListItem> {
        return items
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it as UserListItem }
    }

    fun  removeUserChatByUserId(userId: Long) {
        items = items.filter { (it as UserListItem).userId != userId } as MutableList<ViewType>
        searchItems = searchItems.filter { (it as UserListItem).userId != userId } as MutableList<ViewType>
        notifyDataSetChanged()
    }

    fun  searchPeople(newText: String) {
        if (TextUtils.isEmpty(newText.trim())){
            items.clear()
            items.addAll(searchItems)
            notifyDataSetChanged()
        }else{
            items.clear()
            items.addAll(searchItems.filter { ((it as UserListItem).userProfile?.name as String).contains(newText.trim(),true)  } as MutableList<ViewType>)
            if (items.isEmpty()){
                listener.noSearchFound()
            }else{
                listener.searchFound()
            }
            notifyDataSetChanged()
        }
    }
}