package com.beijing.arouter_api.launcher

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.beijing.arouter_annotaion.enums.RouteType
import com.beijing.arouter_api.callback.NavigationCallback
import com.beijing.arouter_api.exception.NoRouteFoundException
import com.beijing.arouter_api.facade.Postcard
import com.beijing.arouter_api.utils.Consts
import com.beijing.arouter_api.utils.DefaultPoolExecutor
import com.beijing.arouter_api.utils.TextUtils

/**
 * Created by jiayk on 2022/3/21
 * Describe:
 */
class _ARouter private constructor() {

    companion object {
        lateinit var mContext: Context

        @Volatile
        var hasInit: Boolean = false
        val executor = DefaultPoolExecutor.getInstance()
        lateinit var mHandler: Handler
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { _ARouter() }
        fun init(application: Application): Boolean {
            mContext = application
            LogisticsCenter.init(mContext, executor)
            hasInit = true
            mHandler = Handler(Looper.getMainLooper())
            return true
        }

        fun afterInit() {

        }
    }

    fun build(path: String): Postcard {
        if (TextUtils.isEmpty(path)) {
            throw RuntimeException("Parameter is invalid!")
        } else {

            //  PathReplaceService  重定向
            return Postcard(path, extractGroup(path))
        }
    }

    /**
     * Extract the default group from path.
     */
    private fun extractGroup(path: String): String? {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw java.lang.RuntimeException(Consts.TAG.toString() + "Extract the default group failed, the path must be start with '/' and contain more than 2 '/'!")
        }
        return try {
            val defaultGroup = path.substring(1, path.indexOf("/", 1))
            if (TextUtils.isEmpty(defaultGroup)) {
                throw RuntimeException(Consts.TAG.toString() + "Extract the default group failed! There's nothing between 2 '/'!")
            } else {
                defaultGroup
            }
        } catch (e: Exception) {
            null
        }
    }

    fun navigation(
        context: Context?,
        postCard: Postcard,
        requestCode: Int,
        callback: NavigationCallback?
    ): Object? {
        // PretreatmentService   预处理
        postCard.context = context ?: mContext
        try {
            LogisticsCenter.completion(postCard)
        } catch (e: NoRouteFoundException) {

            callback?.onLost(postCard)
            // DegradeService  没有找到，降级处理

        }
        callback?.onFound(postCard)

        if (!postCard.isGreenChannel) {
            // interceptorServices    拦截处理
            _navigation(postCard, requestCode, callback)
        } else {
            _navigation(postCard, requestCode, callback)
        }
        return null
    }

    @SuppressLint("WrongConstant")
    private fun _navigation(postCard: Postcard, requestCode: Int, callback: NavigationCallback?) {
        val currentContext = postCard.context
        when (postCard.type) {
            RouteType.ACTIVITY -> {
                var intent = Intent(currentContext, postCard.destination)
                if (postCard.optionsBundle != null) {
                    intent.putExtras(postCard.optionsBundle!!)
                }
                val flags = postCard.flags
                if (flags != 0) {
                    intent.flags = flags
                }
                if (currentContext !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val action = postCard.action
                if (!TextUtils.isEmpty(action)) {
                    intent.action = action
                }

                runOnMainThread {
                    startActivity(
                        requestCode,
                        currentContext!!,
                        intent,
                        postCard,
                        callback
                    )
                }
            }
        }
    }

    private fun startActivity(
        requestCode: Int,
        currentContext: Context,
        intent: Intent,
        postCard: Postcard,
        callback: NavigationCallback?
    ) {
        if (requestCode >= 0) {  // Need start for result
            if (currentContext is Activity) {
                ActivityCompat.startActivityForResult(
                    currentContext as Activity,
                    intent,
                    requestCode,
                    postCard.optionsBundle
                )
            } else {
//                _ARouter.logger.warning(
//                    Consts.TAG,
//                    "Must use [navigation(activity, ...)] to support [startActivityForResult]"
//                )
            }
        } else {
            ActivityCompat.startActivity(currentContext, intent, postCard.optionsBundle)
        }

        if (-1 != postCard.enterAnim && -1 != postCard.exitAnim && currentContext is Activity) {    // Old version.
            currentContext.overridePendingTransition(
                postCard.enterAnim,
                postCard.exitAnim
            )
        }

        callback?.onArrival(postCard)
    }


    fun runOnMainThread(runnable: Runnable) {
        if (Looper.getMainLooper().thread != Thread.currentThread()) {
            mHandler.post(runnable)
        } else {
            runnable.run()
        }
    }

}