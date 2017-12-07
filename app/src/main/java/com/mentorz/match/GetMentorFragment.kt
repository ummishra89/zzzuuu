package com.mentorz.match


import android.app.Activity
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
import android.util.Log
import android.view.*
import com.mentorz.R
import com.mentorz.activities.FilterActivity
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.expertise.ExpertiseActivity
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.interest.InterestsItem
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.adapter.MentorListAdapter
import com.mentorz.match.adapter.MentorsDelegateAdapter
import com.mentorz.model.*
import com.mentorz.requester.SendMentorRequestRequester
import com.mentorz.retrofit.listeners.SendMentorRequestListener
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Global
import kotlinx.android.synthetic.main.fragment_request_mentor.*


/**
 * A simple [Fragment] subclass.
 */
class GetMentorFragment : BaseFragment(), MatchView, MentorsDelegateAdapter.onViewSelectedListener, SendMentorRequestListener {
    private val PROFILE_REQUEST_CODE=100
    private val FILTER_REQUEST_CODE: Int = 101

    override fun networkError() {
        super.networkError()
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing =false
        }
    }
    override fun onProfileClick(item: UserListItem) {
        val intent = Intent(context, MentorProfileActivity::class.java)
        intent.putExtra("user_id", item.userId)
        val rating = Rating()
        rating.rating = item.rating
        item.userProfile?.rating = rating
        item.userProfile?.requests=item.request
        Log.d("adapter", "rating:" + item.userProfile?.rating?.rating)
        intent.putExtra("profile_data", item.userProfile)
        startActivityForResult(intent,PROFILE_REQUEST_CODE)

    }

    override fun onSessionExpired() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(activity, AuthenticationActivity::class.java))
                        activity.finish()
                    })
        }
    }


    override fun dataSetChanged() {
        activity?.runOnUiThread {
            mentorListAdapter?.notifyDataSetChanged()
        }

    }

    override fun requestSent() {
        activity?.showSnackBar(rootView, getString(R.string.request_already_sent))
    }

    override fun alreadyYourMentor() {
        activity?.showSnackBar(rootView, getString(R.string.already_your_mentor))
    }

    override fun sendMentorRequestSuccess(model: Any?, position: Int) {
        activity?.runOnUiThread {
            when(model){
                is UserListItem->{
                    activity.showSnackBar(rootView, getString(R.string.request_was_send_successfully_to) + " " + model.userProfile?.name)
                    model.request = UserListItem.Request.REQUEST_SENT
                }
                is ProfileData->{
                    activity.showSnackBar(rootView, getString(R.string.request_was_send_successfully_to) + " " + model.userProfile?.name)
                    model.request = UserListItem.Request.REQUEST_SENT
                }
            }

            mentorListAdapter?.notifyItemChanged(position)

        }
    }

    override fun sendMentorRequestFail() {
        activity?.showSnackBar(rootView, getString(R.string.unable_to_connect))
    }

    override fun onSendRequestClick(message: String, item: UserListItem, position: Int) {
        if (TextUtils.isEmpty(message)) {
            activity?.showSnackBar(rootView, getString(R.string.please_enter_some_message))
        } else {
            SendMentorRequestRequester(item.userId!!,SendMentorRequest(message), this, item, position).execute()
        }
    }

    override fun noMentorFound() {
        activity?.runOnUiThread {
            mentorListAdapter?.removeLoaderFromBottom()
            if ((recyclerView.adapter as MentorListAdapter).getMentors().isEmpty()) {
                textNoMentor.visibility = View.VISIBLE
            }

        }
    }

    var pageNo = 0
    var mentorListAdapter: MentorListAdapter? = null
    override fun onExpertiseClick(list: MutableList<ExpertiseItem>) {
        val intent = Intent(activity, ExpertiseActivity::class.java)
        val arrayList: ArrayList<ExpertiseItem> = arrayListOf()
        arrayList.addAll(list)
        intent.putExtra("user_type", UserType.FRIEND)
        intent.putParcelableArrayListExtra("mentor_expertise", arrayList)
        startActivity(intent)
    }


    override fun updateMentorListAdapter(userList: List<UserListItem?>?) {
        activity?.runOnUiThread {
            mentorListAdapter?.notifyDataSetChanged()
        }
    }

    override fun setMentorListAdapter(userList: List<UserListItem?>?) {
        activity?.runOnUiThread({
            if (pageNo == 0) {
                swipeToRefresh?.isRefreshing = false
                mentorListAdapter?.clearAndAddMentors(userList as List<UserListItem>)
            } else {
                mentorListAdapter?.setLoaded()
                mentorListAdapter?.addMentors(userList as List<UserListItem>)
            }

        })
    }

    override fun onItemSelected(url: String?) {

    }

    override fun hideProgress() {
        activity?.hideProgressBar(progressBar)
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing =false
        }
    }

    override fun showProgress() {
       activity?.showProgressBar(progressBar)
    }

    val presenter = MatchPresenterImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        var intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_REQUEST_SENT)
        intentFilter.addAction(Constant.ACTION_BLOCK)
        activity?.registerReceiver(receiver,intentFilter)


    }

    var menu: Menu?=null
    var inflater: MenuInflater?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_mentor, container, false)
    }

    var receiver = object :BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){
                Constant.ACTION_BLOCK->{
                    mentorListAdapter?.removeItem(intent!!.getLongExtra("user_id",0))
                }
                Constant.ACTION_REQUEST_SENT->{
                    mentorListAdapter?.updateItem(intent!!.getLongExtra("user_id",0))
                }
            }

        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
            layoutManager = linearLayout

        }
        mentorListAdapter = MentorListAdapter(activity, this)
        recyclerView.adapter = mentorListAdapter
        mentorListAdapter?.registerAdapterDataObserver(textNoMentor)
        mentorListAdapter!!.setOnLoadMoreListener(recyclerView,object : OnLoadMoreListener {
            override fun onLoadMore() {
                mentorListAdapter!!.addLoaderAtBottom()
                presenter.getMyMentors(getMentorRequest(),++pageNo,false)
            }
        })
        presenter.getMyMentors(getMentorRequest(),pageNo,false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getMyMentors(getMentorRequest(),pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)

    }

    fun getMentorRequest(): MyMentorRequest {
        val mentorRequest = MyMentorRequest()
        val myInterestList = mutableListOf<InterestsItem>()
        if(Global.filteredExpertise!!.isEmpty()) {
            myInterestList.addAll(Global.userInterests!!)
        }
        else{
            myInterestList.addAll(Global.filteredExpertise!!)
        }
        val list = mutableListOf<InterestIds>()
        for (item in myInterestList) {
            val ids = InterestIds()
            ids.interestId = item.interestId
            ids.parentId = item.parentId
            list.add(ids)
        }
        mentorRequest.interests = list
        mentorRequest.maxExp = FilterActivity.maxExp
        mentorRequest.minExp = FilterActivity.minExp
        return mentorRequest
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        menu?.clear()
//        this.inflater=inflater
//        this.menu=menu
//        inflater?.inflate(R.menu.match_menu, menu)
//        val searchViewItem = menu?.findItem(R.id.search)
//        val filterViewItem = menu?.findItem(R.id.filter)
//
//        val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
//
//        searchViewAndroidActionBar.queryHint = getString(R.string.mentor_search_hint)
//
//        searchViewItem?.setOnActionExpandListener(object:MenuItem.OnActionExpandListener{
//            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
//                filterViewItem?.isVisible=false
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
//                filterViewItem?.isVisible=true
//                hideKeyBoard()
//                return true
//            }
//
//        })
//        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                searchViewAndroidActionBar.clearFocus()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                mentorListAdapter?.filter?.filter(newText)
//                return false
//            }
//        })
//
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.filter -> {
                startActivityForResult(Intent(activity, FilterActivity::class.java), FILTER_REQUEST_CODE)
            }

        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FILTER_REQUEST_CODE->{
                if(resultCode==Activity.RESULT_OK) {
                    pageNo = 0
                    presenter.clearList()
                    var mentorRequest = data?.getParcelableExtra<MyMentorRequest>("mentor_request")
                    presenter.getMyMentors(mentorRequest!!,pageNo, true)
                }
            }
        }
    }


    override fun onDestroy() {
        activity?.unregisterReceiver(receiver)
        super.onDestroy()
    }


}