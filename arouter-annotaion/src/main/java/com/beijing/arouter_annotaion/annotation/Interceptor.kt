package com.beijing.arouter_annotaion.annotation

/**
 * Created by jiayk on 2022/3/18
 * Describe:
 */
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class Interceptor(
    val name: String,
    val priority: Int
)

