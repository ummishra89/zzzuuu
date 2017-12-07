package com.mentorz.expertise


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
import com.mentorz.R
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.extensions.showToast
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.FragmentFactory
import com.mentorz.utils.Global
import com.plumillonforge.android.chipview.ChipViewAdapter
import kotlinx.android.synthetic.main.fragment_expertise.*

/**
 * A simple [Fragment] subclass.
 */
class ExpertiseFragment : BaseFragment(), ExpertiseView {
    var parentId = 0
    var superParentId = 0
    var expertiseList: List<ExpertiseItem>? = null
    var tempExpertise:MutableList<ExpertiseItem>?=null

    override fun networkError() {
        super.networkError()
    }
    override fun onSessionExpired() {
        activity?.runOnUiThread {

        }
    }

    override fun onUpdateExpertiseSuccess() {
        activity.runOnUiThread {
            activity.setResult(Activity.RESULT_OK, Intent())
            activity.finish()
        }
    }

    override fun onUpdateExpertiseFail() {
        showToast(getString(R.string.something_went_wrong_please_try_later))
    }

    override fun setExpertiseAdapter(expertise: List<ExpertiseItem>?) {

        activity.runOnUiThread {

            chipView.adapter = MyChipViewAdapter(activity)
            chipView.chipLayoutRes = R.layout.chip_close
            chipView.chipBackgroundColor = resources.getColor(R.color.colorAccent)
            chipView.chipBackgroundColorSelected = resources.getColor(R.color.black)

            expertiseList = expertise
            Global.defaultExpertiseMap!!.put(parentId, expertiseList!! as MutableList<ExpertiseItem>)

            chipView.chipList = expertiseList
        }

    }

    val presenter = ExpertisePresenterImpl(this)

    override fun showProgress() {
        activity.runOnUiThread {
            activity.showProgressBar(progressBar)

        }
    }

    override fun hideProgress() {
        activity.runOnUiThread {
            activity.hideProgressBar(progressBar)
        }
    }

