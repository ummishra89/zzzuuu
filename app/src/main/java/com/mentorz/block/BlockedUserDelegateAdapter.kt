package com.mentorz.block

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.blocked_user_list_item.view.*


class BlockedUserDelegateAdapter(adapter: BlockedUserAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun unBlockUser(userListItem: UserListItem)

    }

    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as UserListItem)
    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.blocked_user_list_item)) {

        fun bind(position: Int, item: UserListItem) = with(itemView) {
            txtName.text = item.userProfile?.name
            if (!TextUtils.isEmpty(item.userProfile?.lresId!!)) {
                val url = item.userProfile?.lresId!!
                profileImage.loadThumbnailUrl(context, url)
            }
            unBlock.setOnClickListener {
                viewActions.unBlockUser(item)
            }

        }
    }
}