package com.mentorz.utils

import com.mentorz.expertise.ExpertiseItem
import com.mentorz.interest.InterestsItem

/**
 * Created by umesh on 25/07/17.
 */

class Global {
    companion object {
        var defaultInterestsMap: MutableMap<Int, MutableList<InterestsItem>>? = hashMapOf()


        var defaultExpertiseMap: MutableMap<Int, MutableList<ExpertiseItem>>? = hashMapOf()
        var userInterests: MutableList<InterestsItem>? = mutableListOf()
        var defaultFilteredInterestsMap: MutableMap<Int, MutableList<InterestsItem>>? = hashMapOf()
        var filteredInterests: MutableList<InterestsItem>? = mutableListOf()

        var myexpertises: MutableList<ExpertiseItem>? = mutableListOf()
        var tempExpertiseMap: MutableMap<Int, MutableList<ExpertiseItem>>? = hashMapOf()

        var defaultFilteredExpertiseMap: MutableMap<Int, MutableList<InterestsItem>>? = hashMapOf()
        var filteredExpertise: MutableList<InterestsItem>? = mutableListOf()


    }
}