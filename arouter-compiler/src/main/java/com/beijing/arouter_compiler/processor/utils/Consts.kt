package com.beijing.arouter_compiler.processor.utils

/**
 * Created by jiayk on 2022/3/18
 * Describe:
 */
object Consts {

    // Generate
    const val SEPARATOR = "$$"
    const val PROJECT = "ARouter"
    const val TAG = PROJECT + "::"
    const val METHOD_LOAD_INTO = "loadInto"
    const val NAME_OF_ROOT = PROJECT + SEPARATOR + "Root"
    const val NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR
    const val PACKAGE_OF_GENERATE_FILE = "com.beijing.android.arouter.routes"

    // Java type
    private const val LANG = "java.lang"
    const val BYTE = "$LANG.Byte"
    const val SHORT = "$LANG.Short"
    const val INTEGER = "$LANG.Integer"
    const val LONG = "$LANG.Long"
    const val FLOAT = "$LANG.Float"
    const val DOUBEL = "$LANG.Double"
    const val BOOLEAN = "$LANG.Boolean"
    const val CHAR = "$LANG.Character"
    const val STRING = "$LANG.String"
    const val SERIALIZABLE = "java.io.Serializable"


    // Custom interface
    const val FACADE_PACKAGE ="com.beijing.arouter_api.facade"
    const val TEMPLATE_PACKAGE = ".template"
    const val MODEL_PACKAGE = ".model"
    const val IROUTE_ROOT = "$FACADE_PACKAGE$TEMPLATE_PACKAGE.IRouteRoot"
    const val IROUTE_GROUP = "$FACADE_PACKAGE$TEMPLATE_PACKAGE.IRouteGroup"

    // System interface
    const val ACTIVITY = "android.app.Activity"
    const val FRAGMENT = "android.app.Fragment"
    const val PARCELABLE = "android.os.Parcelable"

    // Log
    val PREFIX_OF_LOGGER: String = Consts.PROJECT + "::Compiler "
    const val NO_MODULE_NAME_TIPS = "These no module name, at 'build.gradle', like :\n" +
            "android {\n" +
            "    defaultConfig {\n" +
            "        ...\n" +
            "        javaCompileOptions {\n" +
            "            annotationProcessorOptions {\n" +
            "                arguments = [AROUTER_MODULE_NAME: project.getName()]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n"

    // Options of processor
    const val KEY_MODULE_NAME = "AROUTER_MODULE_NAME"
    const val KEY_GENERATE_DOC_NAME = "AROUTER_GENERATE_DOC"

    const val VALUE_ENABLE = "enable"

    // Annotation type
    const val ANNOTATION_TYPE_INTECEPTOR: String =
        FACADE_PACKAGE + ".annotation.Interceptor"
    const val ANNOTATION_TYPE_ROUTE: String =
        FACADE_PACKAGE + ".annotation.Route"
    const val ANNOTATION_TYPE_AUTOWIRED: String =
        FACADE_PACKAGE + ".annotation.Autowired"
}