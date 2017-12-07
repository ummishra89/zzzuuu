package com.mentorz.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mentorz.listener.OnLoadMoreListener

/**
 * Created by umesh on 26/08/17.
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var isLoading: Boolean = false
    var visibleThreshold = 1
    var lastVisibleItem = 0
    var totalItemCount = 0
    private var onLoadMoreListener: OnLoadMoreListener? = null
    fun setOnLoadMoreListener(recyclerView: RecyclerView,mOnLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener
         addScrollListener(recyclerView)
    }
    private fun addScrollListener(recyclerView:RecyclerView){
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
    }
    open fun getRows():List<Any?>?{
        return null
    }
    fun registerAdapterDataObserver(emptyView:View){
        registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setEmptyView(emptyView)
            }
            override fun onChanged() {
                super.onChanged()
                setEmptyView(emptyView)

            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                setEmptyView(emptyView)
            }

        })
    }
    fun setEmptyView(emptyView: View){
        if(!getRows()!!.isEmpty()){
            emptyView.visibility= View.GONE
        }
        else{
            emptyView.visibility= View.VISIBLE
        }
    }
    fun setLoaded() {
        isLoading = false
    }

}