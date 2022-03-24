package com.beijing.arouter_api.facade.template;


import com.beijing.arouter_annotaion.module.RouteMeta;

import java.util.Map;

public
/**
 * Created by jiayk on 2022/3/23
 * Describe:
 */
interface IRouteGroup{
    void loadInto(Map<String, RouteMeta> atlas);
}
