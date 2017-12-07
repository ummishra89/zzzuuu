package com.mentorz.model

import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

/**
 * Created by umesh on 17/08/17.
 */
class UploadItem(var progress:Int=0) :ViewType{
    override fun getViewType(): Int {
       return AdapterConstants.PROGRESS
    }

}