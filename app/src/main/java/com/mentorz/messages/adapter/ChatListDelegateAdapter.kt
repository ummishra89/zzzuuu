package com.mentorz.messages.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.pubnub.StreamChatPacket
import kotlinx.android.synthetic.main.chat_user_list_item.view.*



class ChatListDelegateAdapter(context: Context, adapter: ChatListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {

    }
    var adapter = adapter
    var context =context

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as StreamChatPacket)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.chat_view_item)) {

        var item: UserListItem? = null

        fun bind(position: Int, item: StreamChatPacket) = with(itemView) {
            txtName.text=item.body
        }
    }
}