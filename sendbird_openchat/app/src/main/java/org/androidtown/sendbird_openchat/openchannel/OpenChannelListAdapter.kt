package org.androidtown.sendbird_openchat.openchannel

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sendbird.android.OpenChannel
import org.androidtown.sendbird_openchat.R

class OpenChannelListAdapter(var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var mChannelList: MutableList<OpenChannel>
    private var mItemClickListener: OnItemClickListener? =null
    private var mItemLongClickListener: OnItemLongClickListener? =null

    init {
        mChannelList = ArrayList<OpenChannel>()
    }

    interface OnItemClickListener {
        fun onItemClick(channel: OpenChannel)
    }

    interface OnItemLongClickListener {
        fun onItemLongPress(channel: OpenChannel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_open_channel,parent,false)
        return ChannelHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("채널 생성", "")
        Log.d("채널 이름", mChannelList[position].name)
        (holder as ChannelHolder).bind(
            mContext,
            mChannelList[position],
            position,
            mItemClickListener,
            mItemLongClickListener
        )

    }

    override fun getItemCount(): Int {
        return mChannelList.size
    }

    fun setOpenChannelList(channelList: MutableList<OpenChannel>) {
        mChannelList = channelList
        notifyDataSetChanged()
    }

    fun addLast(channel: OpenChannel) {
        mChannelList.add(channel)
        notifyDataSetChanged()
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mItemLongClickListener = listener
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
    }

    class ChannelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //채널 리스트 색깔 리스트
        private val colorList = arrayOf("#ff2de3e1", "#ff35a3fb", "#ff805aff", "#ffcf47fb", "#ffe248c3")
        private var nameText: TextView = itemView.findViewById(R.id.text_open_channel_list_name)
        private var participantCountText: TextView =
            itemView.findViewById(R.id.text_open_channel_list_participant_count)
        private var coloredDecorator: ImageView = itemView.findViewById(R.id.image_open_channel_list_decorator)

        internal fun bind(
            context: Context,
            channel: OpenChannel,
            position: Int,
            clickListener: OnItemClickListener?,
            longClickListener: OnItemLongClickListener?
        ) {
            nameText.text = channel.name

            var participantCount: String = "${channel.participantCount} participants" // 사람수
            participantCountText.text = participantCount

            coloredDecorator.setBackgroundColor(Color.parseColor(colorList[position % colorList.size]))

            //아이탬 클릭시 설정
            if (clickListener != null) {
                itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        clickListener.onItemClick(channel)
                    }

                })
            }
            //길게 눌렀을때 설정
            if (longClickListener != null) {
                itemView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        longClickListener.onItemLongPress(channel)
                        return true
                    }

                })
            }

        }
    }
}