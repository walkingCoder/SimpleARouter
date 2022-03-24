package com.beijing.arouter_compiler.processor

import com.beijing.arouter_annotaion.annotation.AutoWired
import com.beijing.arouter_annotaion.annotation.Route
import com.beijing.arouter_annotaion.enums.RouteType
import com.beijing.arouter_annotaion.module.RouteMeta
import com.beijing.arouter_compiler.processor.utils.Consts
import com.beijing.arouter_compiler.processor.utils.Consts.IROUTE_ROOT
import com.beijing.arouter_compiler.processor.utils.Logger
import com.beijing.arouter_compiler.processor.utils.TypeUtils
import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.MapUtils
import org.apache.commons.lang3.StringUtils
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
@SupportedAnnotationTypes(Consts.ANNOTATION_TYPE_ROUTE, Consts.ANNOTATION_TYPE_AUTOWIRED)
class RouteProcessor : AbstractProcessor() {
    lateinit var mFiler: Filer
    lateinit var logger: Logger
    lateinit var types: Types
    lateinit var typeUtils: TypeUtils
    lateinit var elementUtils: Elements
    var moduleName : String = ""
    var generateDoc:Boolean = false

    var groupMap = mutableMapOf<String, MutableSet<RouteMeta>>()
    var rootMap = mutableMapOf<String, String>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer   // 返回用于创建新的源，类或辅助文件的文件管理器。
        logger = Logger(processingEnv.messager)    //返回用于报告错误，警告和其他通知的消息。
        elementUtils = processingEnv.elementUtils   // 返回一些用于操作元素的实用方法的实现
        types = processingEnv.typeUtils         // 返回一些用于对类型进行操作的实用程序方法的实现。
        typeUtils = TypeUtils(types, elementUtils)

        logger.info("jyk ---=============-- init success")

