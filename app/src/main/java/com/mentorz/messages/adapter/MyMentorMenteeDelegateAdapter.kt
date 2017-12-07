package com.mentorz.messages.adapter

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.Rating
import com.mentorz.sinchvideo.ChatActivity
import kotlinx.android.synthetic.main.chat_user_list_item.view.*

class MyMentorMenteeDelegateAdapter(context: Context, adapter: MyMentorMenteeListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun noSearchFound()
        fun searchFound()

    }
    var adapter = adapter
    var context =context

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item as UserListItem)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.chat_user_list_item)) {

        var item: UserListItem? = null

        fun bind(position: Int, item: UserListItem) = with(itemView) {
            txtName.text=item.userProfile?.name
            txtBasicInfo.text= item.userProfile?.basicInfo
            if(item.userProfile?.chatUnreadCount==0 ){
                tv_chat_count.visibility = View.GONE
            }else{
                tv_chat_count.visibility = View.VISIBLE
            tv_chat_count.text = ""+item.userProfile?.chatUnreadCount}
            img_thumbnail.loadThumbnailUrl(context,item.userProfile?.lresId)
            mainContent.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("user_id", item.userId)
                intent.putExtra("user_name", item.userProfile?.name)
                intent.putExtra("user_lres", item.userProfile?.lresId)
                val rating = Rating()
                rating.rating = item.rating
                item.userProfile?.rating = rating
                Log.d("adapter", "rating:" + item.userProfile?.rating?.rating)
                intent.putExtra("profile_data", item.userProfile)
                context.startActivity(intent)
            }
        }
    }
}