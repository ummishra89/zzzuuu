package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.ProfileImageResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.ProfileImageListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 08/08/17.
 */
class ProfileImageRequester(private var userId: Long?, private var listener: ProfileImageListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getProfileImage(userId!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var response: ProfileImageResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<ProfileImageResponse>(mentorzApiResponse.responseBody as String, ProfileImageResponse::class.java)
            if (response != null) {
                listener?.profileImageSuccess(response, userId)
            }

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.profileImageFail()
        }
    }


}