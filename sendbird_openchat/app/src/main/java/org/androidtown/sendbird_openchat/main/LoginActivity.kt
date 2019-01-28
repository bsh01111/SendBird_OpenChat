package org.androidtown.sendbird_openchat.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.widget.Button
import android.widget.Toast
import org.androidtown.sendbird_openchat.ChatManager
import org.androidtown.sendbird_openchat.utils.PreferenceUtils
import org.androidtown.sendbird_openchat.R

class LoginActivity : AppCompatActivity() {

    private lateinit var mLoginLayout:CoordinatorLayout
    private lateinit var mButton:Button

    private val m_Handler = LoginActivityHandler(this)

    class LoginActivityHandler(val act:LoginActivity) : Handler(){

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                ChatManager.LOGIN->act.LogIn()

                ChatManager.LOGINERROR->act.LoginError()

                ChatManager.UPDATEUSERINFO->act.Update(msg.obj.toString())

                ChatManager.UPDATEUSERINFOERROR->act.UpdateError()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mLoginLayout = findViewById(R.id.layout_login)
        val mUserIdEditText: TextInputEditText = findViewById(R.id.edittext_login_user_id)
        val mUserNicknameEditText: TextInputEditText = findViewById(R.id.edittext_login_user_nickname)

        mUserIdEditText.setText(PreferenceUtils.getUserId())
        mUserNicknameEditText.setText(PreferenceUtils.getNickname())

        mButton = findViewById(R.id.button_login_connect)
        mButton.setOnClickListener{
            var userId = mUserIdEditText.text.toString()
//             Remove all spaces from userID
            userId = userId.replace("\\s".toRegex(), "")

            val userNickname = mUserNicknameEditText.text.toString()

            PreferenceUtils.setUserId(userId)
            PreferenceUtils.setNickname(userNickname)

            connectToSendBird(userId, userNickname)
        }

        mUserIdEditText.setSelectAllOnFocus(true)
        mUserNicknameEditText.setSelectAllOnFocus(true)

    }


    override fun onStart() {
        super.onStart()
        if(PreferenceUtils.getConnected()){
            connectToSendBird(PreferenceUtils.getUserId(), PreferenceUtils.getNickname())
        }
    }

    private fun connectToSendBird(userId: String, userNickname: String) {
        mButton.isEnabled = false
        ChatManager.login(userId,userNickname, m_Handler)
        ChatManager.updateCurrentUserInfo(userNickname,m_Handler)
    }

    private fun LogIn() {
        PreferenceUtils.setConnected(true)

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun LoginError(){
        // Error!
        Toast.makeText(
            this@LoginActivity, "Login to SendBird failed",
            Toast.LENGTH_SHORT
        ).show()

        Log.d("아이디", PreferenceUtils.getUserId())
        Log.d("닉네임", PreferenceUtils.getNickname())
        // 로그인실패
        showSnackbar("Login to SendBird failed")
        mButton.isEnabled = true
        PreferenceUtils.setConnected(false)
    }


    private fun Update(userNickname : String){
        PreferenceUtils.setNickname(userNickname)
    }

    private fun UpdateError(){
        // Error!
        Toast.makeText(
            this@LoginActivity, "Update user nickname failed",
            Toast.LENGTH_SHORT
        ).show()
        showSnackbar("Update user nickname failed")
    }


    private fun showSnackbar(text:String){
        val snackbar:Snackbar = Snackbar.make(mLoginLayout, text, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }
}

