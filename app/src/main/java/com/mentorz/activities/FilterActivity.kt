package com.mentorz.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mentorz.R
import com.mentorz.customviews.RangeSeekBar
import com.mentorz.customviews.RangeSeekBarDisable
import com.mentorz.interest.InterestsItem
import com.mentorz.match.FilterExpertiseActivity
import com.mentorz.match.MyMentorRequest
import com.mentorz.model.InterestIds
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.utils.Global
import com.plumillonforge.android.chipview.ChipViewAdapter
import kotlinx.android.synthetic.main.activity_filter.*


class FilterActivity : BaseActivity() {

    private var interestList:MutableList<InterestsItem>?=null
    private val INTEREST_REQUEST_CODE=100
    private var ageSeekbar:RangeSeekBar<Int>?=null

    companion object {
         var minExp=1
         var maxExp=50
         var minPrice=0
         var maxPrice=200
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        chipView.adapter = MyChipViewAdapter(this)
        chipView.chipLayoutRes = R.layout.layout_chip
        chipView.chipBackgroundColor = resources.getColor(R.color.colorAccent)
        chipView.chipBackgroundColorSelected = resources.getColor(R.color.white)
        interestList= mutableListOf()
        if(Global.filteredExpertise!!.isEmpty()) {
            interestList!!.addAll(getTrimList(Global.userInterests!!))
            addMore(Global.userInterests!!)
        }
        else{
            interestList!!.addAll(getTrimList(Global.filteredExpertise!!))
            addMore(Global.filteredExpertise!!)
        }

        chipView.chipList = interestList!!.toList()

        initExperienceRangeBar()
        initRatingRangeBar()
        submit.setOnClickListener {
            var intent =Intent()
            intent.putExtra("mentor_request",getMentorRequest())
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

    }

    fun getMentorRequest(): MyMentorRequest {
        val mentorRequest = MyMentorRequest()
        val myInterestList = mutableListOf<InterestsItem>()
        if(Global.filteredExpertise!!.isEmpty()) {
            myInterestList.addAll(Global.userInterests!!)

        }
        else{
            myInterestList.addAll(Global.filteredExpertise!!)
        }
        val list = mutableListOf<InterestIds>()
        for (item in myInterestList) {
            val ids = InterestIds()
            ids.interestId = item.interestId
            ids.parentId = item.parentId
            list.add(ids)
        }
        mentorRequest.interests = list
        minExp =ageSeekbar?.selectedMinValue!!
        maxExp =ageSeekbar?.selectedMaxValue!!
        mentorRequest.maxExp = maxExp
        mentorRequest.minExp = minExp
        return mentorRequest
    }
    fun getTrimList(list: MutableList<InterestsItem>):MutableList<InterestsItem>{
        if(list.size>6){
           return  list.subList(0,6)
        }
        else{
            return list
        }
    }
    var previousThumb=0
    private fun addMore(list:MutableList<InterestsItem>){
        val item : InterestsItem =InterestsItem()
        if(list!!.size>6){
            item.interest= "+ ${(list!!.size-6)} "+getString(R.string.more)
        }
        else{
            item.interest=getString(R.string.add_more)
        }
        interestList!!.add(item)
    }
    private fun initExperienceRangeBar() {
        ageSeekbar = RangeSeekBar(1, 50, this)
        ageSeekbar?.selectedMinValue = minExp
        ageSeekbar?.selectedMaxValue = maxExp
        txtMinExp.text = "$minExp yr"
        txtMaxExp.text = "$maxExp yr"

        ageSeekbar?.setOnRangeSeekBarChangeListener(object :RangeSeekBar.OnRangeSeekBarChangeListener<Int> {
            override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<*>?, minValue: Int?, maxValue: Int?) {

                var maxValue = maxValue
                var minValue =minValue
                if (maxValue!! - minValue!! < 10) {
                    if (bar?.pressedThumb == -1) {
                        minValue = maxValue - 10
                        bar?.setSelectedMinValue()
                        previousThumb = -1
                    } else if (bar?.pressedThumb == 1) {
                        maxValue = minValue + 10
                        if (maxValue > 50)
                            maxValue = 50
                        bar?.setSelectedMaxValue()
                        previousThumb = 1

                    } else {
                        if (previousThumb == -1) {
                            minValue = maxValue - 10
                            bar?.setSelectedMinValue()
                        } else {
                            maxValue = minValue + 10
                            bar?.setSelectedMaxValue()
                        }
                    }
                }
                txtMinExp.text = "${minValue}yr"
                txtMaxExp.text = "${maxValue}yr"



            }
        })
        rSeekbarExp.addView(ageSeekbar)
    }
    private fun initRatingRangeBar() {
        val ratingSeekbar = RangeSeekBarDisable(0, 200, this)
        ratingSeekbar.isEnabled=false
        ratingSeekbar.selectedMinValue = minPrice
        ratingSeekbar.selectedMaxValue = maxPrice
        txtMinRating.text = "free"
        txtMaxRating.text = "$200"


        ratingSeekbar.setOnRangeSeekBarChangeListener(object :RangeSeekBarDisable.OnRangeSeekBarChangeListener<Int> {
            override fun onRangeSeekBarValuesChanged(bar: RangeSeekBarDisable<*>?, minValue: Int?, maxValue: Int?) {

                var maxValue = maxValue
                var minValue =minValue
                if (maxValue!! - minValue!! < 4) {
                    if (bar?.getPressedThumb() === -1) {
                        minValue = maxValue - 4
                        bar?.setSelectedMinValue()
                        previousThumb = -1
                    } else if (bar?.getPressedThumb() === 1) {
                        maxValue = minValue + 4
                        if (maxValue > 200)
                            maxValue = 200
                        bar?.setSelectedMaxValue()
                        previousThumb = 1

                    } else {
                        if (previousThumb == -1) {
                            minValue = maxValue - 4
                            bar?.setSelectedMinValue()
                        } else {
                            maxValue = minValue + 4
                            bar?.setSelectedMaxValue()
                        }
                    }
                }
                txtMinRating.text = "$$minValue"
                txtMaxRating.text = "$$maxValue"


            }
        })
        rSeekbarRating.addView(ratingSeekbar)
    }


