package com.shardainfotech.jobpic

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import com.shardainfotech.jobpic.Constants.FORGOT_PASSWORD
import com.shardainfotech.jobpic.Constants.LOGIN_URL
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.forgotpassword_dialog.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.HashMap

class LoginActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initilize()
    }

    fun initilize() {
        email_login.setText("pj@gmail.com")
        password_login.setText("123456")

        term_conditions_btn.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }

        login_btn.setOnClickListener {
            validations()
        }

        forgot_password_btn.setOnClickListener {
            forgotPassword()
        }

        checkAndRequestPermissions()

    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    fun forgotPassword() {

        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.forgotpassword_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogLoginBtn.setOnClickListener {
            //dismiss dialog

            if (mDialogView.email_forgotpass.text.toString() == "") {
                alert("Email Address","Please enter your email address!")
            } else if (!isEmailValid(mDialogView.email_forgotpass.text.toString())) {
                alert("Invalid Email","Please enter your correct email address!")
            } else {
                 forgoyPassword(mDialogView.email_forgotpass.text.toString().trim(), hidePopUp = ({
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
            Login By API
     ****************************/

    fun validations() {
        if (email_login.text.toString() == "") {
            alert("Email Address","Please enter your email address!")
        } else if (!isEmailValid(email_login.text.toString())) {
            alert("Invalid Email","Please enter your correct email address!")
        } else if (password_login.text.toString() == "") {
            alert("Password","Please enter your password!")
        } else {
            /**
                val intent = Intent(applicationContext, MainDashboardActivity::class.java)
                startActivity(intent)
             **/
            loginByApi(email_login.text.toString(), password_login.text.toString())
        }
    }


    fun alert(title: String, message: String) {
        val builder = AlertDialog.Builder(this@LoginActivity)
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
        positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }


    private fun loginByApi(email:String, password:String) {

        val client = OkHttpClient()
        val params = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(LOGIN_URL)
            .post(params)
            .build()


//        val progressDialog = ProgressDialog(this@LoginActivity)
//        progressDialog.setMessage("loading...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()

        DisplayProgressDialog()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                call.cancel()
                this@LoginActivity.runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body()?.string())
                println("Login Responce $json")

                if (json.get("message") == "successfully login!") {
                    val token: String = json.get("access_token") as String
                    val id: Int = json.getJSONObject("user")?.get("id") as Int
                    val email = json.getJSONObject("user")?.get("email") as String
                    val img = json.getJSONObject("user")?.get("profile_image")

                    println(img)

                    var PImg = ""
                    if (img != null) {
                        PImg = img.toString()
                    }

                    // val img = profileInfo.getString("profile_image_url")

                    val sharedPreferences = getSharedPreferences("loginScreenInfo",Context.MODE_PRIVATE)

                    val info = sharedPreferences.edit()
                    info.putString("token", token)
                    info.putString("id", id.toString())
                    info.putString("profile_Image", PImg)
                    info.putString("email", email)

                    info.apply()

                    this@LoginActivity.runOnUiThread {
                        val intent = Intent(applicationContext, MainDashboardActivity::class.java)
                        startActivity(intent)
                        // overridePendingTransition( R.anim.left_to_right, R.anim.right_to_left);
                    }

                } else {
                    this@LoginActivity.runOnUiThread {
                        alert("Alert", json.get("message").toString())
                    }
                }

                this@LoginActivity.runOnUiThread {
                    HideProgressDialog()
                }

            }
        })
    }

    /****************************
        Forgot Password By Api
     ****************************/

    fun forgoyPassword(email: String, hidePopUp: (() -> Unit)) {

        val client = OkHttpClient()
        val params = FormBody.Builder()
            .add("email", email)
            .build()

        val request = Request.Builder()
            .url(FORGOT_PASSWORD)
            .post(params)
            .build()

        DisplayProgressDialog()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                call.cancel()

                this@LoginActivity.runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body()?.string())
                println("Forgot password Responce $json")

                if (json.get("message") == "We have sent a new password on your email.") {
                    this@LoginActivity.runOnUiThread {
                        alert("Success", "Please check your email address for the new password!")
                        hidePopUp.invoke()
                    }
                } else {
                    this@LoginActivity.runOnUiThread {
                        alert("Email Address", "Invalid email address, Please enter your correct email address!")
                    }
                }

                this@LoginActivity.runOnUiThread {
                    HideProgressDialog()
                }
            }

        })

    }



    override fun onBackPressed() {
        /* super.onBackPressed()  commented this line in order to disable back press */
        moveTaskToBack(true)
    }


    fun DisplayProgressDialog() {
        // we take seprate file and put our loader in that and add it in alert dialoge here,and show progressbar
        val builder = AlertDialog.Builder(this)
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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(),
                PostJobActivity.REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            PostJobActivity.REQUEST_ID_MULTIPLE_PERMISSIONS -> {

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
        android.app.AlertDialog.Builder(this)
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
}
