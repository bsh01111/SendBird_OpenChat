package org.androidtown.sendbird_openchat.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtils {

    companion object {

        private val PREFERENCE_KEY_USER_ID = "userId"
        private val PREFERENCE_KEY_NICKNAME = "nickname"
        private val PREFERENCE_KEY_CONNECTED = "connected"

//        private val PREFERENCE_KEY_NOTIFICATIONS = "notifications"
//        private val PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS = "notificationsShowPreviews"
//        private val PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB = "notificationsDoNotDisturb"
//        private val PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM = "notificationsDoNotDisturbFrom"
//        private val PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO = "notificationsDoNotDisturbTo"
//        private val PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT = "channelDistinct"

        lateinit var mAppContext: Context

        public fun init(context: Context) {
            mAppContext = context
        }

        private fun getSharedPreferences(): SharedPreferences {
            return mAppContext.getSharedPreferences("sendbird", Context.MODE_PRIVATE)
        }

        public fun setUserId(user_id: String) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putString(PREFERENCE_KEY_USER_ID, user_id).apply()
        }

        public fun getUserId(): String {
            return getSharedPreferences().getString(PREFERENCE_KEY_USER_ID, "")
        }

        public fun setNickname(nickname: String) {
            val editor: SharedPreferences.Editor = getSharedPreferences()
                .edit()
            editor.putString(PREFERENCE_KEY_NICKNAME, nickname).apply()
        }

        public fun getNickname(): String {
            return getSharedPreferences().getString(PREFERENCE_KEY_NICKNAME, "")
        }

        public fun setConnected(tf: Boolean) {
            val editor: SharedPreferences.Editor = getSharedPreferences().edit()
            editor.putBoolean(PREFERENCE_KEY_CONNECTED, tf).apply()
        }

        public fun getConnected(): Boolean {
            return getSharedPreferences().getBoolean(PREFERENCE_KEY_CONNECTED, false)
        }

        public fun clearAll() {
            val editor = getSharedPreferences().edit()
            editor.clear().apply()
        }
    }
}