    inner class MyChipViewAdapter(context: Context) : ChipViewAdapter(context) {

        override fun getBackgroundRes(position: Int): Int {
            return 0

        }

        override fun onLayout(view: View?, position: Int) {
            val tagItem = getChip(position) as InterestsItem
            val container = view?.findViewById<View>(android.R.id.content) as FrameLayout
            val imgTick = view.findViewById<View>(R.id.imgTick) as ImageView
            val txtValue = view.findViewById<TextView>(android.R.id.text1) as TextView
            imgTick.setBackgroundResource(R.drawable.selected_tick)
            container.setBackgroundResource(R.drawable.white_rounded_corner)
            txtValue.setTextColor(getColor(R.color.black))
            if(tagItem.interest!!.contains(getString(R.string.more))){
                imgTick.setBackgroundResource(0)
                container.setBackgroundResource(android.R.color.transparent)
                txtValue.paintFlags = txtValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
            (view.findViewById<View>(android.R.id.content) as FrameLayout).setOnClickListener {
                if(tagItem.interest!!.contains(getString(R.string.more))){
                   val intent = Intent(this@FilterActivity, FilterExpertiseActivity::class.java)
                    startActivityForResult(intent,INTEREST_REQUEST_CODE)
                }

            }
        }



        override fun getLayoutRes(position: Int): Int {
            return 0
        }

        override fun getBackgroundColorSelected(position: Int): Int {
            return 0
        }

        override fun getBackgroundColor(position: Int): Int {
            return 0
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            INTEREST_REQUEST_CODE->{
                if(resultCode== Activity.RESULT_OK) {
                    interestList!!.clear()
                    interestList!!.addAll(getTrimList(Global.filteredExpertise!!.toMutableList()))
                    interestList!!.sortBy {
                        it.interest
                    }
                    addMore(Global.filteredExpertise!!)
                    chipView.chipList = interestList!!.toList()
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
