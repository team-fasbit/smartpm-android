package com.shardainfotech.jobpic.Adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.shardainfotech.jobpic.Constants.IMAGE_BASE_URL
import com.shardainfotech.jobpic.R
import kotlinx.android.synthetic.main.activity_login.view.*
import org.json.JSONObject
import java.lang.ref.WeakReference

import java.util.ArrayList

class AllPicsAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<AllPicsAdapter.ViewHolder>() {
    var callback: Callback? = null
    private val dataList = ArrayList<JSONObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_pic_image_cell, parent, false)
        return ViewHolder(view, WeakReference(dataList))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addItems(list: List<JSONObject>){
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View, private val dataList: WeakReference<ArrayList<JSONObject>>) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int)
        {
            val model = dataList.get()!![position]
            Glide.with(itemView.context).load(model.getString("image_name")).into(itemView.imageView_job)


            itemView.imageView_job.setOnClickListener {
                callback?.onImageChoose(model.getString("image_name"))
            }
        }
    }

    interface Callback
    {
        fun onImageChoose(imagePath: String)
    }
}
