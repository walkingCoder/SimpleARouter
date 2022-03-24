package com.beijing.arouter_annotaion.annotation


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Route(
    val path: String = "",
    val group: String = "",
    val name: String = "",
    val extras: Int = Int.MIN_VALUE,
    val priority: Int = -1
)