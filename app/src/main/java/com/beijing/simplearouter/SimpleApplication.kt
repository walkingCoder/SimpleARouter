package com.beijing.simplearouter

import android.app.Application
//import androidx.multidex.MultiDex
import com.beijing.arouter_api.launcher.ARouter

/**
 * Created by jiayk on 2022/3/22
 * Describe:
 */
class SimpleApplication : Application(){
    override fun onCreate() {
        super.onCreate()
//        MultiDex.install(this)
        ARouter.init(this)
    }
}