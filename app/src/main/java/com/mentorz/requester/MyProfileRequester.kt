package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.ProfileData
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.retrofit.listeners.UserProfileListener
import com.mentorz.uploadfile.FileType
import com.mentorz.utils.EncodingDecoding
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection


/**
 * Created by aMAN GUPTA on 22/7/17.
 */
class MyProfileRequester(listener: UserProfileListener) : BaseRequester(), SignedUrlListener {

    var listener: UserProfileListener? = listener

    override fun signedUrlSuccess(fileType: String, url: String, model: Any?) {
        when (fileType) {
                FileType.PROFILE_PICTURE -> {
                      MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_LRES, url)
          }
        FileType.PROFILE_VIDEO_THUMBNAIL -> {
            MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_LRES, url)
        }
        FileType.PROFILE_VIDEO -> {
            MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_HRES, url)
        }
    }
        listener?.userProfileSuccess(model as ProfileData)
    }
    override fun onSessionExpired() {
    }
    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            mentorzApiResponse = HttpController.getUserProfile()
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var userListItem: ProfileData? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            userListItem = MentorzApplication.instance?.gson?.fromJson<ProfileData>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), ProfileData::class.java)
            if (userListItem != null) {
                getMyProfileImage(userListItem)

            } else {
                listener?.userProfileFail()
            }
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else{
            listener?.userProfileFail()
        }
    }

    fun getMyProfileImage(userListItem: ProfileData){
        SignedUrlRequester(FileType.PROFILE_PICTURE,this,userListItem,userListItem.userProfile?.lresId).execute()
        SignedUrlRequester(FileType.PROFILE_VIDEO_THUMBNAIL,this,userListItem,userListItem.userProfile?.videoBioHres).execute()
        SignedUrlRequester(FileType.PROFILE_VIDEO,this,userListItem,userListItem.userProfile?.videoBioLres).execute()
    }

}