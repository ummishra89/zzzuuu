package com.mentorz.activities.values

import com.google.gson.annotations.SerializedName
import com.mentorz.model.ValuesItem
import com.mentorz.model.ValuesResponse
import com.mentorz.requester.DefaultValuesRequester
import com.mentorz.requester.UpdateValuesRequester
import com.mentorz.requester.UserValuesRequester
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.DefaultValuesListener
import com.mentorz.retrofit.listeners.UpdateValuesListener
import com.mentorz.retrofit.listeners.UserValuesLintener

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class ValuesPresenterImpl(view: ValuesView) : ValuesPresenter, DefaultValuesListener, UserValuesLintener, UpdateValuesListener {

    override fun onNetworkFail() {
        view.networkError()
    }
    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    override fun onUpdateValuesSuccess(mentorzApiResponse: MentorzApiResponse) {
        view.hideProgressBar()
        view.onUpdateValuesSuccess()
    }

    override fun onUpdateValuesFail() {
        view.hideProgressBar()
        view.onUpdateValuesFail()
    }

    override fun updateValues() {
        val list: List<ValuesItem>? = values?.filter {
            it.isMyValue!!
        }
        if (list?.isEmpty()!!) {
            view.showEmptyValuesAlert()
            return
        }

        view.showProgressBar()
        val idList = mutableListOf<UpdateValuesRequest.RequestItem>()
        list.forEach {
            idList.add(UpdateValuesRequest.RequestItem(it.valueId))
        }
        val updateValuesRequest = UpdateValuesRequest(idList)
        val updateValuesRequester = UpdateValuesRequester(updateValuesRequest, this, this)
        updateValuesRequester.execute()

    }

    data class UpdateValuesRequest(

            @field:SerializedName("values")
            var values: List<RequestItem>? = null

    ) {
        data class RequestItem(
                @field:SerializedName("value_id")
                var valueId: Int? = null)
    }

    override fun noUserValues() {
        view.hideProgressBar()
        view.setValuesAdapter(values)
    }

    override fun userValuesFail() {
        view.hideProgressBar()
    }

    override fun userValues(valuesResponse: ValuesResponse) {
        view.hideProgressBar()

        userValues = valuesResponse.values
        for (i in 0 until values?.size!!) {
            (0 until userValues?.size!!)
                    .filter { values?.get(i)?.valueId == userValues?.get(it)?.valueId }
                    .forEach { values?.get(i)?.isMyValue = true }
        }
        view.setValuesAdapter(values)

    }

    override fun defaultValuesSuccess(valuesResponse: ValuesResponse) {
        values = valuesResponse.values
        val userValuesRequester = UserValuesRequester(this, this)
        userValuesRequester.execute()

    }

    override fun defaultValuesFail() {
        view.hideProgressBar()

    }

    override fun getValues() {
        view.showProgressBar()
        val defaultValuesRequester = DefaultValuesRequester(this, this)
        defaultValuesRequester.execute()
    }

    val view = view
    var values: List<ValuesItem>? = null
    var userValues: MutableList<ValuesItem>? = null
}