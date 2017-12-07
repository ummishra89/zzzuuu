package com.mentorz.me

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.expertise.ExpertiseActivity
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.extensions.inflate
import com.mentorz.extensions.loadThumbnailUrl
import com.mentorz.match.UserListItem
import com.mentorz.match.adapter.ViewType
import com.mentorz.match.adapter.ViewTypeDelegateAdapter
import com.mentorz.model.ProfileData
import com.mentorz.model.UserType
import com.mentorz.review.ReviewActivity
import com.mentorz.userdetails.UserDetailActivity
import com.mentorz.utils.DialogUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_header.view.*


class MentorPostHeaderDelegateAdapter(adapter: MentorPostListAdapter, val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {

    interface onViewSelectedListener {
        fun onClickRateNow(userId: Long, userName: String)
        fun onClickBlock(userId: Long, userName: String)
        fun onClickSendRequest(profileData: ProfileData, comment: String, position: Int)
        fun onClickFollow(profileData: ProfileData)
        fun onClickUnFollow(profileData: ProfileData)
    }

    var adapter = adapter

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(position, item)

    }

    inner class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.profile_header)) {

        fun bind(position: Int, item: Any) = with(itemView) {
            if (item is ProfileData) {
                val profileData: ProfileData = item
                name.text = profileData.userProfile?.name
                txtMentor.text = item.userProfile?.getMentorText(context)
                txtMentee.text = item.userProfile?.getMenteeText(context)
                txtFollowers.text = item.userProfile?.getFollowerText(context)
                txtFollowing.text = item.userProfile?.getFollowingText(context)

                txtMentorsCount.text = profileData.userProfile?.mentors.toString()
                txtFollowersCount.text = profileData.userProfile?.followers.toString()
                txtFollowingCount.text = profileData.userProfile?.following.toString()
                txtMenteeCount.text = profileData.userProfile?.mentees.toString()
                headline.text = profileData.userProfile?.basicInfo
                rating.rating = profileData.userProfile?.rating?.rating ?: 0.0f

                if (item.userId == MentorzApplication.instance?.prefs?.getUserId()) {
                    popup_menu.visibility = View.GONE

                } else {
                    popup_menu.visibility = View.VISIBLE

                }

                if (item.isFollowing != null && item.userId != MentorzApplication.instance?.prefs?.getUserId()) {
                    followFollowing.visibility = View.VISIBLE
                    if (item.isFollowing!!) {
                        followFollowing.isSelected = true
                        followFollowing.text = context.getString(R.string.following)
                    } else {
                        followFollowing.isSelected = false
                        followFollowing.text = context.getString(R.string.follow)
                    }
                }

                followFollowing.setOnClickListener {
                    if (!item.isFollowing!!) {
                        viewActions.onClickFollow(item)
                    }else{
                        viewActions.onClickUnFollow(item)
                    }
                }
                if (item.userId != MentorzApplication.instance?.prefs?.getUserId()) {
                    when (item.request) {
                        UserListItem.Request.SEND_REQUEST -> {
                            lSendRequest.visibility = View.VISIBLE
                            sendRequest.text = context.getString(R.string.send_mentor_request)
                            imgMatch.setBackgroundResource(R.drawable.match_add_selected)
                            btnSendRequest.isSelected = false
                        }
                        UserListItem.Request.REQUEST_SENT -> {
                            lSendRequest.visibility = View.VISIBLE
                            sendRequest.text = context.getString(R.string.pending)
                            imgMatch.setBackgroundResource(R.drawable.no_request)
                            btnSendRequest.isSelected = false

                        }
                        UserListItem.Request.ALREADY_YOUR_MENTOR -> {
                            lSendRequest.visibility = View.VISIBLE
                            btnSendRequest.isSelected = true
                            lSendRequest.isSelected = true
                            btnSendRequest.setBackgroundResource(R.color.colorAccent)
                            sendRequest.text = context.getString(R.string.already_your_mentor)
                            imgMatch.setBackgroundResource(R.drawable.my_mentor_white)
                        }
                        else -> {
                            lSendRequest.visibility = View.GONE
                        }
                    }
                }
                if (!item.userProfile?.videoBioHres.isNullOrEmpty()) {
                    imgVideo.visibility = View.VISIBLE
                }
                imgVideo.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(item.userProfile?.videoBioHres), "video/mp4")
                    context.startActivity(intent)
                }
                lSendRequest.setOnClickListener {
                    when (item.request) {
                        UserListItem.Request.SEND_REQUEST -> {
                            if (layoutSendRequest.visibility == View.VISIBLE) {
                                layoutSendRequest.visibility = View.GONE
                            } else {
                                layoutSendRequest.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                btnSendRequest.setOnClickListener {
                    if (item.request == UserListItem.Request.SEND_REQUEST) {
                        if (edtAbout.text.isEmpty()) {
                            return@setOnClickListener
                        }
                        layoutSendRequest.visibility = View.GONE
                        viewActions.onClickSendRequest(item, edtAbout.text.toString(), position)
                    }
                }

                if (item.userId == MentorzApplication.instance?.prefs?.getUserId()) {

                    imgProfile.loadThumbnailUrl(context, MentorzApplication.instance?.prefs?.getProfilePictureLres())
                    if (!TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getProfilePictureLres())) {
                        Picasso.with(context).load(MentorzApplication.instance?.prefs?.getProfilePictureLres()).resize(800, 500).centerCrop().into(imgBackground)
                    }

                } else if (!TextUtils.isEmpty(item.userProfile?.lresId)) {
                    imgProfile.loadThumbnailUrl(context, item.userProfile?.lresId)
                    Picasso.with(context).load(item.userProfile?.lresId).resize(800, 500).centerCrop().into(imgBackground)

                }
                imgProfile.setOnClickListener {
                    if (!TextUtils.isEmpty(item.userProfile?.lresId))
                        DialogUtils.showImagePreview(context, item.userProfile?.lresId).show()
                }

                if (profileData.expertises != null) {
                    if (profileData.expertises!!.count() > 1) {
                        val text = profileData.expertises!![0].expertise + "  + " + (profileData.expertises!!.count() - 1).toString() + " more"
                        val content = SpannableString(text)
                        content.setSpan(UnderlineSpan(), content.length - ((profileData.expertises!!.count() - 1).toString() + " more").length, content.length, 0)
                        txtExpertise.text = content
//                        txtExpertise.text = profileData.expertises!![0].expertise + "  +" + (profileData.expertises!!.count() - 1) + " more"

                    } else if (profileData.expertises!!.isEmpty()) {
                        txtExpertise.text = ""
                    } else {
                        txtExpertise.text = profileData.expertises!![0].expertise
                    }

                }
                if (item.expertises == null || item.expertises?.isEmpty()!!){
                    lSendRequest.visibility = View.GONE
                }
                rating.isSelected = true
                txtExpertise.setOnClickListener {
                    val intent = Intent(context, ExpertiseActivity::class.java)
                    val arrayList: ArrayList<ExpertiseItem> = arrayListOf()
                    arrayList.addAll(profileData.expertises!!)
                    if (profileData.userId == MentorzApplication.instance?.prefs?.getUserId()) {
                        intent.putExtra("user_type", UserType.SELF)
                    } else {
                        intent.putExtra("user_type", UserType.FRIEND)
                    }
                    intent.putParcelableArrayListExtra("mentor_expertise", arrayList)
                    context.startActivity(intent)
                }

                review.setOnClickListener {
                    val intent = Intent(context, ReviewActivity::class.java)
                    intent.putExtra("user_id", item.userId)
                    context.startActivity(intent)
                }
                rFollowers.setOnClickListener {
                    val intent = Intent(context, UserDetailActivity::class.java)
                    intent.putExtra("user_type", UserType.FOLLOWERS)
                    intent.putExtra("friend_id", item.userId)
                    context.startActivity(intent)


                }
                rFollowing.setOnClickListener {
                    val intent = Intent(context, UserDetailActivity::class.java)
                    intent.putExtra("user_type", UserType.FOLLOWING)
                    intent.putExtra("friend_id", item.userId)
                    context.startActivity(intent)
                }
                rMentors.setOnClickListener {
                    val intent = Intent(context, UserDetailActivity::class.java)
                    intent.putExtra("user_type", UserType.MENTOR)
                    intent.putExtra("friend_id", item.userId)
                    context.startActivity(intent)
                }
                rMentee.setOnClickListener {
                    val intent = Intent(context, UserDetailActivity::class.java)
                    intent.putExtra("user_type", UserType.MENTEE)
                    intent.putExtra("friend_id", item.userId)
                    context.startActivity(intent)
                }
                popup_menu.setOnClickListener {
                    val popup = PopupMenu(context, popup_menu)
                    //Inflating the Popup using xml file
                    popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.rateNow -> {
                                try{
                                viewActions.onClickRateNow(profileData.userId!!, profileData.userProfile?.name!!)
                            }catch (e:Exception)
                        {
                            Log.e("MentorPostHeader",""+e.toString())
                        }
                            }
                            R.id.block -> {
                                try{
                                    viewActions.onClickBlock(profileData.userId!!, profileData.userProfile?.name!!)
                                    }catch (e:Exception)
                                {
                                    Log.e("MentorPostHeader",""+e.toString())
                                }

                            }
                        }
                        true
                    }
                    popup.show()
                }
            }
        }
    }



}