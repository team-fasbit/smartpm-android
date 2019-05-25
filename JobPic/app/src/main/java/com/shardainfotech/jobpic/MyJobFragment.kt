package com.shardainfotech.jobpic

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.shardainfotech.jobpic.Adapters.MyJobAdapter
import kotlinx.android.synthetic.main.fragment_job.*
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import com.shardainfotech.jobpic.Constants.POST_JOB
import com.shardainfotech.jobpic.Models.JobModel
import com.shardainfotech.jobpic.Models.NetworkState
import java.io.BufferedOutputStream


class MyJobFragment : androidx.fragment.app.Fragment(),
        MyJobAdapter.Callback
{

    lateinit var alertDialog: androidx.appcompat.app.AlertDialog
    var job: JobModel.Data? = null
    lateinit var adapter: MyJobAdapter

    private var CAMERA_REQUEST = 1888
    private var Saveimeg_CAMERA_REQUEST = 1000
    private var UPLOAD_IMAGE_CAPTURE = 1240


    val myJobsViewModel: MyJobViewModel by lazy {
        ViewModelProviders.of(this).get(MyJobViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MyJobAdapter()
        adapter.callback = this
        recycalview_my_job.layoutManager = LinearLayoutManager(context)
        recycalview_my_job.adapter = adapter

        myJobsViewModel.networkStateLiveData?.observe(this, androidx.lifecycle.Observer {
            it?.let {
                state ->
                when(state)
                {
                    NetworkState.STATE_UNAUTHORIZED ->
                    {
                        val preferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
                        preferences?.edit()?.clear()?.apply()
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    NetworkState.INITIAL_LOADING -> {
                        shimmerLayout.visibility = View.VISIBLE
                        shimmerLayout.startShimmer()
                        recycalview_my_job.visibility = View.GONE
                        no_record_myjob.visibility = View.GONE
                    }

                    NetworkState.STATE_EMPTY -> {
                        recycalview_my_job.visibility = View.GONE
                        no_record_myjob.visibility = View.VISIBLE
                    }

                    NetworkState.INITIAL_ERROR -> {
                        shimmerLayout.visibility = View.GONE
                        shimmerLayout.stopShimmer()
                        recycalview_my_job.visibility = View.GONE
                        no_record_myjob.text = "Error while loading data"
                        no_record_myjob.visibility = View.VISIBLE
                    }

                    else ->
                    {
                        shimmerLayout.visibility = View.GONE
                        shimmerLayout.stopShimmer()
                        recycalview_my_job.visibility = View.VISIBLE
                        no_record_myjob.visibility = View.GONE
                        adapter.networkState = it
                    }
                }
            }
        })


        myJobsViewModel.jobsLiveData?.observe(this, androidx.lifecycle.Observer {
            it?.let { pagedList ->
                adapter.submitList(pagedList)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycalview_my_job.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        add_btn_jobs.setOnClickListener {
            val intent = Intent(context, PostJobActivity::class.java)
            startActivity(intent)
        }


        camera_myjob.setOnClickListener {
            if(checkAndRequestPermissions()) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, Saveimeg_CAMERA_REQUEST)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        myJobsViewModel.jobsLiveData?.value?.dataSource?.invalidate()
//        getAlljobs()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == Saveimeg_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras!!.get("data") as Bitmap
            // p_image!!.setImageBitmap(photo)
            saveimagefolder(photo)
        }
        else
        if (requestCode == UPLOAD_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras!!.get("data") as Bitmap
            val file = File(context?.cacheDir,"temp.jpg")
            val os = BufferedOutputStream(FileOutputStream(file))
            photo.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
            postJobData(file)
        }
    }


    private fun postJobData(image: File) {

        val sharedPreferences = activity?.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token","")

        val client = OkHttpClient()
        val requestBodyBuilder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        // add the images as part data
        requestBodyBuilder.addFormDataPart(
            "images[]",
            image.name,
            RequestBody.create(MediaType.parse("image/jpeg"), image)
        )

        // add the name of the job
        requestBodyBuilder.addFormDataPart("id", "${job?.id}")
        requestBodyBuilder.addFormDataPart("job_name", job?.job_name)
        // add the job description
        requestBodyBuilder.addFormDataPart("job_description", job?.job_description)
        // add the address
        requestBodyBuilder.addFormDataPart("address", job?.address)



        val request = Request.Builder()
            .url(POST_JOB)
            .addHeader(
                "authorization",
                "Bearer $token"
            )
            .post(requestBodyBuilder.build())
            .build()

        //client.newCall(request)

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body()?.string())
                if (json.get("success") == true) {
                    activity?.runOnUiThread {
                        alert("Success", "Image uploaded successfully.")
                        onResume()
                    }
                } else {
                    activity?.runOnUiThread {
                        alert("Failed to upload Image", json.getString("response"))
                    }
                }
/*
                activity?.runOnUiThread {
                    HideProgressDialog()
                }
*/
            }

        })

    }


    // Save image in folder
    fun saveimagefolder(bitmap : Bitmap){
        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Jobpic")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Wallpaper-" + n + ".jpg"
        val file = File(myDir, fname)
        if (file.exists())
            file.delete()
        try
        {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            /* Toast.makeText(
           this, getString(R.string.toast_saved).replace("#",
           "\"" + pref.getGalleryName() + "\""),
           Toast.LENGTH_SHORT).show();*/
            Log.d("aa", "Wallpaper saved to: " + file.getAbsolutePath())
        }
        catch (e:Exception) {
            e.printStackTrace()
            //Toast.makeText(this, "fail",
              //  Toast.LENGTH_SHORT).show()
        }
    }




    fun alert(title: String, message: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()
        }
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!,R.color.colorPrimary))
    }

    fun DisplayProgressDialog() {
        // we take seprate file and put our loader in that and add it in alert dialoge here,and show progressbar
        val builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.loader)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(null)
    }


    fun HideProgressDialog() {
        //dialog dismiss
        alertDialog.dismiss()
    }


    private fun checkAndRequestPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
        val writePermission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if(cameraPermission==PackageManager.PERMISSION_DENIED|| writePermission==PackageManager.PERMISSION_DENIED)
        {
            openSettings()
            return false
        }
        return true
    }

    private fun openSettings()
    {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package",activity?.packageName, null)
        intent.data = uri
        context?.startActivity(intent)
        Toast.makeText(context,"Please allow permission",Toast.LENGTH_LONG).show()
    }

    override fun onViewClick(images: List<JobModel.Images>) {
        val gson = Gson()
        val intent = Intent(context, AllPicsActivity::class.java)
        intent.putExtra(AllPicsActivity.IMAGES_ARRAY,gson.toJson(images))
        startActivity(intent)
    }

    override fun onCameraClick(job: JobModel.Data) {
        if(checkAndRequestPermissions()) {
            this.job = job
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("JOB", job.toString())
            startActivityForResult(cameraIntent, UPLOAD_IMAGE_CAPTURE)
        }
    }

}
