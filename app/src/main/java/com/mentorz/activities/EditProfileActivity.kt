package com.mentorz.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.customviews.ChooseImageDialog
import com.mentorz.customviews.UploadVideoDialog
import com.mentorz.extensions.showSnackBar
import com.mentorz.model.UpdateProfileRequest
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.requester.UpdateProfileRequester
import com.mentorz.retrofit.listeners.UpdateProfileListener
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import com.mentorz.utils.Constant
import com.mentorz.utils.DateUtils
import com.mentorz.utils.Prefs
import com.mentorz.utils.Utils
import com.squareup.picasso.Picasso
import io.vrinda.kotlinpermissions.PermissionCallBack
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File

class EditProfileActivity : BaseActivity(), View.OnClickListener, ChooseImageDialog.ChooseImageDialogListener, UpdateProfileListener, UploadVideoDialog.UploadVideoListener, FileUploadListener {
    override fun onNetworkFail() {
        super.networkError()
    }

    override fun onSessionExpired() {
        runOnUiThread {

        }
    }

    override fun onCancelListener() {
        videoUploadDialog?.dismiss()
    }

    override fun onUploadListener(bitmap: Bitmap) {
//        val path = MediaStore.Images.Media.insertImage(this.contentResolver, bitmap, "image", null)
//        val uri = Uri.parse(path)
        uploadImage(Uri.parse(Utils.getThumbnailPathForLocalFile(this@EditProfileActivity, profileVideoUri!!)), FileType.PROFILE_VIDEO_THUMBNAIL)
    }

