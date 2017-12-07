package com.mentorz.requester

import com.google.gson.reflect.TypeToken
import com.mentorz.MentorzApplication
import com.mentorz.listener.BlockUserListener
import com.mentorz.match.UserListItem
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetBlockedUserRequester(private var pageNo: Int, private var presenter: Any?, private var listener: BlockUserListener?) : BaseRequester() {


    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getBlockedUsers(pageNo)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var blockedUserList: MutableList<UserListItem>? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            blockedUserList = MentorzApplication.instance?.gson?.fromJson<MutableList<UserListItem>>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), object : TypeToken<ArrayList<UserListItem>>() {}.type)
            if (blockedUserList != null) {
                listener?.onGetBlockedUserSuccess(blockedUserList)
            }

        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
           listener?.blockedUserNotFound()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onGetBlockedUserFail()
        }
    }


}