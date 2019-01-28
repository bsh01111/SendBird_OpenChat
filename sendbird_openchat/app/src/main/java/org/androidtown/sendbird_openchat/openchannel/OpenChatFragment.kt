package org.androidtown.sendbird_openchat.openchannel

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sendbird.android.*
import org.androidtown.sendbird_openchat.ChatManager
import org.androidtown.sendbird_openchat.R

class OpenChatFragment : Fragment() {

    /**
     * To create an instance of this fragment, a Channel URL should be passed.
     */
    companion object {

        private val CHANNEL_LIST_LIMIT = 30
        private val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_OPEN_CHAT"

        fun newInstance(channelUrl: String): OpenChatFragment {
            val fragment = OpenChatFragment()

            val args = Bundle()
            args.putString(OpenChannelListFragment.EXTRA_OPEN_CHANNEL_URL, channelUrl)
            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: OpenChatAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mRootLayout: View
    private lateinit var mMessageEditText: EditText
    private lateinit var mMessageSendButton: Button
    private lateinit var mUploadFileButton: ImageButton
    private lateinit var mCurrentEventLayout: View
    private lateinit var mCurrentEventText: TextView

    private lateinit var mChannel: OpenChannel
    private lateinit var mChannelUrl: String
    private lateinit var mPrevMessageListQuery: PreviousMessageListQuery


    private val m_Handler = OpenChatFragmentHandler(this)


    class OpenChatFragmentHandler(val act: OpenChatFragment) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ChatManager.ONPAUSE -> act.ResumeStart(msg.obj as Boolean)

                ChatManager.ENTERCHANNEL -> act.enterChannelSucceed(msg.obj as OpenChannel)

                ChatManager.ENTERCHANNELERROR->act.enterChannelFail()

                ChatManager.SENDUSERMESSAGE -> act.sendUserMessageSucceed(msg.obj as UserMessage)

                ChatManager.SENDUSERMESSAGEERROR->act.sendUserMessageFail()

                ChatManager.LOADMESSAGELIST -> act.loadMessageListSucceed(msg.obj as MutableList<BaseMessage>)

                ChatManager.LOADNEXTMESSAGELIST -> act.loadNextMessageListSucceed(msg.obj as MutableList<BaseMessage>)

                ChatManager.LOADMESSAGELISTERROR ->act.loadMessageListFail()


            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragnment_open_chat, container, false)

        retainInstance = true
        setHasOptionsMenu(true)

        mRootLayout = rootView.findViewById(R.id.layout_open_chat_root)
        mRecyclerView = rootView.findViewById(R.id.recycler_open_channel_chat)

        mCurrentEventLayout = rootView.findViewById(R.id.layout_open_chat_current_event)
        mCurrentEventText = rootView.findViewById(R.id.text_open_chat_current_event)

        setUpChatAdapter()
        setUpRecyclerView()

        // Set up chat box
        mMessageSendButton = rootView.findViewById(R.id.button_open_channel_chat_send)
        mMessageEditText = rootView.findViewById(R.id.edittext_chat_message)

        mMessageEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable) {
                mMessageSendButton.isEnabled = s.isNotEmpty()
            }
        })

        mMessageSendButton.isEnabled = false
        mMessageSendButton.setOnClickListener {
            val userInput = mMessageEditText.text.toString()
            if (userInput.length > 0) {
                sendUserMessage(userInput)
                mMessageEditText.setText("")
            }
        }

        mUploadFileButton = rootView.findViewById(R.id.button_open_channel_chat_upload)

        mChannelUrl = arguments!!.getString(OpenChannelListFragment.EXTRA_OPEN_CHANNEL_URL)


        return rootView
    }

    override fun onResume() {
        super.onResume()
        ChatManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID,m_Handler)
    }

    fun ResumeStart(result :Boolean){
        if(result){
            refresh()
        }
        else{
            refreshFirst()
        }
    }


    override fun onDestroyView() {
        ChatManager.ExitChannel(mChannel)
        super.onDestroyView()
    }

    private fun setUpChatAdapter() {
        mChatAdapter = OpenChatAdapter(activity!!)

        //아이템 클릭 이벤트
        mChatAdapter.setOnItemClickListener(object : OpenChatAdapter.OnItemClickListener {
            override fun onUserMessageItemClick(message: UserMessage) {}

            override fun onFileMessageItemClick(message: FileMessage) {}

            override fun onAdminMessageItemClick(message: AdminMessage) {}

        })

        mChatAdapter.setOnItemLongClickListener(object : OpenChatAdapter.OnItemLongClickListener {
            override fun onBaseMessageLongClick(message: BaseMessage, position: Int) {
            }

        })
    }

    private fun setUpRecyclerView() {
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.reverseLayout = true
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mChatAdapter

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.itemCount - 1) {
                    loadNextMessageList(CHANNEL_LIST_LIMIT)
                }
            }
        })
    }

    private fun sendUserMessage(text: String) {
       ChatManager.SendUserMessage(text,mChannel,m_Handler)
    }

    private fun sendUserMessageSucceed(userMessage:UserMessage?){
        if (userMessage != null) {
            mChatAdapter.addFirst(userMessage)
        }
    }

    private fun sendUserMessageFail(){
        // Error!
        Toast.makeText(context, "Send User Message failed", Toast.LENGTH_SHORT).show()
    }

    private fun refreshFirst() {
        enterChannel(mChannelUrl)
    }

    private fun enterChannel(channelUrl: String) {
        ChatManager.enterChannel(channelUrl,m_Handler)
    }
    private fun enterChannelSucceed(openChannel: OpenChannel){
        mChannel = openChannel
        if (activity != null) {
            //엑션바 이름 설정
            (activity as OpenChannelActivity).setActionBarTitle(mChannel.name)
        }
        refresh()
    }
    private fun enterChannelFail(){
        // Error!
        Toast.makeText(context, "EnterChannel failed", Toast.LENGTH_SHORT).show()
    }

    private fun refresh() {
        loadInitialMessageList(CHANNEL_LIST_LIMIT)
    }

    private fun loadInitialMessageList(numMessages: Int) {
        mPrevMessageListQuery = mChannel.createPreviousMessageListQuery()
        ChatManager.loadMessageList(numMessages,mPrevMessageListQuery,m_Handler)
    }

    private fun loadMessageListSucceed(list:MutableList<BaseMessage>){
        mChatAdapter.setMessageList(list)
    }

    private fun loadMessageListFail(){
        // Error!
        Toast.makeText(context, "LoadMessageList failed", Toast.LENGTH_SHORT).show()
    }

    private fun loadNextMessageList(numMessages: Int) {
        ChatManager.loadNextMessageList(numMessages,mPrevMessageListQuery, m_Handler)
    }

    private fun loadNextMessageListSucceed(list : MutableList<BaseMessage>) {
        if (list != null) {
            for (i in 0..list.size - 1) {
                mChatAdapter.addLast(list[i])
            }
        }
    }
}