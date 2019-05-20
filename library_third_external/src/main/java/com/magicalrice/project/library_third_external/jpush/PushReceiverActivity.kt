package com.magicalrice.project.library_third_external.jpush

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.magicalrice.project.library_base.base.AppManager
import com.magicalrice.project.library_base.base.SPUtils

class PushReceiverActivity : AppCompatActivity() {
    private lateinit var list: MutableList<Activity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        toIntent(intent)
    }

    private fun toIntent(intentHas: Intent?) {
        list = AppManager.getInstance().getActivityList()
        if (list.isNotEmpty()) {
            skip(intentHas)
        } else {
//            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    private fun skip(intent: Intent?) {
        val bundle = intent?.getBundleExtra("extras")
        val noticeType = bundle?.getInt("noticeType", -1)
        val intents = arrayOfNulls<Intent>(2)
//        val mainIntent = Intent(this,MainActivity::class.java)
//        intents[0] = mainIntent
//        if (noticeType == 2) {
//            val noticeIntent = Intent(this,MessageActivity::class.java)
//            intents[1] = noticeIntent
//        }
        startActivities(intents)
    }

    private fun check(): Boolean {
        val token = SPUtils.getInstance().get("token", "") as String
        if (token.isNotEmpty()) {
            return true
        } else {
//            val intent = Intent(this, SplashActivity::class.java)
//            startActivity(intent)
        }
        return false
    }

    companion object {
        fun start(context: Context?, intent: Intent?) {
            context?.startActivity(intent)
        }
    }
}