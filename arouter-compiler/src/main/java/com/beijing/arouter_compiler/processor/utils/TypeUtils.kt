package com.beijing.arouter_compiler.processor.utils

import com.beijing.arouter_annotaion.enums.TypeKind
import com.beijing.arouter_compiler.processor.utils.Consts.BOOLEAN
import com.beijing.arouter_compiler.processor.utils.Consts.BYTE
import com.beijing.arouter_compiler.processor.utils.Consts.CHAR
import com.beijing.arouter_compiler.processor.utils.Consts.DOUBEL
import com.beijing.arouter_compiler.processor.utils.Consts.FLOAT
import com.beijing.arouter_compiler.processor.utils.Consts.INTEGER
import com.beijing.arouter_compiler.processor.utils.Consts.LONG
import com.beijing.arouter_compiler.processor.utils.Consts.SHORT
import com.beijing.arouter_compiler.processor.utils.Consts.STRING
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by jiayk on 2022/3/18
 * Describe:
 */
class TypeUtils constructor(var types: Types, elements: Elements) {

    // getTypeElement()返回已给出其规范名称的类型元素
    // asType()  返回一个TypeMirror是元素的类型信息，包括包名，类(或方法，或参数)名/类型，在生成动态代码的时候，我们往往需要知道变量/方法参数的类型，以便写入正确的类型声明
    var parcelableType: TypeMirror = elements.getTypeElement(Consts.PARCELABLE).asType()
    var serializableType: TypeMirror = elements.getTypeElement(Consts.SERIALIZABLE).asType()

    fun typeChange(element: Element): Int {
        var typeMirror = element.asType()

        if (typeMirror.kind.isPrimitive) {
            return element.asType().kind.ordinal
        }
        when (typeMirror.toString()) {
            BYTE -> return TypeKind.BYTE.ordinal
            SHORT -> return TypeKind.SHORT.ordinal
            INTEGER -> return TypeKind.INT.ordinal
            LONG -> return TypeKind.LONG.ordinal
            FLOAT -> return TypeKind.FLOAT.ordinal
            DOUBEL -> return TypeKind.DOUBLE.ordinal
            BOOLEAN -> return TypeKind.BOOLEAN.ordinal
            CHAR -> return TypeKind.CHAR.ordinal
            STRING -> return TypeKind.STRING.ordinal
            else -> return when {
                types.isSubtype(typeMirror, parcelableType) -> {
                    TypeKind.PARCELABLE.ordinal
                }
                types.isSubtype(typeMirror, serializableType) -> {
                    TypeKind.SERIALIZABLE.ordinal
                }
                else -> {
                    TypeKind.OBJECT.ordinal
                }
            }
        }
    }
}