        var options = processingEnv.options
        if (MapUtils.isNotEmpty(options)){
            moduleName = options[Consts.KEY_MODULE_NAME].toString()
            generateDoc = Consts.VALUE_ENABLE == options[Consts.KEY_GENERATE_DOC_NAME]
        }
        logger.info("jyk ---=============-- init 1111111111")
        if (StringUtils.isNotEmpty(moduleName)){
            moduleName = moduleName.replace("[^0-9a-zA-Z_]+".toRegex(), "")
            logger.info("The user has configuration the module name, it was [$moduleName]")
        }else {
            logger.error(Consts.NO_MODULE_NAME_TIPS)
            throw RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.")
        }
        logger.info("jyk ---=============-- init 222222222")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            Route::class.java.canonicalName
        )
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return HashSet<String>().also {
            it.add(Consts.KEY_MODULE_NAME)
            it.add(Consts.KEY_GENERATE_DOC_NAME)
        }
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        logger.info("jyk ---==========23123213===-- process")
        if (CollectionUtils.isNotEmpty(annotations)) {
            val routeElements = roundEnv.getElementsAnnotatedWith(Route::class.java)
            try {
                parseRoutes(routeElements)
            } catch (e: Exception) {

            }
            return true
        }
        return false
    }

    private fun parseRoutes(routeElements: Set<Element>) {
        logger.info("jyk ---==========23123213===-- parseRoutes")
        if (CollectionUtils.isNotEmpty(routeElements)) {
            logger.info("jyk ---==========1111111111=${routeElements.size}==-- parseRoutes")
            rootMap.clear()

            val type_Activity = elementUtils.getTypeElement(Consts.ACTIVITY).asType()
            val type_Fragment = elementUtils.getTypeElement(Consts.FRAGMENT).asType()
            val type_IRouteGroup = elementUtils.getTypeElement(Consts.IROUTE_GROUP)

            val routeMateCn = ClassName.get(RouteMeta::class.java)
            val routeTypeCn = ClassName.get(RouteType::class.java)
            logger.info("jyk ---==========routeMate00000000===-- ")
            // Build input type
            // Map<String, Class<? extends IRouteGroup>>
            val inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(Map::class.java),
                ClassName.get(String::class.java),
                ParameterizedTypeName.get(
                    ClassName.get(Class::class.java),
                    WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                )
            )
            logger.info("jyk ---==========routeMate0111111111===-- ")
            // Map<String, RouteMate>
            val inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(Map::class.java),
                ClassName.get(String::class.java),
                ClassName.get(RouteMeta::class.java)
            )
            // build input param name
            val rootParameterSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build()
            val groupParameterSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build()
            logger.info("jyk ---==========routeMate0122222222==-- ")
            // build method: "loadInto"
            var loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rootParameterSpec)

            logger.info("jyk ---==========routeMate===-- routeElements")
            //  Follow a sequence, find out metas of group first, generate java file, then statistics them as root.
            for (element in routeElements) {
                val typeMirror = element.asType()
                val route = element.getAnnotation(Route::class.java)
                var routeMate: RouteMeta

                // Activity or Fragment
                if (types.isSubtype(typeMirror, type_Activity) || types.isSubtype(
                        typeMirror,
                        type_Fragment
                    )
                ) {
                    logger.info("jyk ---==========routeMate===-- type_Activity")
                    // Get all fields annotation by @Autowired
                    var paramsType = mutableMapOf<String, Int>()
                    var injectConfig = mutableMapOf<String, AutoWired>()
                    injectParamCollector(element, paramsType, injectConfig)

                    if (types.isSubtype(typeMirror, type_Activity)) {
                        logger.info("jyk ---==========routeMate===-- ACTIVITY")
                        routeMate = RouteMeta(route, element, RouteType.ACTIVITY, paramsType)
                    } else {
                        routeMate = RouteMeta(route, element, RouteType.FRAGMENT, paramsType)
                    }

                    routeMate.injectConfig = injectConfig
                } else {
                    throw RuntimeException("The @Route is marked on unsupported class, look at $typeMirror")
                }

                categories(routeMate)
            }

            for ((groupName, value) in groupMap) {
                val loadInfoMethodOfGroupBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groupParameterSpec)

                for (routeMate in value) {
                    val className = ClassName.get(routeMate.rawType as TypeElement)

                    // make map body for paramsType
                    var mapBodyBuilder = StringBuilder()
                    val paramsType = routeMate.paramsType
//                    val injectConfigs = routeMate.injectConfig

                    if (MapUtils.isNotEmpty(paramsType)) {
                        paramsType!!.forEach { (t, u) ->
                            mapBodyBuilder.append("put(\"")
                                .append(t).append("\",")
                                .append(u).append(");")
                        }
                    }

                    val mapBody = mapBodyBuilder.toString()
                    loadInfoMethodOfGroupBuilder.addStatement(
                        "atlas.put(\$S,\$T.build(\$T." + routeMate.type + ", \$T.class, \$S, \$S, " + (if (StringUtils.isEmpty(
                                mapBody
                            )
                        ) null else "new java.util.HashMap<String, Integer>(){{$mapBodyBuilder}}") + ", " + routeMate.priority + ", " + routeMate.extra + "))",
                        routeMate.path,
                        routeMateCn,
                        routeTypeCn,
                        className,
                        routeMate.path!!.lowercase(),
                        routeMate.group!!.lowercase()
                    )
                }

                // Generate groups
                val groupFileName = Consts.NAME_OF_GROUP + groupName
                JavaFile.builder(
                    Consts.PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupFileName)
                        .addSuperinterface(ClassName.get(type_IRouteGroup))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(loadInfoMethodOfGroupBuilder.build())
                        .build()
                ).build().writeTo(mFiler)
                logger.info("jyk ---==========1111111111===-- write group")
                rootMap.put(groupName, groupFileName)
            }

            if (MapUtils.isNotEmpty(rootMap)) {
                //Generate root meta by group name, it must be generated before root, then I can find out the class of group.
                rootMap.forEach { (t, u) ->
                    loadIntoMethodOfRootBuilder.addStatement(
                        "routes.put(\$S,\$T.class)",
                        t,
                        ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, u)
                    )
                }
            }

            // Write root mate into disk
            val rootFileName = Consts.NAME_OF_ROOT + Consts.SEPARATOR + moduleName
            JavaFile.builder(
                Consts.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootFileName)
                    .addSuperinterface(ClassName.get(elementUtils.getTypeElement(IROUTE_ROOT)))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadIntoMethodOfRootBuilder.build())
                    .build()
            ).build().writeTo(mFiler)
            logger.info("jyk ---==========1111111111===-- write root")
        }
    }

    private fun categories(routeMate: RouteMeta) {
        if (routeVerify(routeMate)) {
            var routeMates = groupMap[routeMate.group]
            if (CollectionUtils.isEmpty(routeMates)) {
                val routeMetaSet = mutableSetOf<RouteMeta>()
                routeMetaSet.add(routeMate)
                routeMate.group?.let { groupMap.put(it, routeMetaSet) }
            } else {
                routeMates!!.add(routeMate)
            }
        }
    }

    fun routeVerify(mate: RouteMeta): Boolean {
        val path = mate.path
        if (StringUtils.isEmpty(path) || !path!!.startsWith("/")) {
            return false
        }
        if (StringUtils.isEmpty(mate.group)) {
            val defaultGroup = path.substring(1, path.indexOf("/", 1))
            if (StringUtils.isEmpty(defaultGroup)) {
                return false
            }
            mate.group = defaultGroup
            return true
        }
        return false
    }

    private fun injectParamCollector(
        element: Element,
        paramsType: MutableMap<String, Int>,
        injectConfig: MutableMap<String, AutoWired>
    ) {
        //element.enclosedElements  此元素包含的所有直接元素
        for (filed in element.enclosedElements) {
            if (filed.kind.isField && filed.getAnnotation(AutoWired::class.java) != null) {
                val paramConfig = filed.getAnnotation(AutoWired::class.java)
                val injectName =
                    if (StringUtils.isEmpty(paramConfig.name)) filed.simpleName.toString() else paramConfig.name
                paramsType[injectName] = typeUtils.typeChange(element)
                injectConfig[injectName] = paramConfig
            }
        }

        // if has parent?
        val parent = (element as TypeElement).superclass
        if (parent is DeclaredType) {
            val parentElement = parent.asElement()
            if (parentElement is TypeElement && !parentElement.qualifiedName.startsWith("android"))
                injectParamCollector(parentElement, paramsType, injectConfig)
        }
    }
}