package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.ChatUserResponseListener
import com.mentorz.match.MentorResponse
import com.mentorz.match.UserListItem
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetChatUserRequester(private var friendId: Long, private var pageNo: Int, private var listener: ChatUserResponseListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val list:MutableList<UserListItem> = mutableListOf()
            val mentorList =getMentorList()
            val menteeList =getMenteeList()
            if(mentorList!=null){
               list.addAll(mentorList)
            }
            if(menteeList!=null){
                list.addAll(menteeList)
            }
            if(!list.isEmpty()){
                listener?.chatUserListSuccess( list.distinct() as MutableList)
            }
            else{
                listener?.noChatUserFound()
            }

        }
    }



    fun getMentorList():MutableList<UserListItem>?{
        val mentorzApiResponse: MentorzApiResponse? = HttpController.getMentorList(friendId,pageNo)

        fun callBack(mentorzApiResponse: MentorzApiResponse?):MutableList<UserListItem>? {
            var mentorResponse:MentorResponse?=null
            if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
                mentorResponse = MentorzApplication.instance?.gson?.fromJson<MentorResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), MentorResponse::class.java)
                if (mentorResponse != null) {
                    return  mentorResponse.userList as MutableList<UserListItem>
                }
            }
            else if(mentorzApiResponse?.statusCode==0){
                return null
            }
            return null
        }

        if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
            return  callBack(mentorzApiResponse)
        }
        return null
    }

    fun getMenteeList():MutableList<UserListItem>?{
        val mentorzApiResponse: MentorzApiResponse? = HttpController.getMenteeList(friendId,pageNo)

         fun callBack(mentorzApiResponse: MentorzApiResponse?):MutableList<UserListItem>? {
            var mentorResponse:MentorResponse?=null
            if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
                mentorResponse = MentorzApplication.instance?.gson?.fromJson<MentorResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), MentorResponse::class.java)
                if (mentorResponse != null) {
                   return  mentorResponse.userList as MutableList<UserListItem>
                }
            }
            else if(mentorzApiResponse?.statusCode==0) {
                listener?.onNetworkFail()
            }
             return null
        }

        if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
           return  callBack(mentorzApiResponse)
        }
        return null
    }

}