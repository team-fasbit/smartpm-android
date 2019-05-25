package com.shardainfotech.jobpic

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.shardainfotech.jobpic.Adapters.PostAdapter
import com.shardainfotech.jobpic.Constants.POST_JOB
import com.shardainfotech.jobpic.Models.Model
import kotlinx.android.synthetic.main.activity_post_job.*
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostJobActivity : AppCompatActivity(), Callback {



    lateinit var alertDialog: androidx.appcompat.app.AlertDialog

    private var Saveimeg_CAMERA_REQUEST = 1000

    private val REQUEST_TAKE_PHOTO = 1
    private var currentPhotoPath: String = ""


    companion object {
        const val CAMERA_REQUEST = 1888
        const val GALLERY_REQUEST = 8882
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    }


    var arrayList: java.util.ArrayList<Model> = java.util.ArrayList()

    var filePaths = ArrayList<String>()
    var fileImgArray = ArrayList<String>()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_post_job)

        checkAndRequestPermissions()

        recyclerView_post.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )

        go_back_privacy.setOnClickListener {
            finish()
        }


        upload_photo.setOnClickListener {
            val galleryIntent = Intent(this, MyGalleryActivity::class.java)
            galleryIntent.putExtra("type", "post")
            startActivityForResult(galleryIntent, GALLERY_REQUEST)
            // selectImage()
        }

        take_photo.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)

            // selectImage()
        }
        submit_poatjob.setOnClickListener {
            println(fileImgArray)

            validation()
        }

    }



    /**
     * function to select the image
     */
    private fun selectImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.postimages.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    /**
     * add image to recycler view
     */
    private fun addImageToList(image: String) {
        //adapter.addImage(image)
    }

    /**
     * show toast
     */
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(call: Call, e: IOException) {
        runOnUiThread {
            // updateButtonState(true)
            // showToast(e.localizedMessage)

//            if (progressDialog != null) {
//                progressDialog.dismiss()
//            }
        }
    }

    override fun onResponse(call: Call, response: Response) {
        runOnUiThread {

            println(response)

           //  updateButtonState(true)
            // showToast(response.body()?.string()!!)

//            if (progressDialog != null) {
//                progressDialog.dismiss()
//            }

        }
    }


    private fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)


        val listPermissionsNeeded = ArrayList<String>()

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
       // Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.RECORD_AUDIO] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED) {
                       // Log.d(TAG, "sms & location services permission granted")
                        // process the normal flow