    override fun updateProfileSuccess() {
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.USER_NAME, et_user_name.text.toString().trim())
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.BASIC_INFO, et_headline.text.toString().trim())
        setResult(Activity.RESULT_OK, Intent())

        val intent = Intent(Constant.ACTION_UPDATE_USER_PROFILE)
        sendBroadcast(intent)
        finish()
    }

    override fun updateProfileFail() {
        showSnackBar(root, getString(R.string.unable_to_connect))
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
            imgBack.id -> finish()
            txtDone.id -> {

                if (TextUtils.isEmpty(et_user_name.text.toString().trim())) {
                    showSnackBar(root, getString(R.string.please_enter_your_name))
                } else if (TextUtils.isEmpty(et_headline.text.toString().trim())) {
                    showSnackBar(root, getString(R.string.please_enter_your_headline))
                } else if (TextUtils.isEmpty(et_occupation.text.toString().trim())) {
                    showSnackBar(root, getString(R.string.please_enter_your_titile_or_occupation))
                } else if (TextUtils.isEmpty(et_organization.text.toString().trim())) {
                    showSnackBar(root, getString(R.string.please_enter_the_orgination))
                } else if (TextUtils.isEmpty(et_location.text.toString().trim())) {
                    showSnackBar(root, getString(R.string.please_enter_your_current_location))
                } else if (TextUtils.isEmpty(experience.text.toString().trim())) {
                    showSnackBar(root, getString(R.string.please_enter_your_experience))
                } else {
                    MentorzApplication.instance?.prefs?.putString(Prefs.Key.OCCUPATION, et_occupation.text.toString().trim())
                    MentorzApplication.instance?.prefs?.putString(Prefs.Key.ORGANIZATION, et_organization.text.toString().trim())
                    MentorzApplication.instance?.prefs?.putString(Prefs.Key.LOCATION, et_location.text.toString().trim())
                    MentorzApplication.instance?.prefs?.putInt(Prefs.Key.EXPERIENCE, experience.text.toString().trim().toInt())
                    if (profilePictureUri != null) {
                        profilePictureProgress.visibility = View.VISIBLE
                        uploadImage(profilePictureUri!!, FileType.PROFILE_PICTURE)
                    } else {
                        updateProfileApi()
                    }
                }
            }
            iv_profile_picture.id -> {
                requestRuntimePermissionForImage()
            }
            imgVideo.id -> {
                requestRuntimePermissionForRecordVideo()
            }
            imgSelfie.id -> {
                requestRuntimePermissionForVideo()
            }
            iv_video.id -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(MentorzApplication.instance?.prefs?.getProfileVideoHres()), "video/mp4")
                startActivity(intent)
            }
        }
    }

    private fun updateProfileApi() {
        val updateProfileRequest = UpdateProfileRequest()
        updateProfileRequest.name = et_user_name.text.toString().trim()
        updateProfileRequest.basicInfo = et_headline.text.toString().trim()
        updateProfileRequest.hresId = profilePictureToken
        updateProfileRequest.lresId = profilePictureToken
        updateProfileRequest.videoBioLres = MentorzApplication.instance?.prefs?.getProfileVideoLres()
        updateProfileRequest.videoBioHres = MentorzApplication.instance?.prefs?.getProfileVideoHres()
        val updateProfileRequester = UpdateProfileRequester(this, this, updateProfileRequest)
        updateProfileRequester.execute()
    }

    fun requestRuntimePermissionForVideo() {
        val permissions: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Storage permissions", "Granted")
                chooseVideo()
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Storage permissions", "Denied")
            }
        })
    }

    private fun chooseVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, CHOOSE_VIDEO_REQUEST_CODE)
    }

    fun recordVideo() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().path + "videocapture.mp4")
        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST_CODE)
    }

    var chooseImageDialog: ChooseImageDialog? = null
    val CAMERA_REQUEST_CODE = 101
    val GALLERY_REQUEST_CODE = 102
    val CROP_PIC_REQUEST_CODE = 103
    val TAKE_VIDEO_REQUEST_CODE = 104
    val CHOOSE_VIDEO_REQUEST_CODE = 105
    var videoUploadDialog: UploadVideoDialog? = null
    var profilePictureUri: Uri? = null
    var profilePictureToken = ""
    var videoThumbnail: Bitmap? = null
    var profileVideoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar)
        txtTitle.text = getString(R.string.edit_profile)
        imgBack.setOnClickListener(this)
        txtDone.setOnClickListener(this)
        iv_profile_picture.setOnClickListener(this)
        imgSelfie.setOnClickListener(this)
        disableSeekBar.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(p0: View?) {
                // for disable seekbar
            }

        })
        imgVideo.setOnClickListener(this)
        if (!TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getProfilePictureLres()))
            Picasso.with(this@EditProfileActivity).load(MentorzApplication.instance?.prefs?.getProfilePictureLres()).resize(200, 200).centerCrop().into(iv_profile_picture)
        if (!TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getProfileVideoLres()) && !TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getProfileVideoHres())) {
            videoImage.visibility = View.VISIBLE
            Picasso.with(this@EditProfileActivity).load(MentorzApplication.instance?.prefs?.getProfileVideoLres()).resize(200, 200).centerCrop().into(iv_video)

        }
        et_user_name.setText(MentorzApplication.instance?.prefs?.getUserName())
        et_headline.setText(MentorzApplication.instance?.prefs?.getBasicInfo())
        et_occupation.setText(MentorzApplication.instance?.prefs?.getString(Prefs.Key.OCCUPATION, ""))
        et_organization.setText(MentorzApplication.instance?.prefs?.getString(Prefs.Key.ORGANIZATION, ""))
        et_location.setText(MentorzApplication.instance?.prefs?.getString(Prefs.Key.LOCATION, ""))
        if (MentorzApplication.instance?.prefs?.getInt(Prefs.Key.EXPERIENCE, -1) != -1) {
            experience.setText(MentorzApplication.instance?.prefs?.getInt(Prefs.Key.EXPERIENCE, 0).toString())
        }
        iv_video.setOnClickListener(this)
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
                    iv_profile_picture.setImageURI(uri)
                    if (uri != null) {
                        profilePictureUri = uri
                    }
                }
            }
            CHOOSE_VIDEO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = Uri.parse(data?.data.toString())
                    profileVideoUri = uri
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    cursor.close()
                    val bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND)
                    videoImage.visibility = View.VISIBLE
                    videoUploadDialog = UploadVideoDialog(this, bitmap, this)
                    videoThumbnail = bitmap
                    videoUploadDialog?.show()
                }
            }
            TAKE_VIDEO_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = Uri.parse(data?.data.toString())
                    profileVideoUri = uri
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    cursor.close()
                    val bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND)
                    videoImage.visibility = View.VISIBLE
                    videoUploadDialog = UploadVideoDialog(this, bitmap, this)
                    videoUploadDialog?.show()
                    videoThumbnail = bitmap
                }

            }

        }
    }

    fun requestRuntimePermissionForRecordVideo() {
        val permissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Call permissions", "Granted")
                requestPermissions(permissions[1], object : PermissionCallBack {
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

    fun chooseImage() {
        chooseImageDialog = ChooseImageDialog(this@EditProfileActivity, this)
        chooseImageDialog?.show()
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





    private fun uploadImage(fileUri: Uri, fileType: String) {
        val filePath = fileUri.path
        val type = Utils.getMimeType(this, fileUri)
        FileUploadRequester(this, filePath, "image/" + type, fileType).execute()
    }


    private fun uploadVideo(fileUri: Uri, fileType: String) {
        val filePath = Utils.getRealPathFromURI(this@EditProfileActivity,fileUri)
        val type = Utils.getMimeType(this, fileUri)
        FileUploadRequester(this, filePath, "video/" + type, fileType).execute()
    }



    override fun fileUploadSuccess(token: String, fileType: String) {
        runOnUiThread {
            when (fileType) {
                FileType.PROFILE_PICTURE -> {
                    profilePictureToken = token
                    profilePictureProgress.visibility = View.GONE
                    updateProfileApi()
                    SignedUrlRequester(FileType.PROFILE_PICTURE, null, null, token).execute()
                }
                FileType.PROFILE_VIDEO_THUMBNAIL -> {
                    SignedUrlRequester(FileType.PROFILE_VIDEO_THUMBNAIL, null, null, token).execute()
                    uploadVideo(profileVideoUri!!, FileType.PROFILE_VIDEO)
                }
                FileType.PROFILE_VIDEO -> {
                    SignedUrlRequester(FileType.PROFILE_VIDEO, null, null, token).execute()
                    videoUploadDialog?.dismiss()
                    iv_video.setImageBitmap(videoThumbnail)
                }

            }
        }
    }

    override fun fileUploadFail() {
        runOnUiThread {
            profilePictureProgress.visibility = View.GONE
            showSnackBar(root, getString(R.string.unable_to_connect))
            updateProfileApi()
        }
    }



}
