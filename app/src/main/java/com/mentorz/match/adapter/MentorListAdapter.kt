package com.mentorz.match.adapter


import android.content.Context
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.mentorz.adapter.BaseAdapter
import com.mentorz.match.UserListItem
import com.mentorz.model.LoadingItem

class MentorListAdapter(context: Context, listener: MentorsDelegateAdapter.onViewSelectedListener) :Filterable, BaseAdapter<MentorListAdapter, RecyclerView.ViewHolder>() {
    override fun getFilter(): Filter {
        return mFilter
    }

    private var mFilter = CustomFilter()
    private var items: MutableList<ViewType>
    private var filteredItems: MutableList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private var loadingItem:LoadingItem= LoadingItem()


    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.ITEMS, MentorsDelegateAdapter(context,this, listener))
        items = mutableListOf()
        filteredItems = mutableListOf()
        filteredItems.addAll(items)

    }


    override fun getItemCount(): Int {
        return filteredItems.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(position, holder, this.filteredItems[position])

    }

    override fun getItemViewType(position: Int): Int {
        return this.filteredItems[position].getViewType()
    }

    fun addMentors(news: List<UserListItem>) {
        // first remove loading and notify
        removeLoaderFromBottom()
        val initPosition = items.size - 1
        items.addAll(news)
        filteredItems.addAll(news)
        notifyItemRangeChanged(initPosition, filteredItems.size   /* plus loading item */)
        if(!filterString.isEmpty()) {
            filter.filter(filterString)
        }
    }

    fun addLoaderAtBottom(){
        items.add(loadingItem)
        filteredItems.add(loadingItem)
        notifyItemInserted(filteredItems.size-1)
    }
    fun removeLoaderFromBottom() {
        setLoaded()
        if (!items.isEmpty()&& items[items.size-1]is LoadingItem) {
            items.removeAt(items.size-1)
            filteredItems.removeAt(filteredItems.size-1)
            notifyItemRemoved(filteredItems.size)
        }
    }
    fun updateItem(userId:Long){
        filteredItems.forEach {
            if(it is UserListItem&& it.userId==userId){
                it.request =UserListItem.Request.REQUEST_SENT
                return@forEach
            }
        }
        notifyDataSetChanged()
    }
    fun removeItem(userId:Long){
        for(index in filteredItems.indices){
           var item =  filteredItems[index]
            if(item is UserListItem&& item.userId==userId){
                filteredItems.removeAt(index)
                notifyItemRemoved(index)
                break
            }
        }

    }
    fun clearAndAddMentors(news: List<UserListItem>) {
        setLoaded()
        items.clear()
        filteredItems.clear()
        filteredItems.addAll(news)
        items.addAll(news)
        if(!filterString.isEmpty()) {
            filter.filter(filterString)
        }
        else{
            notifyDataSetChanged()
        }
    }
    fun clear(){
        items.clear()
        filteredItems.clear()
        notifyDataSetChanged()
    }

    fun getMentors(): List<UserListItem> {
        return items
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it as UserListItem }
    }

    var filterString=""

    inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            filterString=constraint.toString()
            val results = Filter.FilterResults()
            setLoaded()
            filteredItems.clear()
            if(constraint.isNullOrEmpty()){
                filteredItems.addAll(items)
            }
             else{

                val tempList = items.filter {
                    it is UserListItem
                }
                val filteredList =  tempList.filter {
                    val item = (it as UserListItem)
                    item.userProfile?.name?.contains(constraint!!,true)!!||item.getExpertiseInString().contains(constraint!!,true)||item.userProfile?.basicInfo?.contains(constraint,true)!!

                }
                if(!filteredList.isEmpty()){
                    filteredItems.addAll(filteredList.toMutableList())
                }

             }
            results.values = filteredItems
            results.count = filteredItems.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            notifyDataSetChanged()
        }
    }
    override fun getRows(): List<Any?>? {
        return filteredItems
                .filter { it.getViewType() == AdapterConstants.ITEMS }
                .map { it }
    }

}