package com.mentorz.requester

import com.mentorz.activities.values.ValuesPresenterImpl
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UpdateValuesListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class UpdateValuesRequester(updateValueRequest: ValuesPresenterImpl.UpdateValuesRequest, private var presenter: Any?, private var listener: UpdateValuesListener?) : BaseRequester() {
    private var updateValueRequest: ValuesPresenterImpl.UpdateValuesRequest? = updateValueRequest

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.updateValues(updateValueRequest)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onUpdateValuesSuccess(mentorzApiResponse)
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onUpdateValuesFail()
        }
    }

}