package com.mentorz.interest


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.extensions.showToast
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.FragmentFactory
import com.mentorz.listener.OnUpdateToolBarListener
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Global
import com.plumillonforge.android.chipview.ChipViewAdapter
import kotlinx.android.synthetic.main.fragment_interests.*

/**
 * A simple [Fragment] subclass.
 */
class FilterInterestsFragment : BaseFragment(), InterestView {

    override fun networkError() {
        super.networkError()
    }
    override fun onSessionExpired() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(activity, AuthenticationActivity::class.java))
                        activity.finishAffinity()
                    })
        }
    }

    var parentId = 0
    var superParentId = 0
    var interestList: List<InterestsItem>? = null
    override fun showEmptyInterestAlert() {
        activity.showSnackBar(activity.window.decorView.rootView, getString(R.string.select_atleast_one_interest))
    }

    override fun onUpdateInterestSuccess() {
        activity.runOnUiThread {
            activity.setResult(Activity.RESULT_OK, Intent())
            activity.finish()
        }
    }

    override fun onUpdateInterestFail() {
        showToast(getString(R.string.something_went_wrong_please_try_later))
    }

    override fun setInterestAdapter(interest: List<InterestsItem>?) {

        activity.runOnUiThread {
            interestList = interest
            interestList?.forEach {
               it.isMyInterest=false
            }
            setAdapter(interestList)
        }
    }

    private fun setAdapter(interest: List<InterestsItem>?){
            interestList =interest
            chipView.adapter = MyChipViewAdapter(activity)
            chipView.chipLayoutRes = R.layout.chip_close
            chipView.chipBackgroundColor = resources.getColor(R.color.colorAccent)
            chipView.chipBackgroundColorSelected = resources.getColor(R.color.black)
            Global.defaultFilteredInterestsMap!!.put(parentId, interestList!! as MutableList<InterestsItem>)
            chipView.chipList = interestList

    }

    val presenter = InterestPresenterImpl(this)

    override fun showProgress() {
        activity.showProgressBar(progressBar)
    }

    override fun hideProgress() {
        activity.hideProgressBar(progressBar)
    }

    companion object {
        fun getInstance(superParentId: Int, parentId: Int): FilterInterestsFragment {
            val bundle: Bundle = Bundle()
            bundle.putInt("parent_id", parentId)
            bundle.putInt("super_parent_id", superParentId)

            val fragment: FilterInterestsFragment = FilterInterestsFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun getInstance(): FilterInterestsFragment {
            val fragment: FilterInterestsFragment = FilterInterestsFragment()
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            parentId = arguments.getInt("parent_id")
            superParentId = arguments.getInt("super_parent_id")

        } catch (e: NullPointerException) {
            Log.d(TAG, "parent is null", e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interests, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.findViewById<TextView>(R.id.txtDone).setOnClickListener {
            //  removeAllChild(InterestsItem(interestId = 1,parentId = 0,hasChildren = true))
            val interest = getUpdatedList(Global.defaultFilteredInterestsMap!!.values)
            if(interest.isEmpty()){
                activity?.showSnackBar(rootView,getString(R.string.select_atleast_one_interest))
                return@setOnClickListener
            }
            activity?.setResult(Activity.RESULT_OK,Intent())
            activity?.finish()
        }
        var listener: OnUpdateToolBarListener = activity as OnUpdateToolBarListener

        if (MentorzApplication.instance?.prefs?.hasValuesInterests()!!) {
            activity.findViewById<TextView>(R.id.txtDone).text = getString(R.string.done)

        } else {
            if(parentId==0){
                activity.findViewById<View>(R.id.imgBack).visibility=View.GONE
            }
            else{
                activity.findViewById<View>(R.id.imgBack).visibility=View.VISIBLE

            }
            activity.findViewById<View>(R.id.interestDesc).visibility = View.VISIBLE
            activity.findViewById<View>(R.id.defaultView).visibility = View.GONE
            activity.findViewById<TextView>(R.id.txtDone).text = getString(R.string.next)
        }

        if (parentId == 0) {
            activity.findViewById<TextView>(R.id.txtDone).visibility = View.VISIBLE
            if (Global.defaultFilteredInterestsMap!![0] != null) {
                setAdapter(Global.defaultFilteredInterestsMap!![0])
            } else {
                presenter.getInterests()
            }
        } else {
            activity.findViewById<TextView>(R.id.txtDone).visibility = View.GONE
            if (Global.defaultFilteredInterestsMap!![parentId] != null) {
                setAdapter(Global.defaultFilteredInterestsMap!![parentId])
            } else {
                presenter.getInterestByParentId(parentId)
            }
        }
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
            val txtClose = view.findViewById<View>(android.R.id.closeButton) as TextView

            txtClose.setOnClickListener {
                FragmentFactory.addFragment(getInstance(parentId, tagItem.interestId!!), R.id.frameContainer, this@MyChipViewAdapter.context)
            }

            txtClose.visibility = if (tagItem.hasChildren!!) {
                View.VISIBLE
            } else {
                View.GONE
            }

            val parentItem = Global.defaultFilteredInterestsMap!![superParentId]?.find {
                it.interestId == parentId
            }
            if (parentItem != null && parentItem.isMyInterest!!) {

                tagItem.isMyInterest = true
                txtClose.setTextColor(getColor(R.color.black))
                imgTick.setBackgroundResource(R.drawable.selected_tick)
                container.setBackgroundResource(R.drawable.white_rounded_corner)
                txtValue.setTextColor(getColor(R.color.black))
            } else {
                if (parentItem != null && parentItem.isUpdated!!) {
                    if (parentItem.isMyInterest!!) {
                        tagItem.isMyInterest = true
                        txtClose.setTextColor(getColor(R.color.black))
                        imgTick.setBackgroundResource(R.drawable.selected_tick)
                        container.setBackgroundResource(R.drawable.white_rounded_corner)
                        txtValue.setTextColor(getColor(R.color.black))

                    } else {
                        tagItem.isMyInterest = false
                        txtClose.setTextColor(getColor(R.color.white))
                        txtValue.setTextColor(getColor(R.color.white))
                        imgTick.setBackgroundResource(R.drawable.tick)
                        container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    }

                } else {
                    if (tagItem.isMyInterest!!) {
                        tagItem.isMyInterest = true
                        txtClose.setTextColor(getColor(R.color.black))
                        imgTick.setBackgroundResource(R.drawable.selected_tick)
                        container.setBackgroundResource(R.drawable.white_rounded_corner)
                        txtValue.setTextColor(getColor(R.color.black))
                    } else {
                        tagItem.isMyInterest = false
                        txtClose.setTextColor(getColor(R.color.white))
                        txtValue.setTextColor(getColor(R.color.white))
                        imgTick.setBackgroundResource(R.drawable.tick)
                        container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    }

                }
            }

            (view.findViewById<View>(android.R.id.content) as FrameLayout).setOnClickListener {

                tagItem.isUpdated = true
                if (tagItem.isMyInterest!!) {
                    tagItem.isMyInterest = false
                    imgTick.setBackgroundResource(R.drawable.tick)
                    container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    txtValue.setTextColor(getColor(R.color.white))
                    txtClose.setTextColor(getColor(R.color.white))

                } else {
                    tagItem.isMyInterest = true
                    imgTick.setBackgroundResource(R.drawable.selected_tick)
                    container.setBackgroundResource(R.drawable.white_rounded_corner)
                    txtValue.setTextColor(getColor(R.color.black))
                    txtClose.setTextColor(getColor(R.color.black))
                }
                removeChildIfExist(tagItem)
            }
        }

        fun removeChildIfExist(interestItem: InterestsItem) {

            if (Global.defaultFilteredInterestsMap!!.contains(interestItem.interestId)) {
                val list = Global.defaultFilteredInterestsMap!![interestItem.interestId]
                for (item in list!!.iterator()) {
                    if (Global.defaultFilteredInterestsMap!!.contains(item.interestId)) {
                        removeChildIfExist(item)
                    }
                }
                Global.defaultFilteredInterestsMap!!.remove(interestItem.interestId)
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

    override fun onDestroy() {
        super.onDestroy()
        if (interestList == null) {
            return
        }
        if (parentId == 0) {
            return
        }

        val isAllChecked = interestList!!.all {
            it.isMyInterest!!
        }
        unCheckedParentItem(isAllChecked, parentId)

    }

    fun unCheckedParentItem(isAllChecked: Boolean, parentId: Int) {
        var parentItem: InterestsItem? = null

        outerloop@ for (itemList in Global.defaultFilteredInterestsMap!!.values) {
            innerloop@ for (item in itemList) {
                if (item.interestId == parentId) {
                    parentItem = item
                    break@outerloop
                }
            }
        }

        if (parentItem != null) {
            if (!isAllChecked) {
                parentItem.isMyInterest = false
                parentItem.isUpdated = false
                if (parentItem.parentId != 0) {
                    unCheckedParentItem(isAllChecked, parentItem.parentId!!)
                }
            } else {
                parentItem.isMyInterest = true
                parentItem.isUpdated = false
            }
        } else {
            return
        }
    }

    private fun getUpdatedList(interestList: MutableCollection<MutableList<InterestsItem>>): MutableList<InterestsItem> {
        val list: MutableList<InterestsItem> = mutableListOf()
        outerloop@ for (itemList in interestList) {
            list.addAll(itemList)
        }
        for (item in list) {
            if (item.isMyInterest!!) {
                if (item.hasChildren!!) {
                    if (Global.filteredInterests!!.contains(item)) {
                        Global.filteredInterests!!.remove(item)
                    }
                    Global.filteredInterests!!.add(item)
                    removeAllChild(item)
                } else {
                    if (Global.filteredInterests!!.contains(item)) {
                        Global.filteredInterests!!.remove(item)
                    }
                    Global.filteredInterests!!.add(item)
                }
            } else if (!item.isMyInterest!! && !item.isUpdated!!) {
                Global.filteredInterests!!.remove(item)
            } else if (!item.isMyInterest!! && item.isUpdated!!) {
                removeAllChild(item)
                Global.filteredInterests!!.remove(item)

            }
        }
        return Global.filteredInterests!!
    }


    fun removeAllChild(interestItem: InterestsItem) {
        val list = Global.filteredInterests!!.filter {
            it.parentId == interestItem.interestId
        }
        for (item in list) {
            if (item.hasChildren!!) {
                removeAllChild(item)
                Global.filteredInterests!!.remove(item)
            } else {
                Global.filteredInterests!!.remove(item)
            }
        }
    }


}