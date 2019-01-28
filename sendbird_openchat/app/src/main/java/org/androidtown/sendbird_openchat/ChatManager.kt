package org.androidtown.sendbird_openchat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.widget.Toast
import com.sendbird.android.*
import org.androidtown.sendbird_openchat.main.LoginActivity
import org.androidtown.sendbird_openchat.openchannel.OpenChannelActivity
import org.androidtown.sendbird_openchat.utils.PreferenceUtils

object ChatManager {
    const val ONPAUSE =0

    const val LOGIN = 10
    const val LOGINERROR = 11
    const val UPDATEUSERINFO = 12
    const val UPDATEUSERINFOERROR = 13

    const val LOGOUT = 20
    const val CREATEOPENCHANNEL = 21
    const val CREATEOPENCHANNELERROR = 22
    const val REFRESHCHANNELLIST = 23
    const val REFRESHCHANNELLISTERROR = 24
    const val LOADNEXTCHANNELLIST = 25
    const val LOADNEXTCHANNELLISTERROR = 26

    const val ENTERCHANNEL = 30
    const val ENTERCHANNELERROR = 31
    const val SENDUSERMESSAGE = 32
    const val SENDUSERMESSAGEERROR = 33
    const val LOADMESSAGELIST = 34
    const val LOADNEXTMESSAGELIST = 35
    const val LOADMESSAGELISTERROR = 36


    //sendbird 연결
    fun init(APP_ID: String, applicationContext: Context) {
        SendBird.init(APP_ID, applicationContext)
    }


    fun login(userId: String, userNickname: String, handler: Handler) {
        SendBird.connect(userId, object : SendBird.ConnectHandler {
            override fun onConnected(user: User?, e: SendBirdException?) {
                if (e == null) {
                    handler.sendEmptyMessage(LOGIN)
                } else {
                    handler.sendEmptyMessage(LOGINERROR)
                }
            }
        })
    }

    fun updateCurrentUserInfo(userNickname: String, handler: Handler) {
        SendBird.updateCurrentUserInfo(userNickname, null, object : SendBird.UserInfoUpdateHandler {
            override fun onUpdated(e: SendBirdException?) {
                var message = handler.obtainMessage()
                message.what = UPDATEUSERINFO
                message.obj = userNickname
            }
        })
    }


    fun logout(handler: Handler) {
        SendBird.disconnect(object : SendBird.DisconnectHandler {
            override fun onDisconnected() {
                handler.sendEmptyMessage(LOGOUT)
            }
        })

    }


    fun createOpenChannel(CannelName: String, handler: Handler) {
        OpenChannel.createChannelWithOperatorUserIds(CannelName, null, null, null,
            object : OpenChannel.OpenChannelCreateHandler {
                override fun onResult(openChannel: OpenChannel?, e: SendBirdException?) {
                    if (e == null) {
                        handler.sendEmptyMessage(CREATEOPENCHANNEL)
                    } else {
                        handler.sendEmptyMessage(CREATEOPENCHANNELERROR)
                    }
                }
            })
    }

    fun refreshChannelList(numChannels: Int, mChannelListQuery: OpenChannelListQuery, handler: Handler) {
        mChannelListQuery.setLimit(numChannels)
        mChannelListQuery.next(object : OpenChannelListQuery.OpenChannelListQueryResultHandler {
            override fun onResult(list: MutableList<OpenChannel>?, e: SendBirdException?) {
                if (e == null) {
                    var message = handler.obtainMessage()
                    message.what = REFRESHCHANNELLIST
                    message.obj = list
                    handler.sendMessage(message)
                } else {
                    handler.sendEmptyMessage(REFRESHCHANNELLISTERROR)
                }
            }

        })
    }

    fun loadNextChannelList(mChannelListQuery: OpenChannelListQuery, handler: Handler) {
        if (mChannelListQuery != null) {
            mChannelListQuery.next(object : OpenChannelListQuery.OpenChannelListQueryResultHandler {
                override fun onResult(list: MutableList<OpenChannel>?, e: SendBirdException?) {
                    if (e == null) {
                        var message = handler.obtainMessage()
                        message.what = LOADNEXTCHANNELLIST
                        message.obj = list
                        handler.sendMessage(message)
                    } else {
                        handler.sendEmptyMessage(LOADNEXTCHANNELLISTERROR)
                    }
                }
            })
        }
    }

