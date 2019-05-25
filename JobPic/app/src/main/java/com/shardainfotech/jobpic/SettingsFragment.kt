package com.shardainfotech.jobpic


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.shardainfotech.jobpic.Constants.*
import kotlinx.android.synthetic.main.changepassword_dialog.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import net.alhazmy13.mediapicker.Image.ImagePicker
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 *
 */

class SettingsFragment : androidx.fragment.app.Fragment() {

//    var bodyHashMap: MutableMap<String, RequestBody> = HashMap()
//    var partArrayList: MutableList<MultipartBody.Part> = ArrayList()


    lateinit var alertDialog: AlertDialog

    private var fileBody: RequestBody? = null

    private var imageSelected: String = ""

    private val STORAGE_PERMISSION_CODE = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val pic= sharedPreferences.getString("profile_Image","")
        val profileUrl = PROFILE_IMAGE_BASE_URL+pic
        Glide.with(this).load(profileUrl).error(R.drawable.user_profile).placeholder(R.drawable.user_profile).into(profile_img)

        changePasswordView.setOnClickListener {
            changePassword()
        }

        show_all_pics.setOnClickListener {
            val galleryIntent = Intent(context, MyGalleryActivity::class.java)
            galleryIntent.putExtra("type", "settings")
            startActivityForResult(galleryIntent, PostJobActivity.GALLERY_REQUEST)
        }

        about_us_settings.setOnClickListener {
            val intent = Intent(context?.applicationContext, AboutUsActivity::class.java)
            startActivity(intent)
        }

        privacy_policy_setting.setOnClickListener {
            val intent = Intent(context?.applicationContext, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        tesrms_conditions_settings.setOnClickListener {
            val intent = Intent(context?.applicationContext, TermsConditionsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val sharedPreferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email","")


        email_setting.text = email

        editprofileBtn.setOnClickListener {

            imageSelected = "profile"

            if(hasPermissions(activity!!,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                imagePickerIntent()
            }
            else{
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }


        }

        logout_btn.setOnClickListener {
            showLogoutAlert()
        }

    }

    fun showLogoutAlert() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure want to logout from the app?")
        builder.setPositiveButton("Yes"){dialog, which ->
            logout()
        }
        builder.setNegativeButton("No") {dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()


        /**Get the positive button from the AlertDialog**/
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

        /**Set your color to the positive button**/
        positiveButton.setTextColor(ContextCompat.getColor(context!!, android.R.color.holo_red_dark))



        /**Get the negative button from the AlertDialog**/
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        /**Set your color to the negative button**/
        negativeButton.setTextColor(ContextCompat.getColor(context!!, android.R.color.black))
    }


    fun logout() {
        val sharedPreferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token","")


        val client = OkHttpClient()

        val request = Request.Builder()
            .url(LOGOUT_URL)
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .build()

        DisplayProgressDialog()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                call.cancel()

                requireActivity().runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body()?.string())
                println("Server Responce $json")

                if (json?.get("response") == "successfully loggedout") {
                    val preferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
                    preferences?.edit()?.clear()?.apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                } else if (json.get("message") == "Unauthenticated.") {
                    val preferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
                    preferences?.edit()?.clear()?.apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    requireActivity().runOnUiThread {
                        alert("Alert", "Please try again after some time!")
                    }
                }

                requireActivity().runOnUiThread {
                    HideProgressDialog()
                }
            }

        })
    }

    fun uploadProfile(image: String) {
        val sharedPreferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token","")

        val client = OkHttpClient()
        val file = File(image)

        val MEDIA_TYPE_JPEG : MediaType? = MediaType.parse("image/png")

        val requestBody : RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("profile_image", image, RequestBody.create(MEDIA_TYPE_JPEG, file)) // !-- Here is the important part
            .addFormDataPart("profile_image", image)
            .build()

        var request : Request = Request.Builder()
            .url(PROFILE_IMG_UPDATE__URL)
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .post(requestBody).build()


        DisplayProgressDialog()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                call.cancel()

                requireActivity().runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful){
                    println("false")
                    Log.d("Error", response.toString())

                }else {
                    var respBody : String? = response.body()?.string()
                    var json = JSONObject(respBody)
                    println("True")
                    println("Server Responce $json")


                    if (json.get("success") == "success") {
                        requireActivity().runOnUiThread {
                            val img = json.getString("new_pic")
                            val sharedPreferences = context!!.getSharedPreferences("loginScreenInfo",Context.MODE_PRIVATE)
                            val info = sharedPreferences.edit()
                            info.putString("profile_Image", img)
                            info.apply()
                            alert("Success", "Your profile image updated successfully!")
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            alert("Alert", "Please try again later!")
                        }
                    }

/*
                    if (json.get("message") == "Visit profile") {
                        requireActivity().runOnUiThread {

                            val profileInfo: JSONObject = json.getJSONObject("data") as JSONObject
                            val img = profileInfo.getString("profile_image_url")

                            val sharedPreferences = context!!.getSharedPreferences("loginScreenInfo",Context.MODE_PRIVATE)
                            val info = sharedPreferences.edit()
                            info.putString("profile_Image", img)
                            info.apply()
                            alert("Success", "Your profile image updated successfully!")
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            alert("Alert", "Please try again later!")
                        }
                    }
*/

                }

