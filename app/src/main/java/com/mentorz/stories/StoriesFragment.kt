package com.mentorz.stories


import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.facebook.CallbackManager
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.mentorz.R
import com.mentorz.activities.CommentActivity
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.activities.StoriesFilterActivity
import com.mentorz.controller.FileUploadController
import com.mentorz.customviews.ChooseImageDialog
import com.mentorz.customviews.ChooseVideoDialog
import com.mentorz.customviews.ReportAbuseDialog
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.interest.InterestsActivity
import com.mentorz.listener.AbusePostListener
import com.mentorz.listener.AddPostListener
import com.mentorz.listener.OnLoadMoreListener
import com.mentorz.listener.PublishDialogClickListener
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.adapter.ViewType
import com.mentorz.model.AbuseType
import com.mentorz.model.ProfileData
import com.mentorz.requester.AbusePostRequester
import com.mentorz.requester.AddPostRequester
import com.mentorz.stories.adapter.PostDelegateAdapter
import com.mentorz.stories.adapter.PostListAdapter
import com.mentorz.stories.adapter.UploadingProgressAdapter
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import com.mentorz.utils.Constant
import com.mentorz.utils.DateUtils
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Utils
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import kotlinx.android.synthetic.main.fragment_stories.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class StoriesFragment : BaseFragment(), PostView, PostDelegateAdapter.onViewSelectedListener, ReportAbuseDialog.ReportAbuseDialogListener, PublishDialogClickListener, ChooseImageDialog.ChooseImageDialogListener, ChooseVideoDialog.ChooseVideoDialogListener, FileUploadListener, AddPostListener, UploadingProgressAdapter.onViewSelectedListener, AbusePostListener {
    override fun onUnFollowClick(postListItem: PostListItem) {
        presenter.unFollowUser(postListItem,postListItem.userId!!)
    }

    override fun networkError() {
        super.networkError()
        activity?.runOnUiThread {
            swipeToRefresh.isRefreshing =false
        }
    }

    override fun onSessionExpired() {
        super.onSessionExpired()
    }
    override fun onCancelUpload() {
        isUploadProgress=false
        progress=0
        postListAdapter?.removeProgress()
        val thread = FileUploadController.unit
        thread!!.interrupt()

    }

    private var  contentRealUrl: String = ""

    override fun fileUploadSuccess(url: String) {
        contentRealUrl = url
    }

    override fun onAddPostSuccess() {
        activity.runOnUiThread {
            try {
                isUploadProgress=false
                progress=0
                postListAdapter?.updateUploadFileProgress(100)
                pageNo = 0
                postListAdapter?.removeProgress()
                presenter.getPost(pageNo,true)
                shareOnFacebookDialog()


            }catch (exception :Exception){
                Log.e("StoriesFragment",""+exception.toString());
            }
            val intent = Intent()
            intent.action = Constant.ACTION_UPLOAD_POST
            activity.sendBroadcast(intent)

        }
    }

    private fun shareOnFacebookDialog() {
        DialogUtils.showDialog(context,"",getString(R.string.do_you_want_to_share_this_post_on_facebook),getString(R.string.yes),getString(R.string.no),View.OnClickListener {
            shareOnFacebook()
            DialogUtils.dismiss()
        },View.OnClickListener {
            DialogUtils.dismiss()
        })
    }

    var callbackManager: CallbackManager? = null
    var shareDialog: ShareDialog? = null
    private fun shareOnFacebook() {
        val facebookShare = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(contentRealUrl))
                .setShareHashtag(ShareHashtag.Builder()
                        .setHashtag("#MyMentorz")
                        .build())
                .build()
        shareDialog?.show(facebookShare)
    }

    override fun onAddPostFail() {
       activity.runOnUiThread {
           activity?.showSnackBar(rootView,getString(R.string.something_went_wrong_please_try_later))
       }
    }
    override fun publishProgress(progress: Int,fileType: String) {
        activity?.runOnUiThread {
            isUploadProgress=true
            Log.d(TAG, "progress:" + progress)
            if (fileType == FileType.VIDEO&& progress!=100) {
                postListAdapter?.updateUploadFileProgress(progress)
                recyclerView.scrollToPosition(0)
            } else if (fileType == FileType.PROFILE_VIDEO_THUMBNAIL) {

            } else {
                if(progress!=100) {
                    postListAdapter?.updateUploadFileProgress(progress)
                    recyclerView.scrollToPosition(0)
                }
            }
        }
    }
    var description:String? =null
    var content:Content?=null
    override fun fileUploadSuccess(token: String, fileType: String) {
        Log.d(TAG,"fileUploadSuccess:")
         content!!.description=description

        if(fileType==FileType.VIDEO){
            content!!.mediaType =  "VIDEO"
            content!!.hresId=token
        }
        else if(fileType==FileType.PROFILE_VIDEO_THUMBNAIL){
            content!!.lresId=token
        }
        else{
            content!!.hresId=token
            content!!.lresId=token
            content!!.mediaType="IMAGE"

        }
        if(content!!.hresId!=null&&content!!.lresId!=null){
            AddPostRequester(content!!,this).execute()
        }
    }

    override fun fileUploadFail() {

    }

    override fun onSelectFromGallery() {
       requestRuntimePermissionForVideo()
    }

    override fun onCaptureVideo() {
        requestRuntimePermissionForRecordVideo()
    }


    var chooseImageDialog: ChooseImageDialog? = null

    var chooseVideoDialog: ChooseVideoDialog? = null
    private val SHARE_REQUEST_CODE = 100

    private val COMMENT_REQUEST_CODE = 106
    val CAMERA_REQUEST_CODE = 101
    val GALLERY_REQUEST_CODE = 102
    val CROP_PIC_REQUEST_CODE = 103
    val TAKE_VIDEO_REQUEST_CODE = 104
    val CHOOSE_VIDEO_REQUEST_CODE = 105
    val FILTER_INTEREST_REQUEST_CODE = 107
    override fun onImageClick(text:String) {
        publishText = text
        requestRuntimePermissionForImage()
    }

    override fun onVideoPublishClick(text:String) {
        publishText = text
        chooseVideo()
    }


    override fun onCancelClickListener() {
        chooseImageDialog?.dismiss()
    }

    override fun onGalleryClickListener() {
        chooseImageDialog?.dismiss()
        val pickPhoto = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE)
    }

    override fun onCameraClickListener() {
        chooseImageDialog?.dismiss()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onPublish(fileType: String,uri: Uri, comment: String) {
        description=comment
        content= Content()
        if(fileType == FileType.VIDEO){
            //uploadImage(uri,FileType.VIDEO_THUMBNAIL)
            uploadImage(Uri.parse(Utils.getThumbnailPathForLocalFile(activity, uri)), FileType.PROFILE_VIDEO_THUMBNAIL)
            uploadVideo(uri,fileType)
            postListAdapter?.updateUploadFileProgress(0)
            recyclerView.scrollToPosition(0)
        }
        else if(fileType == FileType.IMAGE){
            uploadImage(uri,fileType)
            postListAdapter?.updateUploadFileProgress(0)
            recyclerView.scrollToPosition(0)
        }
    }



    companion object {
        var progress:Long=0
        var isUploadProgress=false
        fun newInstance(): StoriesFragment {
            return StoriesFragment()
        }
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
       // presenter.abusePost(this.postListItem!!, AbuseType.INAPPROPRIATE_CONTENT)
        AbusePostRequester(postListItem!!, AbuseType.INAPPROPRIATE_CONTENT, this, this).execute()
        reportAbuseDialog?.dismiss()
        onAbusePostSuccess()

    }

    override fun onSpamClickListener() {
        postListAdapter?.notifyItemChanged(position)
        //presenter.abusePost(this.postListItem!!,AbuseType.SPAM)
        AbusePostRequester(postListItem!!, AbuseType.SPAM, this, this).execute()
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

            postListAdapter?.notifyDataSetChanged()
        }
    }

    private fun sendBroadcastForUpdatePost(postListItem: PostListItem){
        var intent = Intent()
        intent.action =Constant.ACTION_UPDATE_POST
        intent.putExtra("post_item",postListItem)
        activity?.sendBroadcast(intent)
    }

    override fun onFollowFail() {
        activity?.runOnUiThread {
            postListAdapter?.notifyDataSetChanged()
        }
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
        intent.putExtra("user_id", postListItem.postId)
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

    override fun onAbuseClick(position: Int, postListItem: PostListItem) {
        this.postListItem = postListItem
        this.position = position
        reportAbuseDialog = ReportAbuseDialog(activity, this@StoriesFragment)
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
            swipeToRefresh?.isRefreshing = false
            postListAdapter?.removeLoaderFromBottom()

        }
    }

    override fun setPostAdapter(postList: MutableList<ViewType>) {
        activity?.runOnUiThread {
            if (pageNo == 0) {
                swipeToRefresh?.isRefreshing = false
                postListAdapter?.clearAndAddPost(postList as List<PostListItem>)
            } else {
                postListAdapter?.addPost(postList as List<PostListItem>)

            }
        }
    }

    var presenter = PostPresenterImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val intentFilter =  IntentFilter()
        intentFilter.addAction(Constant.ACTION_UPDATE_POST)
        intentFilter.addAction(Constant.ACTION_UPLOAD_POST)
        intentFilter.addAction(Constant.ACTION_FOLLOW)
        intentFilter.addAction(Constant.ACTION_POST_ABUSE)


        intentFilter.addAction(Constant.ACTION_BLOCK)
        intentFilter.addAction(Constant.ACTION_UPDATE_USER_PROFILE)
        activity?.registerReceiver(receiver,intentFilter)

        callbackManager = CallbackManager.Factory.create();
        shareDialog = ShareDialog(activity);



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

    var receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.action){
                Constant.ACTION_UPDATE_POST->{
                    postListAdapter?.updateItem(intent.getParcelableExtra("post_item"))
                }
                Constant.ACTION_FOLLOW->{
                    postListAdapter?.updateFollowStatus(intent.getLongExtra("user_id",0),intent.getBooleanExtra("is_following",false))
                }
                Constant.ACTION_POST_ABUSE->{
                    refreshPostList()
                }
                Constant.ACTION_BLOCK ->{
                    refreshPostList()
                    postListAdapter?.removePostByUserId(intent.getLongExtra("user_id",0))
                }
                Constant.ACTION_UPLOAD_POST->{
                    refreshPostList()
                }
                Constant.ACTION_UPDATE_USER_PROFILE->{
                    refreshPostList()
                }
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hideKeyBoard()
        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(activity)
           // val linearLayout = WrapContentLinearLayoutManager()

            layoutManager = linearLayout
            clearOnScrollListeners()
        }
        postListAdapter = PostListAdapter(presenter.postList!!,this,this)
        recyclerView.adapter = postListAdapter
        postListAdapter?.registerAdapterDataObserver(emptyMessage)
        postListAdapter!!.setOnLoadMoreListener(recyclerView,object :OnLoadMoreListener{
            override fun onLoadMore() {
                if(postListAdapter!!.itemCount>0&&!postListAdapter!!.isLoadingInProgress()) {
                    postListAdapter!!.addLoaderAtBottom()
                    presenter.getPost(++pageNo, false)
                }
            }

        })

        presenter.getPost(0,false)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = true
            pageNo = 0
            presenter.getPost(pageNo,true)
        }
        swipeToRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.CYAN)
    }
    fun refreshPostList(){
        presenter.getPost(0,true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.stories_menu, menu)
        val searchViewItem = menu?.findItem(R.id.search)
        val publishPostViewItem = menu?.findItem(R.id.add)
        val filterViewItem = menu?.findItem(R.id.filter)

        val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
        filterViewItem?.isVisible=false
        searchViewAndroidActionBar.queryHint = getString(R.string.search_for_people_or_interest)
        searchViewItem?.setOnActionExpandListener(object:MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                publishPostViewItem?.isVisible=false
                filterViewItem?.isVisible=true
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                publishPostViewItem?.isVisible=true
                filterViewItem?.isVisible=false
                hideKeyBoard()
                return true
            }
        })
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewAndroidActionBar.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                postListAdapter?.filter?.filter(newText)

                return false
            }
        })
    }
    var publishText = ""

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
           R.id.add->{
               if(postListAdapter?.isAlreadyUploading()!!){
                   return true
               }
               publishText = ""
               DialogUtils.showPublishPostDialog("",activity,null,this,publishText).show()
           }
            R.id.filter->{
                hideKeyBoard()
                val intent = Intent(activity, InterestsActivity::class.java)
                intent.putExtra("to_filter",true)
                startActivityForResult(intent,FILTER_INTEREST_REQUEST_CODE)
            }
        }
        return true
    }


    fun requestRuntimePermissionForRecordVideo() {
        val permissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        (activity as PermissionsActivity).requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Call permissions", "Granted")
                (activity as PermissionsActivity).requestPermissions(permissions[1], object : PermissionCallBack {
                    override fun permissionGranted() {
                        super.permissionGranted()
                        Log.v("Call permissions", "Granted")
                        recordVideo()
                    }

                    override fun permissionDenied() {
                        super.permissionDenied()
                        Log.v("Call permissions", "Denied")
                    }
                })
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Call permissions", "Denied")
            }
        })
    }

    fun requestRuntimePermissionForImage() {
        val permissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        (activity as PermissionsActivity).requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Storage permissions", "Granted")
                (activity as PermissionsActivity).requestPermissions(permissions[1], object : PermissionCallBack {
                    override fun permissionGranted() {
                        super.permissionGranted()
                        Log.v("Camera permissions", "Granted")
                        chooseImage()
                    }

                    override fun permissionDenied() {
                        super.permissionDenied()
                        Log.v("Camera permissions", "Denied")
                    }
                })
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Storage permissions", "Denied")
            }
        })



    }

    fun chooseImage() {
        chooseImageDialog = ChooseImageDialog(activity, this)
        chooseImageDialog?.show()
    }
    fun chooseVideo() {
        chooseVideoDialog = ChooseVideoDialog(activity, this)
        chooseVideoDialog?.show()
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

            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap
                    val path = MediaStore.Images.Media.insertImage(activity.contentResolver, imageBitmap, "image", null)
                    doCrop(Uri.parse(path))
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data
                    if (uri != null) {
                        doCrop(uri)
                    }
                }
            }
            CROP_PIC_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.data
                    chooseImageDialog?.dismiss()
                    if (uri != null) {
                        DialogUtils.dismiss()
                        DialogUtils.showPublishPostDialog(FileType.IMAGE,activity,uri,this,publishText).show()
                    }
                }
            }
            CHOOSE_VIDEO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = Uri.parse(data?.data.toString())
                     DialogUtils.dismiss()
                     DialogUtils.showPublishPostDialog(FileType.VIDEO,activity,uri,this,publishText).show()
                 }
            }
            TAKE_VIDEO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = Uri.parse(data?.data.toString())
                    DialogUtils.dismiss()
                    DialogUtils.showPublishPostDialog(FileType.VIDEO,activity,uri,this,publishText).show()

                }
            }
            FILTER_INTEREST_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                   startActivity(Intent(activity,StoriesFilterActivity::class.java))

                }
            }

        }
    }

    fun recordVideo() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().path + "videocapture.mp4")
        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST_CODE)
    }
    private fun doCrop(picUri: Uri) {
        val image = File(Utils.appFolderCheckandCreate(), "img" + DateUtils.getTimeStamp() + ".jpg")
        val uriSavedImage = Uri.fromFile(image)
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(picUri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 640)
        intent.putExtra("outputY", 640)
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
        intent.putExtra("return-data", true)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, CROP_PIC_REQUEST_CODE)
    }
    fun requestRuntimePermissionForVideo() {
        val permissions: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        (activity as PermissionsActivity).requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Storage permissions", "Granted")
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, CHOOSE_VIDEO_REQUEST_CODE)
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Storage permissions", "Denied")
            }
        })
    }
    private fun uploadImage(fileUri: Uri, fileType: String) {
        val filePath = Utils.getRealPathFromURI(activity, fileUri)
      //  val filePath = fileUri.path
        val type = Utils.getMimeType(activity, fileUri)
        FileUploadRequester(this, filePath, "image/" + type, fileType).execute()
    }

    private fun uploadVideo(fileUri: Uri, fileType: String) {
        val filePath = Utils.getRealPathFromURI(activity,fileUri)
        val type = Utils.getMimeType(activity, fileUri)
        FileUploadRequester(this, filePath, "video/" + type, fileType).execute()
    }
    override fun onDestroy() {
        activity?.unregisterReceiver(receiver)
        super.onDestroy()
    }

}