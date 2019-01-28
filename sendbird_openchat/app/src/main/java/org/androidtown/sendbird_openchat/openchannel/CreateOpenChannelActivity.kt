package org.androidtown.sendbird_openchat.openchannel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import com.sendbird.android.OpenChannel
import com.sendbird.android.SendBirdException
import org.androidtown.sendbird_openchat.ChatManager
import org.androidtown.sendbird_openchat.R
import org.androidtown.sendbird_openchat.main.LoginActivity
import org.androidtown.sendbird_openchat.utils.PreferenceUtils

class CreateOpenChannelActivity : AppCompatActivity() {

    private lateinit var mNameEditText: TextInputEditText
    private var enableCreate: Boolean = false
    private lateinit var mCreateButton: Button

    private val m_Handler = CreateOpenChannelActivityHandler(this)

    class CreateOpenChannelActivityHandler(val act: CreateOpenChannelActivity) : Handler(){

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                ChatManager.CREATEOPENCHANNEL -> act.CreateOpenChannelSucceed()

                ChatManager.UPDATEUSERINFOERROR -> act.CreateOpenChannelFail()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_open_channel)

        var toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_create_open_channel)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_left_white_24_dp)
        }

        mNameEditText = findViewById(R.id.edittext_create_open_channel_name)
        mCreateButton = findViewById(R.id.button_create_open_channel)

        mCreateButton.setOnClickListener {
            createOpenChannel(mNameEditText.text.toString())
        }

        mCreateButton.isEnabled = enableCreate

        mNameEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 0) {
                    if (enableCreate) {
                        mCreateButton.isEnabled = false
                        enableCreate = false
                    }
                } else {
                    if (!enableCreate) {
                        mCreateButton.isEnabled = true
                        enableCreate = true
                    }
                }
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id:Int = item.itemId

        if (id == android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createOpenChannel(name:String){
        ChatManager.createOpenChannel(name,m_Handler)
    }

    private fun CreateOpenChannelSucceed(){
        val intent: Intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    private fun CreateOpenChannelFail(){
        // Error!
        Toast.makeText(
            this, "CreateOpenChannel failed",
            Toast.LENGTH_SHORT
        ).show()
    }


}