package org.androidtown.sendbird_openchat.openchannel

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sendbird.android.*
import org.androidtown.sendbird_openchat.R
import org.androidtown.sendbird_openchat.utils.DateUtils
import org.androidtown.sendbird_openchat.utils.ImageUtils

class OpenChatAdapter(var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val VIEW_TYPE_USER_MESSAGE = 10
        private val VIEW_TYPE_FILE_MESSAGE = 20
        private val VIEW_TYPE_ADMIN_MESSAGE = 30
        private val VIEW_TYPE_STATE_MESSAGE = 40
    }

    private var mMessageList: MutableList<Any> = ArrayList<Any>()
    private lateinit var mItemClickListener: OnItemClickListener
    private lateinit var mItemLongClickListener: OnItemLongClickListener

    /**
     * An interface to implement item click callbacks in the activity or fragment that
     * uses this adapter.
     */
    interface OnItemClickListener {
        fun onUserMessageItemClick(message: UserMessage)

        fun onFileMessageItemClick(message: FileMessage)

        fun onAdminMessageItemClick(message: AdminMessage)

        fun onStateMessageItemClick(message: StateMessage)
    }

    interface OnItemLongClickListener {
        fun onBaseMessageLongClick(message: BaseMessage, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mItemLongClickListener = listener
    }

    fun setMessageList(messages: MutableList<BaseMessage>) {
        mMessageList = messages as MutableList<Any>
        notifyDataSetChanged()
    }

    fun addFirst(message: StateMessage) {
        mMessageList.add(0, message)
        notifyDataSetChanged()
    }

    fun addFirst(message: BaseMessage) {
        mMessageList.add(0, message)
        notifyDataSetChanged()
    }

    fun addLast(message: BaseMessage) {
        mMessageList.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
//            VIEW_TYPE_FILE_MESSAGE->{} // file
            VIEW_TYPE_USER_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_open_chat_user, parent, false)
                return UserMessageHolder(view)
            }
            VIEW_TYPE_ADMIN_MESSAGE -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_open_chat_admin, parent, false)
                return AdminMessageHolder(view)
            }
            else -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_open_chat_state, parent, false)
                return StateMessageHolder(view)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        if (mMessageList[position] is UserMessage) {
            return VIEW_TYPE_USER_MESSAGE
        } else if (mMessageList[position] is AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE
        } else if (mMessageList[position] is FileMessage) {
            return VIEW_TYPE_FILE_MESSAGE
        }
        return VIEW_TYPE_STATE_MESSAGE // 다루지않은 메시지
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (mMessageList[position] is StateMessage) {
            val message = mMessageList[position] as StateMessage

            (holder as StateMessageHolder).bind((message as StateMessage), mItemClickListener)
        } else {
            val message = mMessageList[position] as BaseMessage
            var isNewDay: Boolean = false

//            //이전 메세지를 확인
//            if (position < mMessageList.size - 1) {
//                val prevMessage: BaseMessage = mMessageList[position + 1] as BaseMessage
//
//                //이전 메세지의 날짜가 어제라면 날짜 띄우기
//                if (!DateUtils.hasSameDate(message.createdAt, prevMessage.createdAt)) {
//                    isNewDay = true
//                }
//            } else if (position == mMessageList.size - 1) {
//                isNewDay = true
//            }

            when (holder.itemViewType) {
                VIEW_TYPE_USER_MESSAGE -> {
                    (holder as UserMessageHolder).bind(
                        mContext, (message as UserMessage), isNewDay,
                        mItemClickListener, mItemLongClickListener, position
                    )
                    Log.d("메시지 생성 확인", "${message.message}")
                }
                VIEW_TYPE_ADMIN_MESSAGE -> {
                    (holder as AdminMessageHolder).bind(
                        (message as AdminMessage), isNewDay,
                        mItemClickListener
                    )
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    class UserMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nicknameText: TextView = itemView.findViewById(R.id.text_open_chat_nickname)
        private var messageText: TextView = itemView.findViewById(R.id.text_open_chat_message)
        private var editedText: TextView = itemView.findViewById(R.id.text_open_chat_edited)
        private var timeText: TextView = itemView.findViewById(R.id.text_open_chat_time)
        private var dateText: TextView = itemView.findViewById(R.id.text_open_chat_date)
        private var profileImage: ImageView = itemView.findViewById(R.id.image_open_chat_profile)

        fun bind(
            context: Context, message: UserMessage, isNewDay: Boolean,
            clickListener: OnItemClickListener?, longClickListener: OnItemLongClickListener, position: Int
        ) {

            var sender: User = message.sender

            // 유저가 누군지에 따른 색구별
            if (sender.userId == SendBird.getCurrentUser().userId) {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.openChatNicknameMe))
            } else {
                nicknameText.setTextColor(ContextCompat.getColor(context, R.color.openChatNicknameOther))
            }

            // Show the date if the message was sent on a different date than the previous one.
            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.text = DateUtils.formatDate(message.createdAt)
            } else {
                dateText.visibility = View.GONE
            }

            nicknameText.text = message.sender.nickname
            messageText.text = message.message
            timeText.text = DateUtils.formatTime(message.createdAt)

            if (message.updatedAt > 0) {
                editedText.visibility = View.VISIBLE
            } else {
                editedText.visibility = View.GONE
            }

            //프로필 가져오기
            ImageUtils.displayRoundImageFromUrl(context, message.sender.profileUrl, profileImage)

            if (clickListener != null) {
                itemView.setOnClickListener {
                    clickListener.onUserMessageItemClick(message)
                }
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        longClickListener.onBaseMessageLongClick(message, position)
                        return true
                    }

                })
            }
        }


    }

    class AdminMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_open_chat_message)
        private val dateText: TextView = itemView.findViewById(R.id.text_open_chat_date)

        fun bind(message: AdminMessage, isNewDay: Boolean, listener: OnItemClickListener) {
            messageText.text = message.message

            if (isNewDay) {
                dateText.visibility = View.VISIBLE
                dateText.text = DateUtils.formatDate(message.createdAt)
            } else {
                dateText.visibility = View.GONE
            }

            itemView.setOnClickListener {
                listener.onAdminMessageItemClick(message)
            }
        }

    }

    class StateMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val messageText: TextView = itemView.findViewById(R.id.text_open_chat_message)

        fun bind(message: StateMessage, listener: OnItemClickListener) {
            messageText.text = message.message

            itemView.setOnClickListener {
                listener.onStateMessageItemClick(message)
            }
        }
    }
}