    fun enterChannel(channelUrl: String, handler: Handler) {
        OpenChannel.getChannel(channelUrl, object : OpenChannel.OpenChannelGetHandler {
            override fun onResult(openChannel: OpenChannel?, e: SendBirdException?) {
                if (e != null) {
                    handler.sendEmptyMessage(ENTERCHANNELERROR)
                    return
                }
                openChannel?.enter(object : OpenChannel.OpenChannelEnterHandler {
                    override fun onResult(e: SendBirdException?) {
                        if (e != null) {
                            handler.sendEmptyMessage(ENTERCHANNELERROR)
                            return
                        }
                        var message = handler.obtainMessage()
                        message.what = ENTERCHANNEL
                        message.obj = openChannel
                        handler.sendMessage(message)
                    }
                })
            }


        })
    }

    fun SendUserMessage(text: String, channel: OpenChannel, handler: Handler) {
        channel.sendUserMessage(text, object : BaseChannel.SendUserMessageHandler {
            override fun onSent(userMessage: UserMessage?, e: SendBirdException?) {
                if (e != null) {
                    handler.sendEmptyMessage(SENDUSERMESSAGEERROR)
                    return

                }
                if (userMessage != null) {
                    var message = handler.obtainMessage()
                    message.what = SENDUSERMESSAGE
                    message.obj = userMessage
                    handler.sendMessage(message)
                }
            }
        })
    }

    fun loadMessageList(numMessages: Int, mPrevMessageListQuery: PreviousMessageListQuery, handler: Handler) {
        mPrevMessageListQuery.load(numMessages, true, object : PreviousMessageListQuery.MessageListQueryResult {
            override fun onResult(list: MutableList<BaseMessage>?, e: SendBirdException?) {
                if (e != null) {
                    handler.sendEmptyMessage(LOADMESSAGELISTERROR)
                    return
                }
                if (list != null) {
                    var message = handler.obtainMessage()
                    message.what = LOADMESSAGELIST
                    message.obj = list
                    handler.sendMessage(message)
                }
            }

        })
    }

    fun loadNextMessageList(numMessages: Int, mPrevMessageListQuery: PreviousMessageListQuery, handler: Handler) {
        mPrevMessageListQuery.load(numMessages, true, object : PreviousMessageListQuery.MessageListQueryResult {
            override fun onResult(list: MutableList<BaseMessage>?, e: SendBirdException?) {
                if (e != null) {
                    handler.sendEmptyMessage(LOADMESSAGELISTERROR)
                    return
                }
                if (list != null) {
                    var message = handler.obtainMessage()
                    message.what = LOADNEXTMESSAGELIST
                    message.obj = list
                    handler.sendMessage(message)
                }
            }
        })
    }

    fun ExitChannel(channel: OpenChannel) {
        channel.exit(object : OpenChannel.OpenChannelExitHandler {
            override fun onResult(e: SendBirdException?) {
                if (e != null) {
                    //error
                    e.printStackTrace()
                    return
                }
            }
        })
    }



    fun addConnectionManagementHandler(handlerId: String, handler: Handler) {
        SendBird.addConnectionHandler(handlerId, object : SendBird.ConnectionHandler {
            override fun onReconnectStarted() {
            }

            override fun onReconnectSucceeded() {
                var message = handler.obtainMessage()
                message.what = ONPAUSE
                message.obj = true
                handler.sendMessage(message)
            }

            override fun onReconnectFailed() {
            }
        })

        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
            var message = handler.obtainMessage()
            message.what = ONPAUSE
            message.obj = false
            handler.sendMessage(message)
        } else if (SendBird.getConnectionState() == SendBird.ConnectionState.CLOSED) {
            var userid: String = PreferenceUtils.getUserId()
            SendBird.connect(userid, object : SendBird.ConnectHandler {
                override fun onConnected(user: User?, e: SendBirdException?) {
                    if (e != null) {
                        return
                    }

                    var message = handler.obtainMessage()
                    message.what = ONPAUSE
                    message.obj = false
                    handler.sendMessage(message)
                }

            })
        }
    }


    interface ConnectionManagementHandler {
        /**
         * A callback for when connected or reconnected to refresh.
         *
         * @param reconnect Set false if connected, true if reconnected.
         */
        fun onConnected(reconnect: Boolean)
    }
}