package com.shardainfotech.jobpic;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class MyJobDataSourceFactory extends DataSource.Factory {
    private MutableLiveData<MyJobDataSource> mutableLiveData;
    private MyJobDataSource feedDataSource;
    private Context context;

    public MyJobDataSourceFactory(Context context) {
        this.context = context;
        this.mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        feedDataSource = new MyJobDataSource(context);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }

    public MutableLiveData<MyJobDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
