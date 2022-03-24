package com.beijing.arouter_annotaion.enums

/**
 * Created by jiayk on 2022/3/18
 * Describe:
 */
enum class RouteType(val id: Int, val className: String) {

    ACTIVITY(1, "android.app.activity"),
    FRAGMENT(2, "android.app.fragment"),
    UNKNOWN(-1, "unknown route type");

    fun parse(className: String): RouteType? {
        for (value in values()) {
            if (value.className == className) {
                return value
            }
        }
        return UNKNOWN
    }
}