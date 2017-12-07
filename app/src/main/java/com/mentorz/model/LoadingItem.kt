package com.mentorz.model

import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

/**
 * Created by umesh on 26/08/17.
 */
class LoadingItem: ViewType {
    override fun getViewType() = AdapterConstants.LOADING
}