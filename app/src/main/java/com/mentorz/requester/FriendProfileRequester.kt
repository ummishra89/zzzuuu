package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.ProfileData
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UserProfileListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 22/7/17.
 */
class FriendProfileRequester(var friendId: Long, listener: UserProfileListener) : BaseRequester() {

    var listener: UserProfileListener? = listener

    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            if (friendId != MentorzApplication.instance?.prefs?.getUserId()) {
                mentorzApiResponse = HttpController.getFriendProfile(friendId)
            } else {
                mentorzApiResponse = HttpController.getUserProfile()
            }
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
                listener?.userProfileSuccess(userListItem)

            } else {
                listener?.userProfileFail()
            }
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_PRECON_FAILED) {
            MentorzApplication.instance?.blockUser(friendId)
        } else if (mentorzApiResponse?.statusCode == 0) {
            listener?.onNetworkFail()
        } else {
            listener?.userProfileFail()
        }
    }


}