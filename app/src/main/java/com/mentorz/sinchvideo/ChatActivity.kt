package com.mentorz.sinchvideo

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.controller.Controller
import com.mentorz.customviews.ChooseImageDialog
import com.mentorz.customviews.UploadVideoDialog
import com.mentorz.database.DbManager
import com.mentorz.manager.PubNubManager
import com.mentorz.manager.WrapContentLinearLayoutManager
import com.mentorz.match.UserProfile
import com.mentorz.messages.adapter.ChatListAdapter
import com.mentorz.messages.adapter.ChatListDelegateAdapter
import com.mentorz.model.MessageType
import com.mentorz.model.NotificationType
import com.mentorz.pubnub.*
import com.mentorz.stories.Content
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import com.mentorz.utils.Constant
import com.mentorz.utils.DateUtils
import com.mentorz.utils.Utils
import io.vrinda.kotlinpermissions.PermissionCallBack
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_comment.view.*


import org.json.JSONObject
import java.io.File
import java.util.*




class ChatActivity : BaseActivity(), View.OnClickListener, ChatListDelegateAdapter.onViewSelectedListener, ChooseImageDialog.ChooseImageDialogListener, FileUploadListener {
    var description: String? = null
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

            addImageMessage(imageUri, content!!.lresId)

        }

    }

    override fun fileUploadFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCancelClickListener() {
        chooseImageDialog?.dismiss()
    }

    override fun onGalleryClickListener() {
        val pickPhoto = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE)
    }

    override fun onCameraClickListener() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> {
                finish()
            }
            placeVideoCall.id -> {
                requestRuntimePermission()
            }
            location.id -> {
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()

            }
            upload.id -> {
                requestRuntimePermissionForImage()


            }
            imgSend.id -> {
                if (!edtMessage.text.toString().trim().isEmpty()) {
                    val packet = StreamChatPacket()

                    /*data for Android  Push*/
                    packet.pn_gcm = PnGcm()
                    packet.pn_gcm?.data = Data()
                    packet.pn_gcm?.data?.senderName = MentorzApplication.instance?.prefs?.getUserName()
                    packet.pn_gcm?.data?.sound = "default"
                    packet.pn_gcm?.data?.badge = 1
                    packet.pn_gcm?.data?.contentAvailable = 1
                    packet.pn_gcm?.data?.senderId = MentorzApplication.instance?.prefs?.getUserId()
                    packet.pn_gcm?.data?.senderLres = MentorzApplication.instance?.prefs?.getProfilePictureLres()
                    packet.pn_gcm?.data?.pushType = 0
                    packet.pn_gcm?.data?.userType = 1
                    packet.pn_gcm?.data?.senderSummary = "hello"
                   /*data for IOS Push*/

                    packet.pn_apns = PnAPNS()
                    packet.pn_apns?.aps = Aps()
                    packet.pn_apns?.aps?.senderName = MentorzApplication.instance?.prefs?.getUserName()
                    packet.pn_apns?.aps?.sound = "default"
                    packet.pn_apns?.aps?.badge = 1
                    packet.pn_apns?.aps?.contentAvailable = 1
                    packet.pn_apns?.aps?.senderId = MentorzApplication.instance?.prefs?.getUserId()
                    packet.pn_apns?.aps?.senderLres = MentorzApplication.instance?.prefs?.getProfilePictureLres()
                    packet.pn_apns?.aps?.pushType = 0
                    packet.pn_apns?.aps?.userType = 1
                    val alert: String = MentorzApplication.instance?.prefs?.getUserName() + ": " + edtMessage.text.toString().trim()
                    packet.pn_apns?.aps?.alert = alert
                    packet.pn_gcm?.data?.alert = alert
                    packet.body = edtMessage.text.toString().trim()
                    packet.isSent = false
                    packet.senderDisplayName = MentorzApplication.instance?.prefs?.getUserName()
                    packet.type = 0
                    packet.messageId = MentorzApplication.instance?.prefs?.getUserId().toString() + "-" + DateUtils.getCurrentTimeStamp().toString()
                    packet.isDelivered = false
                    packet.isRead = false
                    if (MentorzApplication.instance?.prefs?.getUserId()!! > friendId) {
                        packet.chatId = (MentorzApplication.instance?.prefs?.getUserId().toString() + friendId.toString()).toLong()
                    } else {
                        packet.chatId = (friendId.toString() + MentorzApplication.instance?.prefs?.getUserId().toString()).toLong()
                    }

                    packet.senderId = MentorzApplication.instance?.prefs?.getUserId()
                    packet.timestamp = DateUtils.changeTimeStampFormatForIOS(DateUtils.getCurrentTimeStamp().toString())
                 PubNubManagerService.instance.publishMessage(edtMessage,this@ChatActivity,packet, friendId.toString(), getPayLoad(packet))
                }
            }

        }
    }
    fun refreshAdapterOnSuccesfullyMessageSent(packet : StreamChatPacket){
        DbManager.getInstance(this@ChatActivity).insertChatMassage(packet)
        chatListAdapter?.addSendMessage(packet)
        recyclerView?.scrollToPosition(chatListAdapter?.itemCount!!.minus(1))
        edtMessage.text.clear()

    }

    fun getPayLoad(streamChatPacket: StreamChatPacket): JSONObject {
        val jsonObject = JSONObject()
        val childJsonObject = JSONObject()
        childJsonObject.put("senderName", MentorzApplication.instance?.prefs?.getUserName())
        childJsonObject.put("sound", "default")
        childJsonObject.put("badge", 1)
        childJsonObject.put("content-available", 1)
        childJsonObject.put("senderId", MentorzApplication.instance?.prefs?.getUserId())
        childJsonObject.put("sender_lres", MentorzApplication.instance?.prefs?.getProfilePictureLres())
        childJsonObject.put("PushType", 0)
        childJsonObject.put("UserType", 1)
        val alert: String = MentorzApplication.instance?.prefs?.getUserName() + ": Tgg"
        childJsonObject.put("alert", alert)
        jsonObject.put("pn_apns", JSONObject().put("aps", childJsonObject))
        return jsonObject
    }

    private var friendId: Long = 0
    private var useName: String= ""
    var userProfile : UserProfile? = null
    var senderLres :String? =""
    private var chatListAdapter: ChatListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        startService(Intent(applicationContext, PubNubManagerService::class.java))
        friendId = intent.getLongExtra("user_id", 0)
        useName = intent.getStringExtra("user_name")
        senderLres = intent.getStringExtra("user_lres")
        txtTitle.text = useName
        imgBack.setOnClickListener(this)
        placeVideoCall.setOnClickListener(this)
        location.setOnClickListener(this)
        upload.setOnClickListener(this)
        imgSend.setOnClickListener(this)
       // getSenderLres()
        initAdapter(senderLres)
        getDataFormDb()


    }

 /*   fun getSenderLres(){
try {
    userProfile = intent.getParcelableExtra("profile_data")
    senderLres = userProfile?.hresId
}catch (e:Exception){
    Log.e("ChatActivity",""+e.toString())
}
    }*/
    fun initAdapter(senderLres:String?) {
        recyclerView.apply {
            setHasFixedSize(true)
            val linearLayout = WrapContentLinearLayoutManager(this@ChatActivity)
            linearLayout.stackFromEnd = true
            layoutManager = linearLayout
        }
        chatListAdapter = ChatListAdapter(this,senderLres)
        recyclerView.adapter = chatListAdapter
        chatListAdapter?.registerAdapterDataObserver(emptyMessage)

    }

    var chatId: Long = 0
    fun getDataFormDb() {
        val dbManager = DbManager.getInstance(this)
        if (MentorzApplication.instance?.prefs?.getUserId()!! > friendId) {
            chatId = (MentorzApplication.instance?.prefs?.getUserId().toString() + friendId.toString()).toLong()
        } else {
            chatId = (friendId.toString() + MentorzApplication.instance?.prefs?.getUserId().toString()).toLong()
        }
        chatListAdapter?.notifyList(dbManager.getChatMessages(chatId))
        recyclerView?.scrollToPosition(chatListAdapter?.itemCount!!.minus(1))

    }

    fun requestRuntimePermission() {
        val permissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Call permissions", "Granted")
                requestPermissions(permissions[1], object : PermissionCallBack {
                    override fun permissionGranted() {
                        super.permissionGranted()
                        Log.v("Call permissions", "Granted")
                        placeCallActivity()
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

    fun placeCallActivity() {
        if (!sinchServiceInterface.isStarted) {
            sinchServiceInterface.startClient(MentorzApplication.instance?.prefs?.getUserId().toString())
        } else {
            val call = sinchServiceInterface.callUserVideo(friendId.toString())
            val callId = call.callId
            val callScreen = Intent(this, CallScreenActivity::class.java)
            callScreen.putExtra(SinchService.CALL_ID, callId)
            startActivity(callScreen)

        }

    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constant.ACTION_PUB_NUB_MESSAGE_RECEIVED)
        intentFilter.addAction(Constant.ACTION_SENDING_CHAT_MESSAGE_STATUS)
        registerReceiver(receiver, intentFilter)


    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.action) {
                Constant.ACTION_PUB_NUB_MESSAGE_RECEIVED -> {
                    val streamChatPacket = intent.getParcelableExtra<StreamChatPacket>("packet")
                    val aps_intent = intent.getParcelableExtra<Aps>("aps")
                    var pnApns = PnAPNS()
                    pnApns.aps= aps_intent
                    streamChatPacket.pn_apns=pnApns
                    if (streamChatPacket.chatId == chatId) {
                        chatListAdapter?.addReceiveMessage(streamChatPacket)
                        recyclerView?.scrollToPosition(chatListAdapter?.itemCount!!.minus(1))
                    }
                }
                Constant.ACTION_SENDING_CHAT_MESSAGE_STATUS -> {
                    val streamChatPacket = intent.getParcelableExtra<StreamChatPacket>("packet")
                    val data_intent = intent.getParcelableExtra<Data>("data")
                    var pnGcm = PnGcm()
                    pnGcm.data= data_intent
                    streamChatPacket.pn_gcm=pnGcm
                    if(intent.getBooleanExtra("is_message_sent",false)){
                        refreshAdapterOnSuccesfullyMessageSent(streamChatPacket)
                    }

                }
            }
        }

    }

    override fun onResume() {
        Controller.chatId=chatId
        DbManager.getInstance(this).setReadAllChatMessage(friendId)
        getHistoryFromPubNub()
        super.onResume()

    }

    override fun onPause() {
        Controller.chatId=0

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getHistoryFromPubNub(){

       // DbManager.getInstance(this).getLastTimestempOfMessageFromChat()

       // PubNubManagerService.instance.punNubMessageHistory(minDate,lastTime,friendId.toString())

    }

    fun requestRuntimePermissionForImage() {
        val permissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

        requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Storage permissions", "Granted")
                requestPermissions(permissions[1], object : PermissionCallBack {
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

    val CAMERA_REQUEST_CODE = 101
    val GALLERY_REQUEST_CODE = 102
    val CROP_PIC_REQUEST_CODE = 103
    val TAKE_VIDEO_REQUEST_CODE = 104
    val CHOOSE_VIDEO_REQUEST_CODE = 105
    var imageUri: Uri? = null

    var chooseImageDialog: ChooseImageDialog? = null
    fun chooseImage() {
        chooseImageDialog = ChooseImageDialog(this@ChatActivity, this)
        chooseImageDialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap
                    val path = MediaStore.Images.Media.insertImage(this.contentResolver, imageBitmap, "image", null)
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
                        imageUri = uri
                        onUploadAndPublishMediaFile(FileType.IMAGE, imageUri!!)

                    }
                }
            }
            CHOOSE_VIDEO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = Uri.parse(data?.data.toString())
                  //  profileVideoUri = uri
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    cursor.close()
                    val bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND)
                   // videoImage.visibility = View.VISIBLE
                   // videoUploadDialog = UploadVideoDialog(this, bitmap, this)
                  //  videoThumbnail = bitmap
                 //   videoUploadDialog?.show()
                }
            }
            TAKE_VIDEO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = Uri.parse(data?.data.toString())
                 //   profileVideoUri = uri
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    cursor.close()
                    val bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND)
                    //videoImage.visibility = View.VISIBLE
                 //   videoUploadDialog = UploadVideoDialog(this, bitmap, this)
                  //  videoUploadDialog?.show()
                   // videoThumbnail = bitmap
                }

            }

        }
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

    fun addImageMessage(uri :Uri?,lres:String?){

        val packet = StreamChatPacket()

        /*data for Android  Push*/
        packet.pn_gcm = PnGcm()
        packet.pn_gcm?.data = Data()
        packet.pn_gcm?.data?.senderName = MentorzApplication.instance?.prefs?.getUserName()
        packet.pn_gcm?.data?.sound = "default"
        packet.pn_gcm?.data?.badge = 1
        packet.pn_gcm?.data?.contentAvailable = 1
        packet.pn_gcm?.data?.senderId = MentorzApplication.instance?.prefs?.getUserId()
        packet.pn_gcm?.data?.senderLres = lres
        packet.pn_gcm?.data?.pushType = 0
        packet.pn_gcm?.data?.userType = 1
        packet.pn_gcm?.data?.senderSummary = "hello"
        packet.pn_gcm?.data?.uri = uri.toString()
        /*data for IOS Push*/

        packet.pn_apns = PnAPNS()
        packet.pn_apns?.aps = Aps()
        packet.pn_apns?.aps?.senderName = MentorzApplication.instance?.prefs?.getUserName()
        packet.pn_apns?.aps?.sound = "default"
        packet.pn_apns?.aps?.badge = 1
        packet.pn_apns?.aps?.contentAvailable = 1
        packet.pn_apns?.aps?.senderId = MentorzApplication.instance?.prefs?.getUserId()
        packet.pn_apns?.aps?.senderLres = MentorzApplication.instance?.prefs?.getProfilePictureLres()
        packet.pn_apns?.aps?.pushType = 0
        packet.pn_apns?.aps?.userType = 1
        val alert: String = MentorzApplication.instance?.prefs?.getUserName() + ": " + edtMessage.text.toString().trim()
        packet.pn_apns?.aps?.alert = alert
        packet.pn_gcm?.data?.alert = alert
        packet.body = edtMessage.text.toString().trim()
        packet.isSent = false
        packet.senderDisplayName = MentorzApplication.instance?.prefs?.getUserName()
        packet.type = 0
        packet.messageId = MentorzApplication.instance?.prefs?.getUserId().toString() + "-" + DateUtils.getCurrentTimeStamp().toString()
        packet.isDelivered = false
        packet.isRead = false
        if (MentorzApplication.instance?.prefs?.getUserId()!! > friendId) {
            packet.chatId = (MentorzApplication.instance?.prefs?.getUserId().toString() + friendId.toString()).toLong()
        } else {
            packet.chatId = (friendId.toString() + MentorzApplication.instance?.prefs?.getUserId().toString()).toLong()
        }
        packet.senderId = MentorzApplication.instance?.prefs?.getUserId()
        packet.timestamp = DateUtils.getCurrentTimeStamp().toString()
        PubNubManagerService.instance.publishMessage(edtMessage,this@ChatActivity,packet, friendId.toString(), getPayLoad(packet))
    }

    var content: Content? = null
   fun onUploadAndPublishMediaFile(fileType: String, uri: Uri) {
        content = Content()
        if (fileType == FileType.VIDEO) {
            uploadImage(Uri.parse(Utils.getThumbnailPathForLocalFile(this, uri)), FileType.PROFILE_VIDEO_THUMBNAIL)
            uploadVideo(uri, fileType)
        } else if (fileType == FileType.IMAGE) {
            uploadImage(uri, fileType)
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
}
