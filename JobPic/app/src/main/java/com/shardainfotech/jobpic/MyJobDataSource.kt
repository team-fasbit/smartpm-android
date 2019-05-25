package com.shardainfotech.jobpic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.shardainfotech.jobpic.Constants.GET_Job
import com.shardainfotech.jobpic.Models.JobModel
import com.shardainfotech.jobpic.Models.NetworkState
import okhttp3.*
import java.io.IOException

class MyJobDataSource(val context: Context): PageKeyedDataSource<Int,JobModel.Data>(){
    val networkState = MutableLiveData<NetworkState>()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, JobModel.Data>) {
        networkState.postValue(NetworkState.INITIAL_LOADING)
        val sharedPreferences = context.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token","")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(GET_Job+"1")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                networkState.postValue(NetworkState.INITIAL_ERROR)
                call.cancel()
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                val jobModel = gson.fromJson(response.body()?.string(),JobModel::class.java)
                if (jobModel.success) {
                    if(jobModel.response.data.isNotEmpty()) {
                        callback.onResult(jobModel.response.data,1,2)
                        networkState.postValue(NetworkState.STATE_LOADED)
                    }
                    else
                        networkState.postValue(NetworkState.STATE_EMPTY)
                } else {
                    if (jobModel.message == "Unauthenticated.") {
                        networkState.postValue(NetworkState.STATE_UNAUTHORIZED)
                    }
                }
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, JobModel.Data>) {
        networkState.postValue(NetworkState.STATE_LOADING)
        val sharedPreferences = context.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token","")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(GET_Job+params.key)
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                networkState.postValue(NetworkState.STATE_ERROR)
                call.cancel()
            }
            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                val jobModel = gson.fromJson(response.body()?.string(),JobModel::class.java)
                if (jobModel.success) {
                    if(jobModel.response.data.isNotEmpty()) {
                        callback.onResult(jobModel.response.data,jobModel.response.current_page+1)
                    }
                    networkState.postValue(NetworkState.STATE_LOADED)
                } else {
                    if (jobModel.message == "Unauthenticated.") {
                        networkState.postValue(NetworkState.STATE_UNAUTHORIZED)
                    }
                }
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, JobModel.Data>) {
    }

}