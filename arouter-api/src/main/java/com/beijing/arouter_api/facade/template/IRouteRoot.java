package com.beijing.arouter_api.facade.template;

import java.util.Map;

public
/**
 * Created by jiayk on 2022/3/23
 * Describe:
 */
interface IRouteRoot{
    void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
}
