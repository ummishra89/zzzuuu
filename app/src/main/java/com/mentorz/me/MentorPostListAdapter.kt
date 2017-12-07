package com.mentorz.me


import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.adapter.BaseAdapter
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.match.UserProfile
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.LoadingDelegateAdapter
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.LoadingItem
import com.mentorz.model.ProfileData
import com.mentorz.stories.PostListItem
import org.jetbrains.anko.doAsync


class MentorPostListAdapter(userProfile: UserProfile?, headerlistener: MentorPostHeaderDelegateAdapter.onViewSelectedListener, listener: MentorPostDelegateAdapter.onViewSelectedListener) : BaseAdapter<MentorPostListAdapter, RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var userProfile = userProfile
    private var loadingItem = LoadingItem()

    private val profileData = object : ViewType {
        override fun getViewType() = AdapterConstants.HEADER
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    init {
        delegateAdapters.put(AdapterConstants.HEADER, MentorPostHeaderDelegateAdapter(this, headerlistener))
        delegateAdapters.put(AdapterConstants.ITEMS, MentorPostDelegateAdapter(this, listener))
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        items = ArrayList()
        items.add(profileData)
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

    fun updateProfile() {
        if (userProfile != null) {
            (items[0] as ProfileData).userProfile = userProfile
        } else {
            (items[0] as ProfileData).userProfile?.name = MentorzApplication.instance?.prefs?.getUserName()
            (items[0] as ProfileData).userProfile?.basicInfo = MentorzApplication.instance?.prefs?.getBasicInfo()
            (items[0] as ProfileData).userProfile?.lresId = MentorzApplication.instance?.prefs?.getProfilePictureLres()
        }

        notifyItemChanged(0)
    }

    fun addProfileData(profileData: ProfileData) {
            items[0] = profileData
            notifyItemChanged(0)
    }

    fun addPost(post: List<PostListItem>) {
        // first remove loading and notify
        removeLoaderFromBottom()
        val initPosition = items.size
        val item = items[0]
        // insert news and the loading at the end of the list
        items.clear()
        items.add(item)
        items.addAll(post)
        notifyItemRangeChanged(initPosition, items.size)
    }

    fun removePost(postListItem: PostListItem) {
        val index = items.indexOf(postListItem)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addLoaderAtBottom() {
        items.add(loadingItem)
        notifyItemInserted(items.size - 1)

    }

    fun removeLoaderFromBottom() {
        setLoaded()
        if (!items.isEmpty() && items[items.size - 1] is LoadingItem) {
            items.removeAt(items.size - 1)
            notifyItemRemoved(items.size)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearAndAddPost(postListItem: List<PostListItem>) {
        setLoaded()
        val item = items[0]
        items.clear()
        items.add(item)
        items.addAll(postListItem)
        notifyDataSetChanged()
    }

    fun updateFollowStatus(userId: Long, isFollowing: Boolean) {
        items.forEach {
            if (it is PostListItem && it.userId == userId) {
                it.isFollowing = isFollowing
            }
        }

        notifyDataSetChanged()
    }

    fun updateItem(postListItem: PostListItem) {
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

    override fun getRows(): List<Any?>? {
        return items
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it }
    }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex

}