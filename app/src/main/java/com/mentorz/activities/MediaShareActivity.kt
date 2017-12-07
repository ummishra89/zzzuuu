package com.mentorz.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.listener.AddPostListener
import com.mentorz.listener.PublishDialogClickListener
import com.mentorz.requester.AddPostRequester
import com.mentorz.stories.Content
import com.mentorz.stories.adapter.PostListAdapter
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Utils
import kotlinx.android.synthetic.main.fragment_stories.*

import org.jetbrains.anko.toast


/**
 * Created by craterzone on 10/4/2017.
 */
class MediaShareActivity : BaseActivity(), PublishDialogClickListener, FileUploadListener, AddPostListener, View.OnClickListener {
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ctv_button ->{
                DialogUtils.dismiss();
                this@MediaShareActivity.finish()
            }

        }

    }

    override fun onAddPostSuccess() {
        runOnUiThread(Runnable {
            myToast(applicationContext,""+resources.getString(R.string.shared_successfully))
            val intent = Intent()
            intent.action = Constant.ACTION_UPLOAD_POST
            sendBroadcast(intent)
        })

    }

    override fun onAddPostFail() {
        runOnUiThread(Runnable {
            myToast(applicationContext,""+resources.getString(R.string.shared_unsuccessfully))
        })
    }

    var description: String? = null
    var content: Content? = null
    var postListAdapter: PostListAdapter? = null

    override fun fileUploadSuccess(token: String, fileType: String) {
        Log.d(TAG, "fileUploadSuccess:")
        content!!.description = description

        if (fileType == FileType.VIDEO) {
            content!!.mediaType = "VIDEO"
            content!!.hresId = token
        } else if (fileType == FileType.PROFILE_VIDEO_THUMBNAIL) {
            content!!.lresId = token
        } else {
            content!!.hresId = token
            content!!.lresId = token
            content!!.mediaType = "IMAGE"

        }
        if (content!!.hresId != null && content!!.lresId != null) {
            AddPostRequester(content!!, this).execute()
        }

        this@MediaShareActivity.finish()
    }

    override fun fileUploadFail() {
        runOnUiThread(Runnable {
            myToast(applicationContext,""+resources.getString(R.string.shared_unsuccessfully))
        })
    }

    override fun onImageClick(text: String) {

    }

    override fun onVideoPublishClick(text: String) {

    }

    override fun onPublish(fileType: String, uri: Uri, comment: String) {
        description = comment
        content = Content()
        if (fileType == FileType.VIDEO) {
            uploadImage(Uri.parse(Utils.getThumbnailPathForLocalFile(this, uri)), FileType.PROFILE_VIDEO_THUMBNAIL)
            uploadVideo(uri, fileType)
            postListAdapter?.updateUploadFileProgress(0)
            recyclerView.scrollToPosition(0)
        } else if (fileType == FileType.IMAGE) {
            uploadImage(uri, fileType)
            postListAdapter?.updateUploadFileProgress(0)
        }

    }

    private fun uploadVideo(fileUri: Uri, fileType: String) {
        val filePath = Utils.getRealPathFromURI(this, fileUri)
        val type = Utils.getMimeType(this, fileUri)
        FileUploadRequester(this, filePath, "video/" + type, fileType).execute()
    }

    private fun uploadImage(fileUri: Uri, fileType: String) {

        val filePath = Utils.getRealPathFromURI(this,fileUri)
        val type = Utils.getMimeType(this, fileUri)
        FileUploadRequester(this, filePath, "image/" + type, fileType).execute()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(MentorzApplication.instance!!.prefs!!.isLogin()){
            getDataFromIntent();
        }else{
           // sendToLoginPage()
            showDialogueForLogin()
        }


    }
    private fun showDialogueForLogin(){
        DialogUtils.showDialog(this@MediaShareActivity,resources.getString(R.string.please_login),resources.getString(R.string.login_alert),resources.getString(R.string.ok),this@MediaShareActivity)
    }

    private fun sendToLoginPage() {
        startActivity(Intent(this@MediaShareActivity,SplashActivity::class.java))
        this@MediaShareActivity.finish()
    }


    fun getDataFromIntent() {
        val action = intent.action
        val type = intent.type
        val uri = intent.extras.getParcelable<Parcelable>(Intent.EXTRA_STREAM)
        requestDialogue(action, type, uri as Uri);
    }

    fun requestDialogue(action: String, type: String, uri: Uri) {

        if (type != null) {
            DialogUtils.showPublishPostDialogWithOption(FileType.IMAGE, this, uri, this, "", this).show()
        }
    }

    fun myToast(context: Context,msg:String) {
        context.toast(""+msg)
    }


}