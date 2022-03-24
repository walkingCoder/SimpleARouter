package com.beijing.arouter_api.core

import com.beijing.arouter_annotaion.module.RouteMeta
import com.beijing.arouter_api.facade.template.IRouteGroup

/**
 * Created by jiayk on 2022/3/21
 * Describe:
 */
object Warehouse {
    var groupsIndex : MutableMap<String,Class<out IRouteGroup>> = mutableMapOf()
    var routes: Map<String, RouteMeta> = mutableMapOf()

}