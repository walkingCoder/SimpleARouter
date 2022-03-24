package com.beijing.simplearouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beijing.arouter_annotaion.annotation.Route

@Route(path = "/route/second")
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }
}