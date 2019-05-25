package com.shardainfotech.jobpic

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


class SplashActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            checkUserLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Fabric.with(this, Crashlytics())


//        val fabric = Fabric.Builder(this)
//            .kits(Crashlytics())
//            .debuggable(true)
//            .build()
//        Fabric.with(fabric)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }


    fun checkUserLogin() {
        val sharedPreferences = getSharedPreferences("loginScreenInfo", Context.MODE_PRIVATE)

        val name = sharedPreferences.getString("token","")

        if (name != "") {
            val intent = Intent(applicationContext, MainDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}
