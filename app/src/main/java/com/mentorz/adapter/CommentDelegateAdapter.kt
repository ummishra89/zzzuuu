package com.mentorz.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.CommentListItem
import com.mentorz.utils.DateUtils
import kotlinx.android.synthetic.main.comment_list_item.view.*

class CommentDelegateAdapter(adapter: CommentListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onDeleteComment(commentListItem: CommentListItem)


    }

    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as CommentListItem)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.comment_list_item)) {

        fun bind(position: Int, item: CommentListItem) = with(itemView) {
            txtComment.text = item.name
            //Picasso.with(itemView.context).load(item.thumbnail).into(img_thumbnail)
            // img_thumbnail.loadImg(item.thumbnail)
            txtComment.text = item.comment
            txtName.text = item.name
            txtTimeStamp.text = DateUtils.getDurationFromDate(item.commentTime!!)
            //txtTimeStamp.text=item.commentTime.toString()
            if (item.userId == MentorzApplication.instance?.prefs?.getUserId()) {
                imgDelete.visibility = View.VISIBLE
                txtName.text = MentorzApplication.instance?.prefs?.getUserName()
                img_thumbnail.loadThumbnailUrl(context, MentorzApplication.instance?.prefs?.getProfilePictureLres())
            } else {
                img_thumbnail.loadThumbnailUrl(context, item.lresId)
                imgDelete.visibility = View.GONE
            }
            imgDelete.setOnClickListener {
                viewActions.onDeleteComment(item)
            }

        }
    }
}