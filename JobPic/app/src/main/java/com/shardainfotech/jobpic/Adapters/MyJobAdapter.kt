package com.shardainfotech.jobpic.Adapters

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.shardainfotech.jobpic.Models.JobModel
import com.shardainfotech.jobpic.Models.NetworkState
import com.shardainfotech.jobpic.R
import kotlinx.android.synthetic.main.item_network_state.view.*
import kotlinx.android.synthetic.main.my_job_cell.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit






// val routs: JSONArray
class MyJobAdapter: PagedListAdapter<JobModel.Data,RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    private val TYPE_PROGRESS = 0
    private val TYPE_ITEM = 1

    var callback: Callback? = null
    var networkState: NetworkState?=null

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<JobModel.Data>() {
            override fun areItemsTheSame(oldItem: JobModel.Data, newItem: JobModel.Data): Boolean {
                return oldItem.id == newItem.id
            }
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: JobModel.Data, newItem: JobModel.Data) = oldItem == newItem
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val holder: RecyclerView.ViewHolder
        if(viewType==TYPE_ITEM) {
            val cellForRow = layoutInflater.inflate(R.layout.my_job_cell, viewGroup, false)
            holder = MyJobAdapterViewHolder(cellForRow,callback)
        }
        else {
            val cellForRow = layoutInflater.inflate(R.layout.item_network_state, viewGroup, false)
            holder = NetworkStateViewHolder(cellForRow,callback)
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // holder.view.rout_number_adapter.text = routs.getJSONObject(p1).getString("route_number") as String
        if(holder is MyJobAdapterViewHolder)
            holder.onBind(position,getItem(position))
        else
            (holder as NetworkStateViewHolder).onBind(position,networkState)
    }


    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState !== NetworkState.STATE_LOADED
    }

    interface Callback
    {
        fun onViewClick(images: List<JobModel.Images>)
        fun onCameraClick(job: JobModel.Data)
    }


    class NetworkStateViewHolder(val view: View,val callback: Callback?): RecyclerView.ViewHolder(view)
    {
        fun onBind(position: Int, networkState: NetworkState?)
        {
            when(networkState)
            {
                NetworkState.STATE_LOADING ->{
                    itemView.shimmer.startShimmer()
                }
                NetworkState.STATE_ERROR -> {
                    itemView.shimmer.stopShimmer()
                    itemView.message.setTextColor(Color.RED)
                    itemView.message.text = "Error while loading data"
                }
                else -> { }
            }
        }
    }


    class MyJobAdapterViewHolder(val view: View,val callback: Callback?): RecyclerView.ViewHolder(view)
    {
        fun onBind(position: Int, data: JobModel.Data?)
        {
            data?.let {
                        model->
                itemView.job_name_myjob.text =  model.job_name
                itemView.address_myjob.text =  model.address
                itemView.description_myjob.text =  model.job_description
                itemView.tvTime.text = getPostTime(model.created_at)
                itemView.view_all_img_myjob.setOnClickListener {
                    callback?.onViewClick(model.images)
                }
                itemView.ivCamera.setOnClickListener {
                    callback?.onCameraClick(model)
                }
            }
        }

        private fun getPostTime(date: String): String
        {
            try
            {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val past = format.parse(date)
                val now = Date()
                val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
                val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
                val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

                if(seconds<60)
                {
                    return "$seconds seconds ago"
                }
                else if(minutes<60)
                {
                    return "$minutes minutes ago"
                }
                else if(hours<24)
                {
                    return "$hours hours  ago"
                }
                else
                {
                    return "$days days ago"
                }
            }
            catch (e: Exception){
                return ""
            }
        }

    }
}


