package com.table.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import co.table.agent.android.workspace.WorkSpaceActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onLaunch(view: View) {
        startActivity(Intent(this,WorkSpaceActivity::class.java))
    }
}
