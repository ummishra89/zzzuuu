package com.mentorz.messages

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.pending_request_list_item.view.*


class PendingRequestDelegateAdapter(adapter: PendingRequestAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onCancelRequest(requestListItem: RequestListItem)
        fun onRejectRequest(requestListItem: RequestListItem)
        fun onAcceptRequest(requestListItem: RequestListItem)

    }

    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as RequestListItem)
    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.pending_request_list_item)) {

        fun bind(position: Int, item: RequestListItem) = with(itemView) {
            txtName.text = item.userProfile?.name
            profileImage.loadThumbnailUrl(context, item.userProfile?.lresId)
            if(item.isMentorRequest!!){
                imgAcceptRequest.visibility= View.VISIBLE
                imgDelete.isSelected=true
            }
            else{
                imgAcceptRequest.visibility= View.GONE
                imgDelete.isSelected=false
            }
            imgAcceptRequest.setOnClickListener {
                viewActions.onAcceptRequest(item)
            }
            imgDelete.setOnClickListener {
                if(item.isMentorRequest!!){
                    viewActions.onRejectRequest(item)
                }
                else{
                    viewActions.onCancelRequest(item)
                }

            }
        }
    }
}