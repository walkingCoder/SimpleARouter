package com.beijing.arouter_api.callback

import com.beijing.arouter_api.facade.Postcard

/**
 * Created by jiayk on 2022/3/22
 * Describe:
 */
interface NavigationCallback {
    fun onFound(postCard: Postcard)
    fun onLost(postCard: Postcard)
    fun onArrival(postCard: Postcard)
    fun onInterrupt(postCard: Postcard)
}