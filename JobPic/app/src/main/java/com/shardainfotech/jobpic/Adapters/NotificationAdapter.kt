package com.shardainfotech.jobpic.Adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shardainfotech.jobpic.R


// val routs: JSONArray
class NotificationAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<NotificationAdapterViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {
        return 0
        //return routs.length()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotificationAdapterViewHolder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        //create view
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.notification_cell, p0, false)
        return NotificationAdapterViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: NotificationAdapterViewHolder, p1: Int) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // p0.view.rout_number_adapter.text = routs.getJSONObject(p1).getString("route_number") as String
    }

}

class NotificationAdapterViewHolder(val view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

}
