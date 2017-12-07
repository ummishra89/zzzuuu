package com.mentorz.me


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.CommentActivity
import com.mentorz.activities.EditProfileActivity
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.block.BlockUserPresenterImpl
import com.mentorz.block.BlockUserView
import com.mentorz.customviews.ReportAbuseDialog
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.listener.RatingClickListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.UserListItem
import com.mentorz.match.UserProfile
import com.mentorz.model.AbuseType
import com.mentorz.model.ProfileData
import com.mentorz.model.SendMentorRequest
import com.mentorz.pubnub.PubNubManagerService
import com.mentorz.requester.SendMentorRequestRequester
import com.mentorz.retrofit.listeners.SendMentorRequestListener
import com.mentorz.stories.PostListItem
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_me.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class UserProfileFragment : BaseFragment(), MentorPostDelegateAdapter.onViewSelectedListener, UserProfileView, MentorPostHeaderDelegateAdapter.onViewSelectedListener, ReportAbuseDialog.ReportAbuseDialogListener, BlockUserView, SendMentorRequestListener{
    override fun onUnFollowClick(postListItem: PostListItem) {
        presenter.unFollowUser(postListItem,postListItem.userId!!)
    }


    override fun sendMentorRequestSuccess(model: Any?, position: Int) {
        hideProgress()
        activity?.runOnUiThread {
            if(model is ProfileData){
              activity.showSnackBar(rootView, getString(R.string.request_was_send_successfully_to) + " " + model.userProfile?.name)
              model.request = UserListItem.Request.REQUEST_SENT
            }
            mentorPostListAdapter?.notifyItemChanged(0)
            var intent = Intent(Constant.ACTION_REQUEST_SENT).putExtra("user_id",userId)
            activity?.sendBroadcast(intent)
        }
    }


    override fun sendMentorRequestFail() {
        hideProgress()
        activity?.runOnUiThread {
            activity?.showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
       }
    }

    override fun onClickSendRequest(profileData: ProfileData,comment:String,position:Int) {

        if (TextUtils.isEmpty(comment)) {
            activity?.showSnackBar(rootView, getString(R.string.please_enter_some_message))
        } else {
            showProgress()
            SendMentorRequestRequester(profileData.userId!!, SendMentorRequest(comment), this, profileData, position).execute()
        }
    }

    override fun onClickFollow(profileData: ProfileData) {
        presenter.followUser(profileData,profileData.userId!!)
    }
    override fun onClickUnFollow(profileData: ProfileData) {
        presenter.unFollowUser(profileData,profileData.userId!!)
    }


    override fun networkError() {
        super.networkError()
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing =false
        }

    }

    override fun onRateUserSuccess() {
        activity?.runOnUiThread {
            activity.showSnackBar(rootView,getString(R.string.rating_has_been_submitted_successfully))
        }

    }

    override fun onRatingFail() {
        activity?.runOnUiThread {
            activity.showSnackBar(rootView,getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun onBlockUserSuccess(mentorId :Long?) {
        activity?.runOnUiThread {
            if (userId == mentorId) {
                val intent = Intent(Constant.ACTION_BLOCK).putExtra("user_id", userId)
                activity?.sendBroadcast(intent)
            }else{
                val intent = Intent(Constant.ACTION_BLOCK).putExtra("user_id", userId)
                activity?.sendBroadcast(intent)
                val intent1 = Intent(Constant.ACTION_BLOCK).putExtra("user_id",mentorId)
                activity?.sendBroadcast(intent1)
            }
            activity.finish()
        }
    }

    override fun onBlockUserFail() {
        activity?.runOnUiThread {
            activity?.showSnackBar(rootView,getString(R.string.something_went_wrong_please_try_later))
        }

    }
    override fun onClickRateNow(userId: Long,userName:String) {
         DialogUtils.showRatingDialog(activity,userName,object:RatingClickListener{
             override fun onSubmit(rating: Float, comment: String) {
                 presenter.rateUser(rating,userId,comment)
             }

         }).show()
    }

    override fun onClickBlock(userId: Long,userName:String) {
        DialogUtils.showDialog(activity,getString(R.string.are_you_sure),getString(R.string.you_want_to_block)+" "+userName,getString(R.string.cancel),getString(R.string.ok),View.OnClickListener{
            DialogUtils.dismiss()
        }, View.OnClickListener {
            blockPresenter.blockUser(userId)
            DialogUtils.dismiss()
        })
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


    override fun onVideoClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "video/mp4")
        startActivity(intent)
    }

    override fun onRatingSuccess() {
        activity?.runOnUiThread {
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onAbusePostSuccess() {
        activity?.runOnUiThread {
            mentorPostListAdapter?.removePost(postListItem!!)
            val intent = Intent()
            intent.action =Constant.ACTION_POST_ABUSE
            activity?.sendBroadcast(intent)
        }
    }

    override fun onAbusePostFail() {
    }

    var reportAbuseDialog: ReportAbuseDialog? = null
    override fun onAbuseCancelClickListener() {
        reportAbuseDialog?.dismiss()
    }

    override fun onInAppropriateContentClickListener() {
        presenter.abusePost(this.postListItem!!, AbuseType.INAPPROPRIATE_CONTENT)
        reportAbuseDialog?.dismiss()

    }

    override fun onSpamClickListener() {
        presenter.abusePost(this.postListItem!!,AbuseType.SPAM)
        reportAbuseDialog?.dismiss()
//        //for testing purpose
//        onAbusePostSuccess()
    }

    override fun isFollowingSuccess() {
        activity?.runOnUiThread {
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onUnFollowSuccess(model: Any?) {
        activity?.runOnUiThread {
            when(model){
                is PostListItem->{
                    val intent = Intent()
                    intent.action =Constant.ACTION_FOLLOW
                    intent.putExtra("user_id",model.userId)
                    intent.putExtra("is_following",model.isFollowing)
                    activity?.sendBroadcast(intent)
                }
                is ProfileData->{
                    val intent = Intent()
                    intent.action =Constant.ACTION_FOLLOW
                    intent.putExtra("user_id",model.userId)
                    intent.putExtra("is_following",model.isFollowing)
                    activity?.sendBroadcast(intent)
                }
            }

            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onFollowSuccess(model: Any?) {
        activity?.runOnUiThread {
            when(model){
                is PostListItem->{
                    val intent = Intent()
                    intent.action =Constant.ACTION_FOLLOW
                    intent.putExtra("user_id",model.userId)
                    intent.putExtra("is_following",model.isFollowing)
                    activity?.sendBroadcast(intent)
                }
                is ProfileData->{
                    val intent = Intent()
                    intent.action =Constant.ACTION_FOLLOW
                    intent.putExtra("user_id",model.userId)
                    intent.putExtra("is_following",model.isFollowing)
                    activity?.sendBroadcast(intent)
                }
            }

            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onFollowFail() {
        activity?.runOnUiThread {
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    private fun sendBroadcastForUpdatePost(postListItem: PostListItem){
        val intent = Intent()
        intent.action =Constant.ACTION_UPDATE_POST
        intent.putExtra("post_item",postListItem)
        activity?.sendBroadcast(intent)
    }
    override fun onViewPostSuccess(postListItem: PostListItem) {
        activity?.runOnUiThread {
            sendBroadcastForUpdatePost(postListItem)
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    var postListItem: PostListItem? = null
    override fun onSharePostSuccess(postListItem: PostListItem) {
        activity?.runOnUiThread {
            sendBroadcastForUpdatePost(postListItem)
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onSharePostFail() {

    }

    private val SHARE_REQUEST_CODE = 100

    private val COMMENT_REQUEST_CODE = 101
    private val UPDATE_PROFILE_REQUEST_CODE = 102

    override fun onLikePostSuccess(postListItem: PostListItem) {
        activity?.runOnUiThread {
            sendBroadcastForUpdatePost(postListItem!!)
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onLikePostFail() {
        activity?.runOnUiThread {
            mentorPostListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onCommentClick(postListItem: PostListItem) {
        this.postListItem = postListItem
        val intent = Intent(activity, CommentActivity::class.java)
        intent.putExtra("user_id", postListItem.userId)
        intent.putExtra("post_id", postListItem.postId)
        startActivityForResult(intent, COMMENT_REQUEST_CODE)
    }

    override fun onLikeClick(postListItem: PostListItem) {
        presenter.likePost(postListItem)
    }

    override fun onShareClick(postListItem: PostListItem) {
        this.postListItem = postListItem
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        if(postListItem.content?.mediaType.equals("VIDEO",true)){
            sendIntent.putExtra(Intent.EXTRA_TEXT, postListItem.content?.description+" "+postListItem.contentVideo)
        }
        else{
            sendIntent.putExtra(Intent.EXTRA_TEXT, postListItem.content?.description+" "+postListItem.contentImage)
        }
        sendIntent.type = "text/plain"
        startActivityForResult(Intent.createChooser(sendIntent, "Share"), SHARE_REQUEST_CODE)
    }

    override fun onProfileClick(postListItem: PostListItem) {
        val intent = Intent(context, MentorProfileActivity::class.java)
        intent.putExtra("user_id", postListItem.userId)
        context.startActivity(intent)
    }

    override fun onFollowClick(postListItem: PostListItem) {
        presenter.followUser(postListItem,postListItem.userId!!)
    }

    override fun onPostClick(postListItem: PostListItem) {
        presenter.viewPost(postListItem)
    }

    override fun onAbuseClick(postListItem: PostListItem) {
        this.postListItem = postListItem
        reportAbuseDialog = ReportAbuseDialog(activity, this@UserProfileFragment)
        reportAbuseDialog?.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SHARE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && postListItem != null) {
                    presenter.sharePost(postListItem!!)
                }
            }
            COMMENT_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && postListItem != null) {
                    postListItem?.commentCount = data?.getIntExtra("comment_count", 0)?.toLong()
                    sendBroadcastForUpdatePost(postListItem!!)
                    mentorPostListAdapter?.notifyDataSetChanged()

                }
            }
            UPDATE_PROFILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    mentorPostListAdapter?.updateProfile()
                }
            }
        }
        Log.d(TAG, "result code:" + resultCode + "," + Activity.RESULT_OK)
    }

    var pageNo: Int = 0
    override fun hideProgress() {
        activity?.hideProgressBar(progressBar)

    }

    override fun showProgress() {
        activity?.showProgressBar(progressBar)
    }

    override fun noPostFound() {
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing = false
            mentorPostListAdapter?.removeLoaderFromBottom()
        }
    }

    override fun setUserProfile(userListItem: ProfileData) {

        activity?.runOnUiThread {
            mentorPostListAdapter?.addProfileData(userListItem)
        }
    }


    override fun setPostAdapter(postList: List<PostListItem?>) {
        activity?.runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh?.isRefreshing = false
                mentorPostListAdapter?.clearAndAddPost(postList as List<PostListItem>)
            } else {
                mentorPostListAdapter?.setLoaded()
                mentorPostListAdapter?.addPost(postList as List<PostListItem>)

            }
        }
    }

    companion object {
        fun getInstance(userId: Long): UserProfileFragment {
            val bundle = Bundle()
            bundle.putLong("user_id", userId)
            val fragment = UserProfileFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): UserProfileFragment {
            val bundle = Bundle()
            bundle.putLong("user_id", MentorzApplication.instance?.prefs?.getUserId()!!)
            val fragment = UserProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var userId: Long = 0
    private var presenter = UserProfilePresenterImpl(this)
    private var blockPresenter = BlockUserPresenterImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        userId = arguments.getLong("user_id", 0)
        try {
          userProfile =  activity.intent.getParcelableExtra<UserProfile>("profile_data")
        }
        catch (e:Exception){
        }
        val intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_UPDATE_POST)
        intentFilter.addAction(Constant.ACTION_UPLOAD_POST)
        intentFilter.addAction(Constant.ACTION_FOLLOW)
        intentFilter.addAction(Constant.ACTION_BLOCK)
        intentFilter.addAction(Constant.ACTION_UPDATE_USER_PROFILE)
        intentFilter.addAction(Constant.UPDATE_PROFILE)
        intentFilter.addAction(Constant.ACTION_REQUEST_ACCEPT)
        activity?.registerReceiver(receiver,intentFilter)

    }

    fun refreshPostList(){
        presenter.getPost(userId, 0,false)
    }
    fun updateProfile(){
        presenter.getUserProfile(userId, 0,false)
    }
    var receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){
                Constant.ACTION_UPDATE_POST->{
                    mentorPostListAdapter?.updateItem(intent.getParcelableExtra("post_item"))
                }
                Constant.ACTION_UPLOAD_POST ->{
                    presenter.getPost(userId, 0,true)
                }
                Constant.ACTION_UPDATE_USER_PROFILE->{
                    presenter.getPost(userId, 0,true)
                }
                Constant.ACTION_FOLLOW->{
                    mentorPostListAdapter?.updateFollowStatus(intent.getLongExtra("user_id",0),intent.getBooleanExtra("is_following",false))
                   refreshData()
                }
                Constant.ACTION_BLOCK ->{
                    if (userId == intent.getLongExtra("user_id",0)){
                        noContentLayout.visibility = View.VISIBLE
                    }
                    refreshPostList()
                }
                Constant.UPDATE_PROFILE ->{
                    updateProfile()
                }
                Constant.ACTION_REQUEST_ACCEPT->{
                    if (userId == intent.getLongExtra("user_id",0)){
                        presenter.getPost(userId, 0,false)
                    }
                }

            }

        }

    }
    private fun refreshHeader(){
        var mentorPostHeaderDeligateAdapter : MentorPostDelegateAdapter ? =null

       // mentorPostHeaderDeligateAdapter.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun dataSetChanged() {
        activity?.runOnUiThread {
            mentorPostListAdapter?.notifyItemChanged(0)
        }
    }
    var mentorPostListAdapter:MentorPostListAdapter?=null
    var userProfile:UserProfile?=null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
            layoutManager = linearLayout
        }

        mentorPostListAdapter = MentorPostListAdapter(userProfile,this, this)
        recyclerView.adapter =mentorPostListAdapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mentorPostListAdapter!!.setOnLoadMoreListener(recyclerView,object : OnLoadMoreListener {
            override fun onLoadMore() {
                recyclerView.post {
                    mentorPostListAdapter!!.addLoaderAtBottom()
                    presenter.getPost(userId,++pageNo,false)
                }

            }
        })
        presenter.getPost(userId, 0,false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getPost(userId, pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)

        val a = ProfileData()
        if(userId==MentorzApplication.instance?.prefs?.getUserId()){
            a.userProfile = UserProfile()
            a.userProfile?.name= MentorzApplication.instance?.prefs?.getUserName()
            a.userProfile?.basicInfo = MentorzApplication.instance?.prefs?.getBasicInfo()
        }else{
            a.userProfile = UserProfile()
            a.userProfile?.name= ""
            a.userProfile?.basicInfo = ""
        }
        mentorPostListAdapter?.addProfileData(a)
    }
    fun refreshData(){
        presenter.getProfileByRequeater(userId, pageNo,true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        if (userId == MentorzApplication.instance?.prefs?.getUserId()) {
            inflater?.inflate(R.menu.me_menu, menu)
            val editItem = menu?.findItem(R.id.edit)
            editItem?.setOnMenuItemClickListener {
                activity.startActivityForResult(Intent(activity, EditProfileActivity::class.java), 0)
                return@setOnMenuItemClickListener true
            }
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (item?.itemId == R.id.edit) {
//            activity.startActivityForResult(Intent(activity, EditProfileActivity::class.java), 0)
//            return true
//        } else {
//            return super.onOptionsItemSelected(item)
//        }
//    }
    override fun onDestroy() {
        activity?.unregisterReceiver(receiver)
        super.onDestroy()
    }

}