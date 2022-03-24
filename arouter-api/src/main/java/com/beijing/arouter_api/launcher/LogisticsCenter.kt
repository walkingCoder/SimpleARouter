package com.beijing.arouter_api.launcher

import android.content.Context
import com.beijing.arouter_api.core.Warehouse
import com.beijing.arouter_api.exception.NoRouteFoundException
import com.beijing.arouter_api.facade.Postcard
import com.beijing.arouter_api.facade.template.IRouteGroup
import com.beijing.arouter_api.facade.template.IRouteRoot
import com.beijing.arouter_api.utils.ClassUtils
import com.beijing.arouter_api.utils.Consts
import com.beijing.arouter_api.utils.Consts.DOT
import com.beijing.arouter_api.utils.Consts.ROUTE_ROOT_PAKCAGE
import com.beijing.arouter_api.utils.Consts.SDK_NAME
import com.beijing.arouter_api.utils.Consts.SEPARATOR
import com.beijing.arouter_api.utils.Consts.SUFFIX_ROOT
import com.beijing.arouter_api.utils.Consts.TAG
import com.beijing.arouter_api.utils.DefaultPoolExecutor

/**
 * Created by jiayk on 2022/3/21
 * Describe:
 */
class LogisticsCenter {

    companion object {
        lateinit var mContext: Context
        lateinit var executor: DefaultPoolExecutor
        var registerByPlugin : Boolean = false

        fun init(context: Context, executor: DefaultPoolExecutor) {
            mContext = context
            this.executor = executor

           var routerMap = setOf<String>()

            routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE)

            // 将routerMap 放sp 缓存起来， 下次直接从sp取， version更新，重新赋值更新sp

            for (className in routerMap){
                if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT)){
                    (Class.forName(className).getConstructor().newInstance() as IRouteRoot).loadInto(Warehouse.groupsIndex)
                }
            }
        }

        fun completion(postCard: Postcard) {
            var routeMate = Warehouse.routes[postCard.path]
            if (null == routeMate){
                if (!Warehouse.groupsIndex.containsKey(postCard.group)){
                    throw NoRouteFoundException(TAG.toString() + "There is no route match the path [" + postCard.path + "], in group [" + postCard.group + "]")
                }else {
                    addRouteGroupDynamic(postCard.group, null)
                    completion(postCard)
                }
            }else {
                postCard.destination = routeMate.destination
                postCard.type = routeMate.type
                postCard.priority = routeMate.priority
                postCard.extra = routeMate.extra
            }
        }

        private fun addRouteGroupDynamic(groupName: String?, group : IRouteGroup?) {
            if (Warehouse.groupsIndex.containsKey(groupName)){
                Warehouse.groupsIndex.get(groupName)?.getConstructor()?.newInstance()?.loadInto(Warehouse.routes)
                Warehouse.groupsIndex.remove(groupName)
            }

            group?.loadInto(Warehouse.routes)
        }
    }
}