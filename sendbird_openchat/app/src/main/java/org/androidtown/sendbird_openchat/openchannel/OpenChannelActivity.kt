package org.androidtown.sendbird_openchat.openchannel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_open_channel.*
import org.androidtown.sendbird_openchat.R

class OpenChannelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_channel)

        setSupportActionBar(toolbar_open_channel)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_left_white_24_dp)
        }

        if(savedInstanceState ==null){
        //open channels을 불러온다
            val fragment:Fragment =OpenChannelListFragment.newInstance()

            val manager:FragmentManager = supportFragmentManager
            manager.popBackStack()

            manager.beginTransaction().replace(R.id.container_open_channel,fragment).commit()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun setActionBarTitle(title: String) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }
}
