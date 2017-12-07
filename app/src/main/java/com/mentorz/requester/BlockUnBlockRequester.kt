package com.mentorz.requester

import com.mentorz.listener.BlockUserListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class BlockUnBlockRequester(private var isBlock: Boolean, private var mentorId: Long, private var presenter: Any?, private var listener: BlockUserListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse?=null
            if(isBlock){
                mentorzApiResponse= HttpController.blockUser(mentorId)
            }
            else{
                mentorzApiResponse= HttpController.unBlockUser(mentorId)
            }

            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            if(isBlock){
               listener?.onBlockUserSuccess(mentorId)
            }
            else{
               listener?.onUnBlockUserSuccess()
            }
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            if(isBlock){
                listener?.onBlockUserFail()
            }
            else{
                listener?.onUnBlockUserFail()
            }
        }
    }


}