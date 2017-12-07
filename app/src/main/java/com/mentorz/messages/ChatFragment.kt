package com.mentorz.messages


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import com.facebook.share.model.AppInviteContent
import com.facebook.share.widget.AppInviteDialog
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.controller.Controller
import com.mentorz.database.DbManager
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.UserListItem
import com.mentorz.messages.adapter.MyMentorMenteeDelegateAdapter
import com.mentorz.messages.adapter.MyMentorMenteeListAdapter
import com.mentorz.pubnub.PubNubManagerService
import com.mentorz.userdetails.UserDetailsPresenterImpl
import com.mentorz.userdetails.UserDetailsView
import com.mentorz.utils.Constant
import com.mentorz.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.main.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : BaseFragment(),UserDetailsView, View.OnClickListener, MyMentorMenteeDelegateAdapter.onViewSelectedListener {
    override fun searchFound() {
        noSearchFound.visibility = View.GONE
    }

    override fun noSearchFound() {
        noSearchFound.visibility = View.VISIBLE
    }


    override fun networkError() {
        super.networkError()
    }

    override fun updateAdapter() {
        activity?.runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun dataSetChanged() {
        activity?.runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            txtFindMentor.id -> {
                if (activity is HomeTabClickListener) {
                    val listener = activity as HomeTabClickListener
                    listener.onClickMatch(null)
                }
            }
                layoutInviteFriends.id->{
                    val appLinkUrl: String = "https://fb.me/1894910480738739"
                    val previewImageUrl: String = "https://storage.googleapis.com/mentorz_content/Logo_FB.png?GoogleAccessId=276892747020-compute@developer.gserviceaccount.com&Expires=1546108200000&Signature=QpWP46k4iWiRY70b1SyRfbJrWdk2DM%2Fz%2BiGw1jaTX6SWfU2SQfYfoDVWSosjRB552sEmC%2FNgUDtyMgD8pcqb1OQsUPMJWoktWQ0Q2cuV6mcF%2FRwUhhdwtkLIJOjLTFC6CSd%2BGDtcP02Z0e%2FvMwLrStKyhtXPCGc9Jqow3PtxuAqGiJSHCasqYtzSTDVTJHe%2FVv8L1yNLW%2BLzAd4GjUgWJg1p%2Byu9fpZOzv9nW%2FdOTlFqd3Sg8Ei%2BXnfeMl8aOBjszrmOGy6TaNQGB9cj0RRNUrKWzSHYCee6rgkkqY9qhdLuoz8dp0c1bD8o1veDWGdn8phPOE73%2Bqk%2FcZw319d3bw%3D%3D"

                    if (AppInviteDialog.canShow()) {
                        val content = AppInviteContent.Builder()
                                .setApplinkUrl(appLinkUrl)
                                .setPreviewImageUrl(previewImageUrl)
                                .build()
                        AppInviteDialog.show(this, content)
                    }
                }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
        setHasOptionsMenu(false)
        val intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_BLOCK)
        intentFilter.addAction(Constant.ACTION_REQUEST_ACCEPT)
        intentFilter.addAction(Constant.ACTION_RECEIVED_REQUEST_ACCEPT)
        intentFilter.addAction(Constant.ACTION_PUB_NUB_MESSAGE_UNREAD_COUNT)
        activity?.registerReceiver(receiver,intentFilter)
    }
    var receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){
                Constant.ACTION_BLOCK ->{
                    adapter?.removeUserChatByUserId(intent.getLongExtra("user_id",0))
                }
                Constant.ACTION_REQUEST_ACCEPT ->{
                    presenter.getChatUsers(MentorzApplication.instance?.prefs?.getUserId()!!, 0, false)
                }
                Constant. ACTION_RECEIVED_REQUEST_ACCEPT ->{
                    presenter.getChatUsers(MentorzApplication.instance?.prefs?.getUserId()!!, 0, false)
                }
                Constant. ACTION_PUB_NUB_MESSAGE_UNREAD_COUNT ->{
                    refreshListWithBatchCount()
                }
            }
        }

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtFindMentor.setOnClickListener(this)
        layoutInviteFriends.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.message_menu, menu)
        val searchViewItem = menu?.findItem(R.id.search)
        noSearchFound.setOnClickListener {

        }
        val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchViewAndroidActionBar.queryHint = getString(R.string.search_for_people)
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter?.searchPeople(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)){
                    noSearchFound.visibility = View.GONE
                }
                adapter?.searchPeople(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun showProgress() {
        activity?.showProgressBar(progressBar)
    }

    override fun noUserFound() {
        activity?.runOnUiThread {
            swipeToRefresh?.isRefreshing = false
            adapter?.removeLoaderFromBottom()
        }
    }
    fun getUnreadMessageWithUserId(userList: MutableList<UserListItem>): MutableList<UserListItem>{
        var countPeruser = 0
        var allCount=0
        for (item in userList) {
            countPeruser =DbManager.getInstance(activity).getUnReadCountForUserTest(item.userId)
            item.userProfile?.chatUnreadCount = countPeruser
            allCount=allCount+countPeruser
        }
        calculateAllUnreadCountAndSendBroadcast(allCount)
        return userList
    }

    fun calculateAllUnreadCountAndSendBroadcast(count:Int?){
        val intent = Intent(Constant.ACTION_PUB_COUNT_UNREAD_COUNT)
        intent.putExtra("count",count)
        MentorzApplication.applicationContext().sendBroadcast(intent)
    }
    fun refreshListWithBatchCount(){
        activity?.runOnUiThread {
            if(Controller.userList!=null){
            adapter?.refreshData(getUnreadMessageWithUserId(Controller.userList!!))}
        }
    }

    override fun setChatUserAdapter(userList: MutableList<UserListItem>) {
        activity?.runOnUiThread {
            Controller.userList=userList
            if (pageNo == 0) {
                swipeToRefresh?.isRefreshing = false
                adapter?.clearAndMentors(getUnreadMessageWithUserId(userList))
            } else {
                adapter?.addMentors(getUnreadMessageWithUserId(userList))

            }
        }
    }

    override fun hideProgress() {
        activity?.hideProgressBar(progressBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    var presenter = UserDetailsPresenterImpl(this)
    var adapter: MyMentorMenteeListAdapter?=null
    var friendId: Long = 0
    var pageNo: Int = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        friendId = activity.intent.getLongExtra("friend_id", 0)
        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
            layoutManager = linearLayout
        }
        adapter = MyMentorMenteeListAdapter(rChatList,rEmptyChat,activity,recyclerView,this)
        recyclerView.adapter = adapter
        adapter!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter!!.addLoaderAtBottom()
                presenter.getChatUsers(MentorzApplication.instance?.prefs?.getUserId()!!, ++pageNo, false)
            }
        })
        presenter.getChatUsers(MentorzApplication.instance?.prefs?.getUserId()!!, 0, false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getChatUsers(MentorzApplication.instance?.prefs?.getUserId()!!, pageNo, true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)

        getHistoryFromPubNub()

    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(receiver)
    }

    fun  searchPeople(query: String) {
        adapter?.searchPeople(query)
    }


    override fun onResume() {
        super.onResume()
        refreshListWithBatchCount()
    }
    override fun onPause() {
        super.onPause()
    }
    fun getHistoryFromPubNub(){

        var timeStamp = DbManager.getInstance(activity).getLastTimestempOfMessageFromChat()
        if(!timeStamp.isEmpty())
        {
            var currentTime= DateUtils.changeTimeStampInto17DigitLongValue(System.currentTimeMillis().toString())
            PubNubManagerService.instance.punNubMessageHistory(DateUtils.changeTimeStampInto17DigitLongValue(timeStamp),currentTime )

        }
    }





}