package com.mentorz.match.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.UserListItem
import kotlinx.android.synthetic.main.mentors_list_item.view.*

class MentorsDelegateAdapter(context: Context, adapter: MentorListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onItemSelected(url: String?)
        fun onExpertiseClick(list: MutableList<ExpertiseItem>)
        fun onSendRequestClick(message: String, item: UserListItem, position: Int)
        fun requestSent()
        fun alreadyYourMentor()
        fun onProfileClick(item: UserListItem)
    }

    var adapter = adapter
    var expandedPosition = -1
    var context=context

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as UserListItem)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.mentors_list_item)) {

        fun bind(position: Int, item: UserListItem) = with(itemView) {
            //Picasso.with(itemView.context).load(item.thumbnail).into(img_thumbnail)
            // img_thumbnail.loadImg(item.thumbnail)
            name.text = item.userProfile?.name
            basic_info.text = item.userProfile?.basicInfo
            rating.rating = item.rating
            txtFollowers.text = if (item.userProfile?.followers!!.toInt() > 1) {
                item.userProfile?.followers.toString() + " " + txtFollowers.context.getString(R.string.followers)
            } else {
                item.userProfile?.followers.toString() + " " + txtFollowers.context.getString(R.string.follower)

            }
            if (item.userProfile!!.lresId != null) {
                img_thumbnail.loadThumbnailUrl(context, item.userProfile!!.lresId)
            }
            txtExpertise.text = item.getExpertiseInString()
            txtExperience.text = item.userProfile?.experience!!.toString() + " " + context.getString(R.string.yr_of_experience)
            sendRequest.setOnClickListener {
                when (item.request) {
                    UserListItem.Request.SEND_REQUEST -> {
                        viewActions.onSendRequestClick(edtAbout.text.toString().trim(), item, position)
                    }
                    UserListItem.Request.REQUEST_SENT -> {
                        viewActions.requestSent()
                    }
                    UserListItem.Request.ALREADY_YOUR_MENTOR -> {
                        viewActions.alreadyYourMentor()
                    }
                }
            }
            when (item.request) {
                UserListItem.Request.SEND_REQUEST -> {

                    edtAbout.visibility = View.VISIBLE
                    sendRequest.text = context.getString(R.string.send_mentor_request)
                    imgMatchAdd.setBackgroundResource(R.drawable.match_add_selected)
                    sendRequest.isSelected=true
                }
                UserListItem.Request.REQUEST_SENT -> {
                    edtAbout.visibility = View.GONE
                    sendRequest.text = context.getString(R.string.pending)
                    imgMatchAdd.setBackgroundResource(R.drawable.no_request)
                    sendRequest.isSelected=true

                }
                UserListItem.Request.ALREADY_YOUR_MENTOR -> {
                    edtAbout.visibility = View.GONE
                    sendRequest.setBackgroundResource(R.color.colorAccent)
                    sendRequest.text = context.getString(R.string.already_your_mentor)
                    imgMatchAdd.setBackgroundResource(R.drawable.selected_match)
                    sendRequest.isSelected=false

                }
            }
            if (position == expandedPosition) {
                layoutBottom.visibility = View.VISIBLE
            } else {
                layoutBottom.visibility = View.GONE
            }
            imgMatchAdd.setOnClickListener {
                if (position != expandedPosition) {
                    expandedPosition = position
                    layoutBottom.visibility = View.VISIBLE
                } else {
                    expandedPosition = -1
                    layoutBottom.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()

            }
            txtExpertise.setOnClickListener {
                viewActions.onExpertiseClick(item.expertises!!)
            }
            img_thumbnail.setOnClickListener {
                viewActions.onProfileClick(item)
            }
            super.itemView.setOnClickListener {
                viewActions.onProfileClick(item)
            }
        }
    }
}