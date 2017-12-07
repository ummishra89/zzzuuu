package com.mentorz.activities.values

import com.mentorz.model.ValuesItem

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
interface ValuesView {
    fun showProgressBar()
    fun hideProgressBar()
    fun setValuesAdapter(values: List<ValuesItem>?)
    fun onUpdateValuesSuccess()
    fun onUpdateValuesFail()
    fun showEmptyValuesAlert()
    fun onSessionExpired()
    fun networkError()

}