//                        val i = Intent(this@MainActivity, WelcomeActivity::class.java)
//                        startActivity(i)
//                        finish()
                        //else any one or both the permissions are not granted
                    } else {
                      //  Log.d(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                           // explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun explain(msg: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
        dialog.setMessage(msg)
            .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                //  permissionsclass.requestPermission(type,code);
                startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.example.parsaniahardik.kotlin_marshmallowpermission")))
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }

    fun getRealPathFromURI(uri: Uri): String {
        val cursor = this!!.getContentResolver().query(uri, null, null, null, null)
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }
    private var requestCode = 20
    var file : File?=null



    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*****
        if (this.requestCode === requestCode && resultCode == Activity.RESULT_OK) {
            val picUri = data!!.data
            file = File(getPath(picUri))
            Log.d("sfnsklfnlsnfl", file.toString())
          //  p_image!!.setImageURI(picUri)
            if (file != null) {
                // Api call

            }
        }*****/

        if(requestCode== GALLERY_REQUEST&&resultCode==Activity.RESULT_OK)
        {
            val photo = data!!.getStringExtra(MyGalleryActivity.IMAGE_PATH)
            // p_image!!.setImageBitmap(photo)
            val model = Model()
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(photo,bmOptions)
            model.bitmap = bitmap
            arrayList.add(model)
            recyclerView_post.adapter = PostAdapter(arrayList)

            val tempUri = getImageUri(this, bitmap)
            file = File(getRealPathFromURI(tempUri))

            filePaths.add(photo)
            fileImgArray.add(file.toString())

            Log.d("sfnsklfnlsnfl", file.toString())
            if (file != null) {
                // APi call
            }
        }
        else
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras!!.get("data") as Bitmap

            // p_image!!.setImageBitmap(photo)

            var model = Model()
            model.bitmap = photo
            arrayList.add(model)
            recyclerView_post.adapter = PostAdapter(arrayList)


            val tempUri = getImageUri(this, photo)
            file = File(getRealPathFromURI(tempUri))

            filePaths.add(file!!.absolutePath)
            fileImgArray.add(file.toString())

            Log.d("sfnsklfnlsnfl", file.toString())
            if (file != null) {
                // APi call
            }

        }

        if (requestCode == Saveimeg_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras!!.get("data") as Bitmap
            // p_image!!.setImageBitmap(photo)

            // saveimagefolder(photo)

            Toast.makeText(this, "save",
                Toast.LENGTH_SHORT).show()
        }

    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    // Save image in folder
    fun saveimagefolder(bitmap : Bitmap){
        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "xyz")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Wallpaper-" + n + ".jpg"
        val file = File(myDir, fname)
        if (file.exists())
            file.delete()
        try {
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
            Toast.makeText(this, "fail",
                Toast.LENGTH_SHORT).show()
        }
    }

    /****************************
     Posting Pics to the server
    ****************************/

    fun validation() {
        if (job_name_post.text.toString() == "") {
            alert("Job Name","Please enter the job name!")
        } else if (job_address_post.text.toString() == "") {
            alert("Job Address","Please enter the job address!")
        } else {
            // job_description_post

           // val images: java.util.ArrayList<String> = adapter.getImages()
            if (fileImgArray.size > 0) {
                // there are some images in the adapter

                // post the data
//                val progressDialog = ProgressDialog(this@PostJobActivity)
//                progressDialog.setMessage("loading...")
//                progressDialog.setCancelable(false)
//                progressDialog.show()

               // http://localhost:8000/jobimages/+ imagename(that is coming in the get all jobs api)


                // updateButtonState(false)

                val sharedPreferences = getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token","")

                // hit the API
                postJobData(token!!, job_name_post.text.toString(), job_description_post.text.toString(), job_address_post.text.toString(), fileImgArray)
            } else {
                showToast("Please select at least one image")
            }

        }
    }

    fun alert(title: String, message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this@PostJobActivity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()

        /**Get the positive button from the AlertDialog**/
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        /**Set your color to the positive button**/
        positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }


    fun postJobData(token: String, name: String, desc: String, address: String, images: ArrayList<String>) {
        val client = OkHttpClient()


        val requestBodyBuilder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)


        // add the images as part data
        images.forEach {
            val fileName: String = it.substring(it.lastIndexOf("/"))
            requestBodyBuilder.addFormDataPart(
                "images[]",
                fileName,
                RequestBody.create(MediaType.parse("image/jpeg"), File(it))
            )
        }

        // add the name of the job
        requestBodyBuilder.addFormDataPart("job_name", name)
        // add the job description
        requestBodyBuilder.addFormDataPart("job_description", desc)
        // add the address
        requestBodyBuilder.addFormDataPart("address", address)


        DisplayProgressDialog()

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
                println(e.message)
                call.cancel()

                this@PostJobActivity.runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body()?.string())
                if (json.get("success") == true) {
                    this@PostJobActivity.runOnUiThread {
                        job_name_post.text = null
                        job_description_post.text = null
                        job_address_post.text = null

                        try {
                            for (path in filePaths) {
                                val file = File(path)
                                if(file.exists())
                                    file.delete()
                            }
                        }
                        catch (e: Exception)
                        {
                            Log.e("error ","eror $e")
                        }


                        for (i in fileImgArray) {
                            fileImgArray.remove(i)
                        }

                        for (i in filePaths) {
                            filePaths.remove(i)
                        }

                        alertCall()

                    }
                } else {
                    this@PostJobActivity.runOnUiThread {
                        alert("An Error Occurred", json.getString("response"))
                    }
                }

                this@PostJobActivity.runOnUiThread {
                    HideProgressDialog()
                }
            }

        })

    }

    fun alertCall() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this@PostJobActivity)
        builder.setTitle("Success")
        builder.setMessage("Job submited successfully!")
        builder.setPositiveButton("OK") { dialog, which ->
            finish()
        }
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()

        /**Get the positive button from the AlertDialog**/
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        /**Set your color to the positive button**/
        positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }


    fun DisplayProgressDialog() {
        // we take seprate file and put our loader in that and add it in alert dialoge here,and show progressbar
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(R.layout.loader)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window.setBackgroundDrawable(null)
    }


    fun HideProgressDialog() {
        //dialog dismiss
        alertDialog?.dismiss()
    }

}
