package com.mentorz.fragments.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.CommentActivity
import com.mentorz.activities.HomeActivity
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.activities.PostActivity
import com.mentorz.adapter.NotificationListAdapter
import com.mentorz.constants.PushType
import com.mentorz.database.DbManager
import com.mentorz.fragments.BaseFragment
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.model.Notification
import com.mentorz.model.ProfileImage
import com.mentorz.model.ProfileImageResponse
import com.mentorz.requester.ProfileImageRequester
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.retrofit.listeners.ProfileImageListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.fragment_notification.*
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * Created by craterzone on 06/09/17.
 */
class NotificationFragment : BaseFragment(), NotificationListAdapter.NotificationListener, ProfileImageListener, SignedUrlListener, DbManager.NotificationDataChangeListener {
    override fun onNotificationDataChange() {
        doAsync {
            val items = dbManager.getNotifications()
            notificationItems = items
            Collections.reverse(notificationItems)
            activity.runOnUiThread {
                notificationListAdapter?.setList(notificationItems)
                notificationListAdapter?.notifyDataSetChanged()
            }
            notificationItems!!.forEach {
                if (it.pushType.equals(PushType.ACCEPT_REQUEST) || it.pushType.equals(PushType.SEND_REQUEST) || it.pushType.equals(PushType.HAS_FOLLOWING)) {
                    loadProfile(it.userId)
                }
            }

            checkList()
            activity.runOnUiThread{
                notificationListAdapter?.notifyDataSetChanged()
            }
        }
    }


    override fun signedUrlSuccess(url: String, model: Any?) {
        notificationItems!!.forEach {
            if (it.pushType.equals(PushType.ACCEPT_REQUEST) || it.pushType.equals(PushType.SEND_REQUEST) || it.pushType.equals(PushType.HAS_FOLLOWING)) {
                if ((model as ProfileImage).userId == it.userId)
                    it.profilePicture = url
            }
        }
        activity.runOnUiThread {
            notificationListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onNetworkFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun profileImageSuccess(profileImageResponse: ProfileImageResponse, userId: Long?) {
        val model = ProfileImage()
        model.userId = userId
        SignedUrlRequester(FileType.FILE, this, model, profileImageResponse.hresId).execute()
    }

    override fun profileImageFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
        var clearNotificationCountListener: ClearNotificationCountListener? = null

        fun registerClearNotificationCountListener(listener: ClearNotificationCountListener) {
            clearNotificationCountListener = listener
        }
    }

    interface ClearNotificationCountListener {
        fun clearNotificationCount()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
    }

    override fun onResume() {
        super.onResume()
     /*   doAsync {
            dbManager.setAllViewedNotification()
        }
        if (clearNotificationCountListener != null){
//            MentorzApplication.instance?.prefs?.setUnseenNotificationCount(0)
            clearNotificationCountListener!!.clearNotificationCount()
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val intentFilter = IntentFilter()
        DbManager.registerNotificationDataChangeListener(this)
        intentFilter.addAction(Constant.ACTION_BLOCK)
        activity?.registerReceiver(receiver, intentFilter)
    }

    fun deleteNotificationFormDb(userId: Long?){
        dbManager.deleteNotification(userId)
    }
    var receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.action) {
                Constant.ACTION_BLOCK -> {
                    notificationListAdapter?.removeNotificationByUserId(intent.getLongExtra("user_id", 0))
                    deleteNotificationFormDb(intent.getLongExtra("user_id", 0))
                }
            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_notification, container, false)
    }

    var dbManager = DbManager.getInstance(MentorzApplication.applicationContext())
    var notificationItems: MutableList<Notification>? = null
    var notificationListAdapter: NotificationListAdapter? = NotificationListAdapter(notificationItems, this)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        notificationList.setHasFixedSize(true)
        val linearLayout = WrapContentLinearLayoutManager(context)
        notificationList.layoutManager = linearLayout
        notificationList.adapter = notificationListAdapter
        doAsync {
            val items = dbManager.getNotifications()
            if (items.size != notificationItems?.size) {
                notificationItems = items
                Collections.reverse(notificationItems)
                activity.runOnUiThread {
                    notificationListAdapter?.setList(notificationItems)
                    notificationListAdapter?.notifyDataSetChanged()
                }
            }
            notificationItems!!.forEach {
                if (it.pushType.equals(PushType.ACCEPT_REQUEST) || it.pushType.equals(PushType.SEND_REQUEST) || it.pushType.equals(PushType.HAS_FOLLOWING)) {
                    loadProfile(it.userId)
                }
            }

            checkList()
        }
        notificationListAdapter?.notifyDataSetChanged()

    }


    private fun loadProfile(userId: Long?) {
        ProfileImageRequester(userId, this).execute()
    }

    private fun checkList() {
        if (notificationItems == null || notificationItems?.isEmpty()!!) {
            activity.runOnUiThread {
                if (emptyMessage != null)
                    emptyMessage.visibility = View.VISIBLE
            }
        } else {
            activity.runOnUiThread {
                if (emptyMessage != null)
                    emptyMessage.visibility = View.GONE
            }
        }
    }

    override fun onNotificationClick(pushType: String?, userId: Long?, postId: Long?, userName: String?, adapterPosition: Int) {
        when (pushType) {
            PushType.SEND_REQUEST -> {
                val intent = Intent(context, HomeActivity::class.java)
                intent.action = "OPEN_TAB_REQUEST";
                startActivity(intent)
            }
            PushType.HAS_FOLLOWING -> {
                val intent = Intent(context, MentorProfileActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("push_type",pushType)
                startActivity(intent)
            }
            PushType.ACCEPT_REQUEST -> {
                val intent = Intent(context, MentorProfileActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("push_type",pushType)
                startActivity(intent)
            }
            PushType.LIKED_POST -> {
                val intent = Intent(context, PostActivity::class.java)
                intent.putExtra("push_type",pushType)
                intent.putExtra("post_id", postId)
                intent.putExtra("user_id", userId)
                startActivity(intent)

            }
            PushType.COMMENTED_ON_POST -> {
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("push_type",pushType)
                intent.putExtra("post_id", postId)
                intent.putExtra("user_name", userName)
                intent.putExtra("user_id", userId)
                startActivity(intent)
            }
            PushType.SHARE -> {
                val intent = Intent(context, PostActivity::class.java)
                intent.putExtra("push_type",pushType)
                intent.putExtra("post_id", postId)
                intent.putExtra("user_id", userId)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(receiver)
    }
}