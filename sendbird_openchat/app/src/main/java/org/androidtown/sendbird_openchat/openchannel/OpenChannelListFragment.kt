package org.androidtown.sendbird_openchat.openchannel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sendbird.android.OpenChannel
import com.sendbird.android.OpenChannelListQuery
import com.sendbird.android.SendBirdException
import org.androidtown.sendbird_openchat.ChatManager
import org.androidtown.sendbird_openchat.R

class OpenChannelListFragment : Fragment() {


    companion object {
        private const val INTENT_REQUEST_NEW_OPEN_CHANNEL = 402
        val EXTRA_OPEN_CHANNEL_URL = "OPEN_CHANNEL_URL"

        private const val CHANNEL_LIST_LIMIT = 7
        private const val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_OPEN_CHANNEL_LIST"

        fun newInstance(): OpenChannelListFragment {
            var fragment: OpenChannelListFragment = OpenChannelListFragment()
            return fragment
        }
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mChannelListAdapter: OpenChannelListAdapter
    private lateinit var mSwipeRefresh: SwipeRefreshLayout
    private lateinit var mCreateChannelFab: FloatingActionButton

    private lateinit var mChannelListQuery: OpenChannelListQuery

    private val m_Handler = OpenChannelListFragmentHandler(this)

    class OpenChannelListFragmentHandler(val act: OpenChannelListFragment) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ChatManager.ONPAUSE->act.ResumeStart(msg.obj as Boolean)

                ChatManager.REFRESHCHANNELLIST -> act.refreshChannelListSucceed(msg.obj as MutableList<OpenChannel>)

                ChatManager.REFRESHCHANNELLISTERROR ->act.refreshChannelListFail()

                ChatManager.LOADNEXTCHANNELLIST -> act.loadNextChannelListSucceed(msg.obj as MutableList<OpenChannel>)

                ChatManager.LOADNEXTCHANNELLISTERROR -> act.loadNextChannelListFail()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_open_channel_list, container, false)

        retainInstance = true
        setHasOptionsMenu(true)


        mRecyclerView = rootView.findViewById(R.id.recycler_open_channel_list)
        mChannelListAdapter = OpenChannelListAdapter(context!!)
        mSwipeRefresh = rootView.findViewById(R.id.swipe_layout_open_channel_list)

        mSwipeRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mSwipeRefresh.isRefreshing = true
                refresh()
            }

        })

        mCreateChannelFab = rootView.findViewById(R.id.fab_open_channel_list)
        mCreateChannelFab.setOnClickListener {
            val intent: Intent = Intent(activity, CreateOpenChannelActivity::class.java)
            startActivityForResult(intent, INTENT_REQUEST_NEW_OPEN_CHANNEL)
        }

        setUpRecyclerView()
        setUpChannelListAdapter()

        return rootView

    }

    override fun onResume() {
        super.onResume()
        ChatManager.addConnectionManagementHandler(EXTRA_OPEN_CHANNEL_URL,m_Handler)
    }

    fun ResumeStart(result :Boolean){
        refresh()
    }


    //adapter설정
    private fun setUpRecyclerView() {
        mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mChannelListAdapter
        mRecyclerView.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

        // 스크롤이 아래로 닿으면 , 더많은 리스트를 가져옴
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.itemCount - 1) {
                    loadNextChannelList()
                }
            }
        })
    }

    //리스트 아이템 클릭시
    private fun setUpChannelListAdapter() {
        mChannelListAdapter.setOnItemClickListener(object : OpenChannelListAdapter.OnItemClickListener {
            override fun onItemClick(channel: OpenChannel) {
                val channelUrl = channel.url
                val fragment = OpenChatFragment.newInstance(channelUrl)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container_open_channel, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        mChannelListAdapter.setOnItemLongClickListener(object : OpenChannelListAdapter.OnItemLongClickListener {
            override fun onItemLongPress(channel: OpenChannel) {
                //길게 누를시
            }
        })
    }

    //새로고침
    private fun refresh() {
        refreshChannelList(CHANNEL_LIST_LIMIT)
    }

    //새로고침
    private fun refreshChannelList(numChannels: Int) {
        mChannelListQuery = OpenChannel.createOpenChannelListQuery()
        ChatManager.refreshChannelList(numChannels, mChannelListQuery, m_Handler)
    }

    private fun refreshChannelListSucceed(list: MutableList<OpenChannel>?) {
        if (list != null) {
            mChannelListAdapter.setOpenChannelList(list)
        }

        if (mSwipeRefresh.isRefreshing) {
            mSwipeRefresh.isRefreshing = false
        }
    }

    private fun refreshChannelListFail() {
        // Error!
        Toast.makeText(context, "RefreshChannelList failed", Toast.LENGTH_SHORT).show()
    }

    private fun loadNextChannelList() {
        ChatManager.loadNextChannelList(mChannelListQuery,m_Handler)
    }

    private fun loadNextChannelListSucceed(list: MutableList<OpenChannel>?){
        if (list != null) {
            for (i in 0..list.size - 1) {
                var channel: OpenChannel = list[i]
                mChannelListAdapter.addLast(channel)
            }
        }
    }

    private fun loadNextChannelListFail(){
        // Error!
        Toast.makeText(context, "LoadNextChannelList failed", Toast.LENGTH_SHORT).show()
    }
}