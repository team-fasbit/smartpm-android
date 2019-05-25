package com.shardainfotech.jobpic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.shardainfotech.jobpic.Adapters.AllPicsAdapter
import com.shardainfotech.jobpic.Constants.IMAGE_BASE_URL
import kotlinx.android.synthetic.main.activity_all_pics.*
import org.json.JSONArray
import org.json.JSONObject

class AllPicsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_pics)


        val allPicsAdapter = AllPicsAdapter()
        allPicsList.layoutManager = GridLayoutManager(this, 2)
        allPicsList.adapter = allPicsAdapter
        intent?.let {
            if(it.hasExtra(IMAGES_ARRAY))
            {
                val array = JSONArray(it.getStringExtra(IMAGES_ARRAY))
                val list = ArrayList<JSONObject>()
                for(i in 0 until array.length())
                {
                    val ob = array.getJSONObject(i)
                    ob.put("image_name", IMAGE_BASE_URL+ob.getString("image_name"))
                    list.add(ob)
                }
                if(list.isEmpty())
                    messageGroup.visibility = View.VISIBLE
                else {
                    allPicsAdapter.addItems(list)
                    messageGroup.visibility = View.GONE
                }
            }
        }


        go_back_privacy.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val IMAGES_ARRAY = "IMAGES_ARRAY"
    }

}
