package com.shardainfotech.jobpic

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.shardainfotech.jobpic.Adapters.AllPicsAdapter
import kotlinx.android.synthetic.main.activity_all_pics.*
import org.json.JSONObject
import java.io.File

class MyGalleryActivity : AppCompatActivity(),
    AllPicsAdapter.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_gallery)


        val allPicsAdapter = AllPicsAdapter()
        allPicsAdapter.callback = this
        allPicsList.layoutManager = GridLayoutManager(this, 2)
        allPicsList.adapter = allPicsAdapter

        getImagesList().let {list->
            if(list.isEmpty())
                messageGroup.visibility = View.VISIBLE
            else {
                allPicsAdapter.addItems(list)
                messageGroup.visibility = View.GONE
            }
        }


        go_back_privacy.setOnClickListener {
            finish()
        }

    }

    private fun getImagesList(): List<JSONObject>
    {
        val imageList = ArrayList<JSONObject>()
        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Jobpic")
        myDir.mkdirs()
        for(file in myDir.listFiles())
        {
            val ob = JSONObject()
            ob.put("image_name",file.absolutePath)
            imageList.add(ob)
        }
        return imageList
    }


    override fun onImageChoose(imagePath: String) {
        val status = intent.getStringExtra("type")

        if (status != "settings") {
            val data = Intent()
            val bundle = Bundle()
            bundle.putString(IMAGE_PATH,imagePath)
            data.putExtras(bundle)
            setResult(Activity.RESULT_OK,data)
            finish()
        }

    }

    companion object {
        const val IMAGE_PATH = "IMAGE_PATH"
    }

}
