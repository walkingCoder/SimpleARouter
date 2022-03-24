package com.beijing.arouter_api.launcher

import android.app.Application
import android.content.Context
import com.beijing.arouter_api.callback.NavigationCallback
import com.beijing.arouter_api.exception.InitException
import com.beijing.arouter_api.facade.Postcard

/**
 * Created by jiayk on 2022/3/21
 * Describe:
 */
class ARouter private constructor() {

    companion object {
        @Volatile
        var hasInit: Boolean = false
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            if (!hasInit){
                throw InitException("ARouter::Init::Invoke init(context) first!")
            }
            ARouter()
        }

        fun init(application: Application) {
            if (!hasInit){
                hasInit = _ARouter.init(application)

                if (hasInit){
                    _ARouter.afterInit()
                }
            }
        }
    }


    fun build(path:String):Postcard{
        return _ARouter.instance.build(path)
    }

    fun navigation(
        context: Context?,
        postCard: Postcard,
        requestCode: Int,
        callback: NavigationCallback?
    ): Object? {
        return _ARouter.instance.navigation(context, postCard, requestCode, callback)
    }

}