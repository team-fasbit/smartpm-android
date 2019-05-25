package com.shardainfotech.jobpic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagedList
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.shardainfotech.jobpic.Models.JobModel
import com.shardainfotech.jobpic.Models.NetworkState


class MyJobViewModel(application: Application): AndroidViewModel(application) {

    var networkStateLiveData: LiveData<NetworkState>? = null
    var jobsLiveData: LiveData<PagedList<JobModel.Data>>? = null

    init {
        val dataSourceFactory = MyJobDataSourceFactory(application)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        networkStateLiveData = Transformations.switchMap(dataSourceFactory.mutableLiveData) { dataSource ->
            dataSource.networkState
        }

        jobsLiveData = LivePagedListBuilder(dataSourceFactory,config).build() as LiveData<PagedList<JobModel.Data>>
    }



}