package com.mentorz.stories


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.mentorz.R
import com.mentorz.activities.CommentActivity
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.customviews.ReportAbuseDialog
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.listener.AbusePostListener
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.adapter.ViewType
import com.mentorz.model.AbuseType
import com.mentorz.model.ProfileData
import com.mentorz.requester.AbusePostRequester
import com.mentorz.stories.adapter.PostDelegateAdapter
import com.mentorz.stories.adapter.PostListAdapter
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Global
import kotlinx.android.synthetic.main.fragment_stories.*

/**
 * A simple [Fragment] subclass.
 */
class StoriesFilterFragment : BaseFragment(), PostView, PostDelegateAdapter.onViewSelectedListener, ReportAbuseDialog.ReportAbuseDialogListener, AbusePostListener {


    override fun onNetworkFail() {

    }


    var description:String? =null
    var content =Content()

    private val SHARE_REQUEST_CODE = 100

    private val COMMENT_REQUEST_CODE = 106


    override fun networkError() {
        super.networkError()
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing =false
        }
    }

    override fun onSessionExpired() {
        super.onSessionExpired()
     /*   activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(activity, AuthenticationActivity::class.java))
                        activity.finish()
                    })
        }*/
    }

    override fun onVideoClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "video/mp4")
        startActivity(intent)
    }

    override fun onRatingSuccess() {
        activity?.runOnUiThread {
            postListAdapter?.notifyDataSetChanged()
        }

    }

    override fun onAbusePostSuccess() {
        activity?.runOnUiThread {
            postListAdapter?.removePost(postListItem!!)

            val intent = Intent()
            intent.action =Constant.ACTION_POST_ABUSE
            activity?.sendBroadcast(intent)
        }

    }

    override fun dataSetChanged() {
        activity?.runOnUiThread {
            postListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onAbusePostFail() {
    }

    var reportAbuseDialog: ReportAbuseDialog? = null
    var position: Int = 0
    override fun onAbuseCancelClickListener() {
        postListAdapter?.notifyItemChanged(position)
        reportAbuseDialog?.dismiss()
    }

    override fun onInAppropriateContentClickListener() {
        postListAdapter?.notifyItemChanged(position)
        AbusePostRequester((postListAdapter?.getList() as MutableList<*>)[position] as PostListItem, AbuseType.INAPPROPRIATE_CONTENT, this, this).execute()
//        presenter.abusePost(this.postListItem!!, AbuseType.INAPPROPRIATE_CONTENT)
        reportAbuseDialog?.dismiss()
        onAbusePostSuccess()

    }

    override fun onSpamClickListener() {
        postListAdapter?.notifyItemChanged(position)
        AbusePostRequester((postListAdapter?.getList() as MutableList<*>)[position] as PostListItem, AbuseType.SPAM, this, this).execute()
        //presenter.abusePost(this.postListItem!!,AbuseType.SPAM)
        reportAbuseDialog?.dismiss()
        //for testing purpose
        onAbusePostSuccess()


    }

    override fun isFollowingSuccess() {
        activity?.runOnUiThread {
            postListAdapter?.notifyDataSetChanged()
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
                is ProfileData ->{
                    val intent = Intent()
                    intent.action =Constant.ACTION_FOLLOW
                    intent.putExtra("user_id",model.userId)
                    intent.putExtra("is_following",model.isFollowing)
                    activity?.sendBroadcast(intent)
                }
            }
            postListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onFollowFail() {
        activity?.runOnUiThread {
            postListAdapter?.notifyDataSetChanged()
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
                is ProfileData ->{
                    val intent = Intent()
                    intent.action =Constant.ACTION_FOLLOW
                    intent.putExtra("user_id",model.userId)
                    intent.putExtra("is_following",model.isFollowing)
                    activity?.sendBroadcast(intent)
                }
            }
            refreshList()
        }
    }
    fun refreshList(){
        postListAdapter?.notifyDataSetChanged()
    }


    override fun onViewPostSuccess(postListItem: PostListItem) {
        activity?.runOnUiThread {
            sendBroadcastForUpdatePost(postListItem)
            postListAdapter?.notifyDataSetChanged()
        }
    }

    var postListItem: PostListItem? = null
    override fun onSharePostSuccess(postListItem: PostListItem) {
        activity?.runOnUiThread {
            sendBroadcastForUpdatePost(postListItem)
            postListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onSharePostFail() {

    }


    override fun onLikePostSuccess(postListItem: PostListItem) {
        activity?.runOnUiThread {
            sendBroadcastForUpdatePost(postListItem)
            postListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onLikePostFail() {
        activity?.runOnUiThread {
            postListAdapter?.notifyDataSetChanged()
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
    override fun onUnFollowClick(postListItem: PostListItem) {
        presenter.unFollowUser(postListItem,postListItem.userId!!)
    }

    override fun onPostClick(postListItem: PostListItem) {
        presenter.viewPost(postListItem)
    }

    override fun onAbuseClick(position: Int, postListItem: PostListItem) {
        this.postListItem = postListItem
        this.position = position
        reportAbuseDialog = ReportAbuseDialog(activity, this@StoriesFilterFragment)
        reportAbuseDialog?.show()
        reportAbuseDialog?.setOnDismissListener {
            postListAdapter?.notifyItemChanged(position)
        }

    }

    var pageNo: Int = 0
    override fun hideProgress() {
        activity?.runOnUiThread {
            activity.hideProgressBar(progressBar)
        }

    }

    override fun showProgress() {
        activity?.runOnUiThread {
            activity.showProgressBar(progressBar)
        }
    }

    override fun noPostFound() {
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing = false
            postListAdapter?.removeLoaderFromBottom()
        }
    }

    override fun setPostAdapter(postList: MutableList<ViewType>) {
        activity?.runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh.isRefreshing = false
                postListAdapter?.clearAndAddPost(postList as List<PostListItem>)
            } else {
                postListAdapter?.addPost(postList as List<PostListItem>)

            }
        }
    }

    var presenter = PostPresenterImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_UPDATE_POST)
        intentFilter.addAction(Constant.ACTION_FOLLOW)
        intentFilter.addAction(Constant.ACTION_BLOCK)
        activity?.registerReceiver(receiver,intentFilter)
        setHasOptionsMenu(true)

    }

    var postListAdapter: PostListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
            layoutManager = linearLayout
        }
        postListAdapter = PostListAdapter(presenter.postList!!,this,null)
        recyclerView.adapter = postListAdapter
        postListAdapter?.registerAdapterDataObserver(emptyMessage)
        postListAdapter!!.setOnLoadMoreListener(recyclerView,object :OnLoadMoreListener{
            override fun onLoadMore() {
                if(postListAdapter!!.itemCount>0) {
                    postListAdapter!!.addLoaderAtBottom()
                    presenter.getPostByInterest(getInterest(),++pageNo, false)
                }
            }

        })

        presenter.getPostByInterest(getInterest(),0,false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getPostByInterest(getInterest(),pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.stories_filter_menu, menu)
        val searchViewItem = menu?.findItem(R.id.search)

       val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchViewAndroidActionBar.queryHint = getString(R.string.search_for_people_or_interest)

        searchViewItem?.setOnActionExpandListener(object:MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {

                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {

                hideKeyBoard()
                return true
            }

        })
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                postListAdapter?.filter?.filter(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return true
    }
    private fun sendBroadcastForUpdatePost(postListItem: PostListItem){
        var intent = Intent()
        intent.action = Constant.ACTION_UPDATE_POST
        intent.putExtra("post_item",postListItem)
        activity?.sendBroadcast(intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                        recyclerView.adapter?.notifyDataSetChanged()

                    }
                }


        }
    }
    private fun getInterest():MutableList<Int>{
        val interestList = mutableListOf<Int>()
        Global.filteredInterests?.forEach {
            interestList.add(it.interestId!!)
        }
        return interestList
    }
    fun refreshPostList(){
        presenter.getPost(0,true)
    }

    var receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){
                Constant.ACTION_UPDATE_POST->{
                    refreshPostList();
                }
                Constant.ACTION_FOLLOW->{
                    postListAdapter?.updateFollowStatus(intent.getLongExtra("user_id",0),intent.getBooleanExtra("is_following",false))
                }
                Constant.ACTION_BLOCK ->{
                    refreshPostList()
                    postListAdapter?.removePostByUserId(intent.getLongExtra("user_id",0))
                }
            }
        }

    }

}