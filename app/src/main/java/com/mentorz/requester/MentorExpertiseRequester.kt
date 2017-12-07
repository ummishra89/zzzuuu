package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.expertise.MentorExpertiseResponseListener
import com.mentorz.expertise.Response
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.Global
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 06/07/17.
 */
class MentorExpertiseRequester(private var mentorId: Long?, private var presenter: Any?, private var listener: MentorExpertiseResponseListener?) : BaseRequester() {


    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            mentorzApiResponse = HttpController.getMentorExpertise(mentorId!!)
            if (!isSessionExpired(listener, mentorzApiResponse)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    fun callBack(mentorzApiResponse: MentorzApiResponse?) {

        var response: Response? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<Response>(mentorzApiResponse.responseBody as String, Response::class.java)
            if (response != null) {
                if(mentorId==MentorzApplication.instance?.prefs?.getUserId()) {
                    Global.myexpertises = response.expertise
                    val tempList = mutableListOf<ExpertiseItem>()
                    tempList.addAll(response.expertise!!)
                    tempList
                            .filter { Global.myexpertises!!.contains(it) && it.hasChildren!! }
                            .forEach { removeAllChild(it) }
                }
                listener?.onMentorExpertiseSuccess(this.mentorId!!, response)
            }

        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onMentorExpertiseNotFound()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onMentorExpertiseFail()
        }
    }


    fun removeAllChild(interestItem: ExpertiseItem) {
        val list = Global.myexpertises!!.filter {
            it.parentId == interestItem.expertiseId
        }
        for (item in list) {
            if (item.hasChildren!!) {
                removeAllChild(item)
                Global.myexpertises!!.remove(item)
            } else {
                Global.myexpertises!!.remove(item)
            }
        }
    }

}