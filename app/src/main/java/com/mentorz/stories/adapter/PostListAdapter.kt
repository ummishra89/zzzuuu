package com.mentorz.stories.adapter


import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.mentorz.adapter.BaseAdapter
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.LoadingDelegateAdapter
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.LoadingItem
import com.mentorz.model.UploadItem
import com.mentorz.stories.PostListItem


class PostListAdapter(postList: MutableList<ViewType>, listener: PostDelegateAdapter.onViewSelectedListener, progressListener: UploadingProgressAdapter.onViewSelectedListener?) : Filterable, BaseAdapter<PostListAdapter, RecyclerView.ViewHolder>() {


    private var items: MutableList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var filteredItems: MutableList<ViewType>

    fun getList(): MutableList<ViewType> {
        return items
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private var mFilter = CustomFilter()
    private var loadingItem: LoadingItem = LoadingItem()

    init {
        loadingItem = LoadingItem()
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, PostDelegateAdapter(this, listener))
        delegateAdapters.put(AdapterConstants.PROGRESS, UploadingProgressAdapter(this, progressListener))
        items = postList
        filteredItems = mutableListOf()
        filteredItems.addAll(items)

    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(position, holder, this.filteredItems[position])

    }


    fun isLoadingInProgress(): Boolean {
        return isLoading
    }

    override fun getItemViewType(position: Int): Int {
        return this.filteredItems[position].getViewType()
    }

    fun addPost(post: List<PostListItem>) {
        removeLoaderFromBottom()
        val initPosition = filteredItems.size - 1
        filteredItems.clear()
        filteredItems.addAll(post)
        notifyItemRangeChanged(initPosition, filteredItems.size)
        if (!filterString.isEmpty()) {
            filter.filter(filterString)
        }
    }

    fun addLoaderAtBottom() {
        filteredItems.add(loadingItem)
        notifyItemInserted(filteredItems.size - 1)
    }

    fun removeLoaderFromBottom() {
        setLoaded()
        if (!filteredItems.isEmpty()) {
            filteredItems.removeAt(filteredItems.size - 1)
            notifyItemRemoved(filteredItems.size)
        } else {
            notifyDataSetChanged()
        }
    }

    fun updateUploadFileProgress(progress: Int) {
        if (items.isNotEmpty() && items[0] !is UploadItem) {
            val uploadItem = UploadItem()
            filteredItems.add(0, uploadItem)
            items.add(0, uploadItem)
            notifyItemInserted(0)
        } else {
            val uploadItem = items[0] as UploadItem
            uploadItem.progress = progress
            notifyItemChanged(0)
        }
    }
    fun updateCompleteFileUploadFileProgress(progress: Int) {
        if (items.isNotEmpty() && items[0] !is UploadItem) {
            val uploadItem = UploadItem()
            filteredItems.add(0, uploadItem)
            items.add(0, uploadItem)
            notifyItemInserted(0)
        } else {
            val uploadItem = items[0] as UploadItem
            uploadItem.progress = progress
            notifyItemChanged(0)
        }
    }

    fun isAlreadyUploading(): Boolean {
        if (items.isNotEmpty() && items[0] is UploadItem) {
            return true
        }
        if (filteredItems.isNotEmpty() && filteredItems[0] is UploadItem) {
            return true
        }
        return false
    }

    fun removePost(postListItem: PostListItem) {

        for (index in items.indices) {
            val item = items[index]
            if (item is PostListItem && item == postListItem) {
                items.removeAt(index)
                break
            }
        }
        for (index in filteredItems.indices) {
            val item = filteredItems[index]
            if (item is PostListItem && item == postListItem) {
                filteredItems.removeAt(index)
                notifyItemRemoved(index)
                break
            }
        }

    }

    fun removeProgress() {
        if (!items.isEmpty()) {
            items.removeAt(0)
        }
        if (!filteredItems.isEmpty()) {
            filteredItems.removeAt(0)
        }
        notifyItemRemoved(0)
    }

    fun updateItem(postListItem: PostListItem) {
        filteredItems.forEach {
            if (it is PostListItem && it.postId == postListItem.postId) {
                it.viewCount = postListItem.viewCount
                it.likeCount = postListItem.likeCount
                it.shareCount = postListItem.shareCount
                it.commentCount = postListItem.commentCount
                return@forEach
            }
        }
        items.forEach {
            if (it is PostListItem && it.postId == postListItem.postId) {
                it.viewCount = postListItem.viewCount
                it.likeCount = postListItem.likeCount
                it.shareCount = postListItem.shareCount
                it.commentCount = postListItem.commentCount
                it.liked = postListItem.liked
                return@forEach
            }
        }
        notifyDataSetChanged()
    }

    fun updateFollowStatus(userId: Long, isFollowing: Boolean) {
        filteredItems.forEach {
            if (it is PostListItem && it.userId == userId) {
                it.isFollowing = isFollowing
            }
        }
        items.forEach {
            if (it is PostListItem && it.userId == userId) {
                it.isFollowing = isFollowing
            }
        }
        notifyDataSetChanged()
    }

    fun clearAndAddPost(postListItem: List<PostListItem>) {
        if (isAlreadyUploading()) {
            val uploadingItem = filteredItems[0]
            filteredItems.clear()
            items[0] = uploadingItem
        } else {
            filteredItems.clear()
        }
        filteredItems.addAll(postListItem)
        if (!filterString.isEmpty()) {
            filter.filter(filterString)
        } else {
            notifyDataSetChanged()
        }
    }

    var filterString = ""

    inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            filterString = constraint.toString()
            val results = Filter.FilterResults()
            // Create new Filter Results and return this to publishResults;
            setLoaded()
            filteredItems.clear()

            if (constraint.isNullOrEmpty()) {
                filteredItems.addAll(items)
            } else {
                val tempList = items.filter {
                    it is PostListItem
                }
                val filteredList = tempList.filter {
                    val item = (it as PostListItem)
                    item.name?.contains(constraint!!, true)!! || item.content?.description?.contains(constraint!!, true)!!
                }
                if (!filteredList.isEmpty()) {
                    if (isAlreadyUploading()) {
                        filteredItems.add(0, items[0])
                    }
                    filteredItems.addAll(filteredList.toMutableList())
                }

            }
            results.values = filteredItems
            results.count = filteredItems.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            notifyDataSetChanged()
        }
    }

    override fun getRows(): List<Any?>? {
        return filteredItems
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it }
    }

    fun removePostByUserId(userId: Long?) {
        filteredItems = filteredItems.filter {
            (it as PostListItem).userId != userId
        } as MutableList<ViewType>
        items = items.filter {
            (it as PostListItem).userId != userId
        } as MutableList<ViewType>
        notifyDataSetChanged()

        /*for(index in filteredItems.indices){
            var item =  filteredItems[index]
            if(item is PostListItem && item.userId==userId){
                filteredItems.removeAt(index)
                notifyItemRemoved(index)
            }
        }

        for(index in items.indices){
            var item =  filteredItems[index]
            if(item is PostListItem && item.userId==userId){
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }*/

    }
}