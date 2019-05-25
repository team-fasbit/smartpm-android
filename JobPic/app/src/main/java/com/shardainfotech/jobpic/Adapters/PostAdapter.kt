package com.shardainfotech.jobpic.Adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.shardainfotech.jobpic.Models.Model
import com.shardainfotech.jobpic.R
import kotlinx.android.synthetic.main.post_image_cell.view.*


// val routs: JSONArray
class PostAdapter(var pics:ArrayList<Model>): androidx.recyclerview.widget.RecyclerView.Adapter<PostAdapterViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {
        return pics.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PostAdapterViewHolder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        //create view
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.post_image_cell, p0, false)
        return PostAdapterViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: PostAdapterViewHolder, position: Int) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // p0.view.rout_number_adapter.text = routs.getJSONObject(p1).getString("route_number") as String
        val model = pics.get(position)
        Glide.with(holder.view.context).load(model.bitmap).into(holder.view.imageView_job)
    }

}

class PostAdapterViewHolder(val view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

