package com.beijing.simplearouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.beijing.arouter_annotaion.annotation.Route
import com.beijing.arouter_api.launcher.ARouter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.btn_to).setOnClickListener {
            ARouter.instance.build("/route/second").navigation()
        }
    }
}