    companion object {
        fun getInstance(superParentId: Int, parentId: Int): ExpertiseFragment {
            val bundle: Bundle = Bundle()
            bundle.putInt("parent_id", parentId)
            bundle.putInt("super_parent_id", superParentId)
            val fragment: ExpertiseFragment = ExpertiseFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun getInstance(): ExpertiseFragment {
            val bundle: Bundle = Bundle()
            bundle.putInt("parent_id", 0)
            val fragment: ExpertiseFragment = ExpertiseFragment()
            fragment.arguments = bundle
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
        return inflater.inflate(R.layout.fragment_expertise, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempExpertise = mutableListOf<ExpertiseItem>()
        activity.findViewById<TextView>(R.id.txtDone).setOnClickListener {
            Global.myexpertises = getUpdatedList(Global.defaultExpertiseMap!!.values)
            if (Global.myexpertises!!.isNotEmpty()) {
                activity.setResult(Activity.RESULT_OK, Intent())
                activity.finish()
            } else {
                activity.showSnackBar(rootView, getString(R.string.select_atleast_one_expertise))
            }
        }
        if (parentId == 0) {
            activity.findViewById<TextView>(R.id.txtDone).visibility = View.VISIBLE
            if (Global.defaultExpertiseMap!![0] != null) {
                setExpertiseAdapter(Global.defaultExpertiseMap!![0])
            } else {
                presenter.getMyExpertise()
                presenter.getDefaultExpertise()
            }
        } else {
            activity.findViewById<TextView>(R.id.txtDone).visibility = View.GONE
            if (Global.defaultExpertiseMap!![parentId] != null) {

                setExpertiseAdapter(Global.defaultExpertiseMap!![parentId])
            } else {
                presenter.getExpertiseByParentId(parentId)
            }
        }
    }

    inner class MyChipViewAdapter(context: Context) : ChipViewAdapter(context) {

        override fun getBackgroundRes(position: Int): Int {
            return 0
        }

        override fun onLayout(view: View?, position: Int) {
            val tagItem = getChip(position) as ExpertiseItem
            val container = view?.findViewById<View>(android.R.id.content) as FrameLayout
            val imgTick = view.findViewById<View>(R.id.imgTick) as ImageView
            val txtValue = view.findViewById<TextView>(android.R.id.text1) as TextView
            val txtClose = view.findViewById<View>(android.R.id.closeButton) as TextView

            txtClose.setOnClickListener {
                FragmentFactory.addFragment(ExpertiseFragment.getInstance(parentId, tagItem.expertiseId!!), R.id.frameContainer, this@MyChipViewAdapter.context)
            }

            txtClose.visibility = if (tagItem.hasChildren!!) {
                View.VISIBLE
            } else {
                View.GONE
            }

            val parentItem = Global.defaultExpertiseMap!![superParentId]?.find {
                it.expertiseId == parentId
            }
            if (parentItem != null && parentItem.isMyExpertise!!) {

                tagItem.isMyExpertise = true
                txtClose.setTextColor(getColor(R.color.black))
                imgTick.setBackgroundResource(R.drawable.selected_tick)
                container.setBackgroundResource(R.drawable.white_rounded_corner)
                txtValue.setTextColor(getColor(R.color.black))
            } else {
                if (parentItem != null && parentItem.isUpdated!!) {
                    if (parentItem.isMyExpertise!!) {
                        tagItem.isMyExpertise = true
                        txtClose.setTextColor(getColor(R.color.black))
                        imgTick.setBackgroundResource(R.drawable.selected_tick)
                        container.setBackgroundResource(R.drawable.white_rounded_corner)
                        txtValue.setTextColor(getColor(R.color.black))

                    } else {
                        tagItem.isMyExpertise = false
                        txtClose.setTextColor(getColor(R.color.white))
                        txtValue.setTextColor(getColor(R.color.white))
                        imgTick.setBackgroundResource(R.drawable.tick)
                        container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    }

                } else {
                    if (tagItem.isMyExpertise!!) {
                        tagItem.isMyExpertise = true
                        txtClose.setTextColor(getColor(R.color.black))
                        imgTick.setBackgroundResource(R.drawable.selected_tick)
                        container.setBackgroundResource(R.drawable.white_rounded_corner)
                        txtValue.setTextColor(getColor(R.color.black))
                    } else {

                        tagItem.isMyExpertise = false
                        txtClose.setTextColor(getColor(R.color.white))
                        txtValue.setTextColor(getColor(R.color.white))
                        imgTick.setBackgroundResource(R.drawable.tick)
                        container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    }

                }
            }

            (view.findViewById<View>(android.R.id.content) as FrameLayout).setOnClickListener {
                tagItem.isUpdated = true
                if (tagItem.isMyExpertise!!) {
                    tagItem.isMyExpertise = false
                    imgTick.setBackgroundResource(R.drawable.tick)
                    container.setBackgroundResource(R.drawable.gray_rounded_corner)
                    txtValue.setTextColor(getColor(R.color.white))
                    txtClose.setTextColor(getColor(R.color.white))

                } else {
                    tagItem.isMyExpertise = true
                    imgTick.setBackgroundResource(R.drawable.selected_tick)
                    container.setBackgroundResource(R.drawable.white_rounded_corner)
                    txtValue.setTextColor(getColor(R.color.black))
                    txtClose.setTextColor(getColor(R.color.black))
                }
                removeChildIfExist(tagItem)
            }
        }

        fun removeChildIfExist(expertiseItem: ExpertiseItem) {

            if (Global.defaultExpertiseMap!!.contains(expertiseItem.expertiseId)) {
                val list = Global.defaultExpertiseMap!![expertiseItem.expertiseId]
                for (item in list!!.iterator()) {
                    if (Global.defaultExpertiseMap!!.contains(item.expertiseId)) {
                        removeChildIfExist(item)
                    }
                }
                Global.defaultExpertiseMap!!.remove(expertiseItem.expertiseId)
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
        if (expertiseList == null) {
            return
        }
        if (parentId == 0) {
            return
        }

        val isAllChecked = expertiseList!!.all {
            it.isMyExpertise!!
        }
        unCheckedParentItem(isAllChecked, parentId)

    }

    fun unCheckedParentItem(isAllChecked: Boolean, parentId: Int) {
        var parentItem: ExpertiseItem? = null

        outerloop@ for (itemList in Global.defaultExpertiseMap!!.values) {
            innerloop@ for (item in itemList) {
                if (item.expertiseId == parentId) {
                    parentItem = item
                    break@outerloop
                }
            }
        }

        if (parentItem != null) {
            if (!isAllChecked) {
                parentItem.isMyExpertise = false
                parentItem.isUpdated = false
                if (parentItem.parentId != 0) {
                    unCheckedParentItem(isAllChecked, parentItem.parentId!!)
                }
            } else {
                parentItem.isMyExpertise = true
                parentItem.isUpdated = false
            }
        } else {
            return
        }
    }

    private fun getUpdatedList(expertiseList: MutableCollection<MutableList<ExpertiseItem>>): MutableList<ExpertiseItem> {

        tempExpertise!!.addAll(Global.myexpertises!!)
        val list: MutableList<ExpertiseItem> = mutableListOf()
        outerloop@ for (itemList in expertiseList) {
            list.addAll(itemList)
        }
        for (item in list) {
            if (item.isMyExpertise!!) {
                if (item.hasChildren!!) {
                    if (tempExpertise!!.contains(item)) {
                        tempExpertise!!.remove(item)
                    }
                    tempExpertise!!.add(item)
                    removeAllChild(item)
                } else {
                    if (tempExpertise!!.contains(item)) {
                        tempExpertise!!.remove(item)
                    }
                    tempExpertise!!.add(item)
                }
            } else if (!item.isMyExpertise!! && !item.isUpdated!!) {
                tempExpertise!!.remove(item)
            } else if (!item.isMyExpertise!! && item.isUpdated!!) {
                removeAllChild(item)
                tempExpertise!!.remove(item)
            }
        }
        return tempExpertise!!
    }

    fun removeAllChild(expertiseItem: ExpertiseItem) {
        val list = tempExpertise!!.filter {
            it.parentId == expertiseItem.expertiseId
        }
        for (item in list) {
            if (item.hasChildren!!) {
                removeAllChild(item)
                tempExpertise!!.remove(item)
            } else {
                tempExpertise!!.remove(item)
            }
        }
    }

}