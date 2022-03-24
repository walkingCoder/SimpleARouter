package com.beijing.arouter_annotaion.annotation



/**
 * Created by jiayk on 2022/3/18
 * Describe:
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class AutoWired(
    val name: String,
    val required: Boolean,
    val desc: String
)