                requireActivity().runOnUiThread {
                    HideProgressDialog()
                }
            }
        })

    }


    fun alert(title: String, message: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()


        /**Get the positive button from the AlertDialog**/
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

        /**Set your color to the positive button**/
        positiveButton.setTextColor(ContextCompat.getColor(context!!, android.R.color.black))

    }


    /*  Select photo from galery or photo */

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                imagePickerIntent()
                //                Intent chooseImageIntent = ImagePicker.getPickImageIntent(MainActivity.this);
                //                startActivityForResult(chooseImageIntent, 100);
            } else {
                Toast.makeText(requireActivity(), "Please grant all the permissions first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun imagePickerIntent() {
//        startActivity(Intent(activity,ProfileSelectActivity::class.java))

        ImagePicker.Builder(activity)
            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
            .directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.JPG)
            .allowMultipleImages(true)
            .enableDebuggingMode(true)
            .build()

    }

   override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {
                // get String data from Intent
//                school_edit_profile.setText(data?.getStringExtra(Intent.EXTRA_COMPONENT_NAME))
//                schoolNameId = data?.getStringExtra(Intent.EXTRA_TEXT).toString()
            }

        } else {
            if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val mPaths = data?.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)
                val photo = File(mPaths!![0])
                fileBody = RequestBody.create(MediaType.parse("/*"), photo)

                val bmp = BitmapFactory.decodeFile(mPaths!![0])

                if (imageSelected == "profile") {
                    profile_img.setImageBitmap(bmp)
                    uploadProfile(mPaths!![0])
                }

            }
        }

    }


    fun DisplayProgressDialog() {
        // we take seprate file and put our loader in that and add it in alert dialoge here,and show progressbar
        val builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.loader)
        builder.setCancelable(false)
        alertDialog = builder.create();
        alertDialog.show()
        alertDialog.getWindow().setBackgroundDrawable(null)
    }


    fun HideProgressDialog() {
        //dialog dismiss
        alertDialog?.dismiss()
    }


    fun hasPermission(context: Context, permission: String): Boolean {

        val res = context.checkCallingOrSelfPermission(permission)

        Log.v(
            "SettingsFragment", "permission: " + permission + " = \t\t" +
                    if (res == PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"
        )

        return res == PackageManager.PERMISSION_GRANTED

    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {

        var hasAllPermissions = true
        for (permission in permissions) {
            //you can return false instead of assigning, but by assigning you can log all permission values
            if (!hasPermission(context, permission)) {
                hasAllPermissions = false
            }
        }

        return hasAllPermissions

    }



    fun changePassword() {

        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.changepassword_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity!!)
            .setView(mDialogView)
            .setTitle("")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogChangePasswordBtn.setOnClickListener {
            //dismiss dialog

            if (mDialogView.old_password_change_pw.text.toString() == "") {
                alert("Old Password","Please enter your old password!")
            }
            else if (mDialogView.new_Password_change_pw.text.toString() == "") {
                alert("New Password","Please enter your new password!")
            }
            else if (mDialogView.new_Password_change_pw.text.toString().length < 6) {
                alert("Password","Your password must be at least 6 characters!")
            }
            else if (mDialogView.confirm_password_change_pw.text.toString() == "") {
                alert("Confirm Address","Please re-enter your new password!")
            }
            else if (mDialogView.new_Password_change_pw.text.toString() != mDialogView.confirm_password_change_pw.text.toString()) {
                alert("Invalid Password","Re-enter your password it does not match!")
            } else {
                forgoyPasswordByApi(mDialogView.old_password_change_pw.text.toString().trim(), mDialogView.new_Password_change_pw.text.toString().trim(), mDialogView.confirm_password_change_pw.text.toString().trim(), hidePopUp = ({
                    mAlertDialog.dismiss()
                }))
            }

        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }


    /****************************
    Change Password By Api
     ****************************/

    fun forgoyPasswordByApi(oldpass: String, newpass: String, conf_pw: String, hidePopUp: (() -> Unit)) {

        val sharedPreferences = context!!.getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token","")

        val client = OkHttpClient()
        val params = FormBody.Builder()
            .add("password_confirmation", conf_pw)
            .add("old_password", oldpass)
            .add("password", newpass)
            .build()

        val request = Request.Builder()
            .url(CHANGE_PASSWORD_URL)
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .post(params)
            .build()

        DisplayProgressDialog()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                call.cancel()

                activity?.runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body()?.string())
                println("Forgot password Responce $json")


                if (json.get("success") == "success") {
                    activity?.runOnUiThread {
                        alert("Success", "Password changed successfully!")
                        hidePopUp.invoke()
                    }
                } else {
                    activity?.runOnUiThread {
                        alert("Alert", "Old password does not match!")
                    }
                }

                activity?.runOnUiThread {
                    HideProgressDialog()
                }

            }

        })

    }

}
