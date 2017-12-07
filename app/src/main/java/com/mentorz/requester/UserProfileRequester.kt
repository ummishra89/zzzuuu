package com.mentorz.requester

import android.text.TextUtils
import com.mentorz.MentorzApplication
import com.mentorz.model.GetProfileResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.uploadfile.FileType
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 22/7/17.
 */
class UserProfileRequester : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getUserProfile()
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var getProfileResponse: GetProfileResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            getProfileResponse = MentorzApplication.instance?.gson?.fromJson<GetProfileResponse>(mentorzApiResponse.responseBody as String, GetProfileResponse::class.java)
            if (getProfileResponse != null) {
                MentorzApplication.instance?.prefs?.putLong(Prefs.Key.USER_ID, getProfileResponse.userId)
                MentorzApplication.instance?.prefs?.putString(Prefs.Key.USER_NAME, getProfileResponse.userProfile?.name)
                MentorzApplication.instance?.prefs?.putString(Prefs.Key.BASIC_INFO, getProfileResponse.userProfile?.basicInfo)
                MentorzApplication.instance?.prefs?.putString(Prefs.Key.BIRTH_DATE, getProfileResponse.userProfile?.birthDate)
                if (!TextUtils.isEmpty(getProfileResponse.userProfile?.lresId) && !MentorzApplication.instance?.prefs?.isSocialLogin()!!) {
                    SignedUrlRequester(FileType.PROFILE_PICTURE, null, null, getProfileResponse.userProfile?.lresId).execute()
                }
                if (!TextUtils.isEmpty(getProfileResponse.userProfile?.videoBioLres) && !MentorzApplication.instance?.prefs?.isSocialLogin()!!) {
                    SignedUrlRequester(FileType.PROFILE_VIDEO_THUMBNAIL, null, null, getProfileResponse.userProfile?.videoBioLres).execute()
                }
                if (!TextUtils.isEmpty(getProfileResponse.userProfile?.videoBioHres) && !MentorzApplication.instance?.prefs?.isSocialLogin()!!) {
                    SignedUrlRequester(FileType.PROFILE_VIDEO, null, null, getProfileResponse.userProfile?.videoBioHres).execute()
                }
            }
        }
    }
}