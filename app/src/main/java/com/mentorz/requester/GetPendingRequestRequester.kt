package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.GetPendingRequestResponseListener
import com.mentorz.messages.PendingRequestResponse
import com.mentorz.messages.RequestListItem
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetPendingRequestRequester(private var pageNo: Int, private var presenter: Any?, private var listener: GetPendingRequestResponseListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val list:MutableList<RequestListItem> = mutableListOf()
            val mentorList =getMentorRequestList()
            val menteeList =getPendingList()
            if(mentorList!=null){
                mentorList.forEach {
                    it.isMentorRequest = true
                }
                list.addAll(mentorList)
            }
            if(menteeList!=null){
                list.addAll(menteeList)
            }
            if(!list.isEmpty()){
                listener?.onGetPendingRequestSuccess(list)
            }
            else{
                listener?.pendingRequestNotFound()
            }
        }
    }

    fun getMentorRequestList():MutableList<RequestListItem>?{
        val mentorzApiResponse: MentorzApiResponse? = HttpController.getMentorRequests(pageNo)

        fun callBack(mentorzApiResponse: MentorzApiResponse?):MutableList<RequestListItem>? {
            var pendingRequestResponse: PendingRequestResponse?
            if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
                pendingRequestResponse = MentorzApplication.instance?.gson?.fromJson<PendingRequestResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), PendingRequestResponse::class.java)
                if (pendingRequestResponse != null) {
                    return  pendingRequestResponse.requestList as MutableList<RequestListItem>
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

    fun getPendingList():MutableList<RequestListItem>?{
        val mentorzApiResponse: MentorzApiResponse? = HttpController.getPendingRequest(pageNo)
        fun callBack(mentorzApiResponse: MentorzApiResponse?):MutableList<RequestListItem>? {
            var pendingRequestResponse: PendingRequestResponse?
            if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
                pendingRequestResponse = MentorzApplication.instance?.gson?.fromJson<PendingRequestResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), PendingRequestResponse::class.java)
                if (pendingRequestResponse != null) {
                    return  pendingRequestResponse.requestList as MutableList<RequestListItem>
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