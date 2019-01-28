package org.androidtown.sendbird_openchat.main

import android.app.Application
import com.sendbird.android.SendBird
import org.androidtown.sendbird_openchat.ChatManager
import org.androidtown.sendbird_openchat.utils.PreferenceUtils

class BaseApplication : Application() {

    private val APP_ID = "9D083688-5430-4023-B6ED-6AC8B8C9DCB7" // sendbird app_id
    val VERSION = "3.0.40"

    override fun onCreate() {
        super.onCreate()
        PreferenceUtils.init(applicationContext)
        ChatManager.init(APP_ID,applicationContext)
    }
}