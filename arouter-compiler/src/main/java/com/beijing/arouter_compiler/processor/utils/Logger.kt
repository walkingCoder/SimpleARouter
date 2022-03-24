package com.beijing.arouter_compiler.processor.utils

import java.lang.StringBuilder
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Created by jiayk on 2022/3/18
 * Describe:
 */
class Logger(var messager: Messager) {

    fun info(info:CharSequence){
        if (info.isNotEmpty()){
            messager.printMessage(Diagnostic.Kind.NOTE, Consts.PROJECT + info)
        }
    }
    fun error(error: CharSequence) {
        if (error.isNotEmpty()) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error + "]"
            )
        }
    }

    fun error(error: Throwable?) {
        if (null != error) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                Consts.PREFIX_OF_LOGGER + "An exception is encountered, [${error.message}]" + "\n" + formatStackTrace(error.stackTrace)
            )
        }
    }

    fun warning(warning: CharSequence) {
        if (warning.isNotEmpty()) {
            messager.printMessage(Diagnostic.Kind.WARNING, Consts.PREFIX_OF_LOGGER + warning)
        }
    }

    private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String{
       val sb =  StringBuilder()
        for (element in stackTrace){
            sb.append("   at").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }

}