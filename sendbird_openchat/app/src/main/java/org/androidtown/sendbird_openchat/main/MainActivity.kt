package org.androidtown.sendbird_openchat.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toolbar
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import kotlinx.android.synthetic.main.activity_main.*
import org.androidtown.sendbird_openchat.ChatManager
import org.androidtown.sendbird_openchat.utils.PreferenceUtils
import org.androidtown.sendbird_openchat.R
import org.androidtown.sendbird_openchat.openchannel.OpenChannelActivity
import java.net.Inet4Address

class MainActivity : AppCompatActivity() {

    private val m_Handler = MainActivityHandler(this)

    class MainActivityHandler(val act:MainActivity) : Handler(){

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                ChatManager.LOGOUT -> act.LogOut()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main)


        linear_layout_open_channels.setOnClickListener{
            val intent:Intent = Intent(this,OpenChannelActivity::class.java)
            startActivity(intent)
        }

        button_disconnect.setOnClickListener{
            //logout
            disconnect()
        }
    }

    private fun disconnect(){
        ChatManager.logout(m_Handler)
    }

    private fun LogOut(){
        PreferenceUtils.setConnected(false)
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
