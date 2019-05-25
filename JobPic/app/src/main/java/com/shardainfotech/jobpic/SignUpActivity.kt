package com.shardainfotech.jobpic

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import com.shardainfotech.jobpic.Constants.REGISTER_URL
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.term_conditions_btn
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SignUpActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initilize()
    }

    fun initilize() {

        go_back_privacy.setOnClickListener {
            finish()
        }

        term_conditions_btn.setOnClickListener {
            val intent = Intent(applicationContext, TermsConditionsActivity::class.java)
            startActivity(intent)
        }

        signup_btn.setOnClickListener {
            validations()
        }

    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun  validations() {

        if (email_signup.text.toString() == "") {
            alert("Email Address","Please enter your email address!")
        } else if (!isEmailValid(email_signup.text.toString())) {
            alert("Invalid Email","Please enter your correct email address!")
        } else if (password_signup.text.toString() == "") {
            alert("Password","Please enter your password!")
        } else if (password_signup.text.toString().length < 6) {
            alert("Password","Your password must be at least 6 characters!")
        } else if (password_confirm_signup.text.toString() == "") {
            alert("Confirm Password","Please re-enter your password!")
        } else if (password_confirm_signup.text.toString().length < 6) {
            alert("Confirm Password","The confirm password must be at least 6 characters!")
        } else if (password_signup.text.toString() != password_confirm_signup.text.toString()) {
            alert("Invalid Password","Password does not match!")
        } else {
            registerByApi(email_signup.text.toString(), password_signup.text.toString(), password_confirm_signup.text.toString())
        }

    }

    fun alert(title: String, message: String) {
        val builder = AlertDialog.Builder(this@SignUpActivity)
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


    private fun registerByApi(email:String, password:String, passwordConfirmation: String) {

        val client = OkHttpClient()
        val params = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .add("password_confirmation", passwordConfirmation)
            .build()

        val request = Request.Builder()
            .url(REGISTER_URL)
            .post(params)
            .build()


        DisplayProgressDialog()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                call.cancel()
                this@SignUpActivity.runOnUiThread {
                    HideProgressDialog()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONObject(response.body()?.string())
                println("Login Responce $json")

                if (json.get("message") == "User has been registered successfully.") {
                    val token: String = json.get("access_token") as String
                    val id: Int = json.getJSONObject("user")?.get("id") as Int
                    val email = json.getJSONObject("user")?.get("email") as String

                    // val img = profileInfo.getString("profile_image_url")

                    val sharedPreferences = getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)

                    val info = sharedPreferences.edit()
                    info.putString("token", token)
                    info.putString("id", id.toString())
                    // info.putString("profile_Image", img)
                    info.putString("email", email)

                    info.apply()

                    this@SignUpActivity.runOnUiThread {
                        val intent = Intent(applicationContext, MainDashboardActivity::class.java)
                        startActivity(intent)
                    }

                } else {
                    this@SignUpActivity.runOnUiThread {
                        alert("Alert", "Your information is incorrect, Please enter correct!")
                    }
                }

                this@SignUpActivity.runOnUiThread {
                    HideProgressDialog()
                }

            }
        })
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


}
