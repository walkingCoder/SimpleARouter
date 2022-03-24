package com.beijing.arouter_api.exception

import java.lang.RuntimeException

/**
 * Created by jiayk on 2022/3/22
 * Describe:
 */
class NoRouteFoundException (detailMessage: String): RuntimeException(detailMessage){
}