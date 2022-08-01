package com.master.aop

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AopPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("-------------------------------------")
        println("|           AOP插件开始运行          |")
        println("-------------------------------------")
        project.extensions.create("aopWeave", AopExtension::class.java)
        val components = project.extensions.getByType(AndroidComponentsExtension::class.java)
        components.onVariants { variant ->
            val extension = project.extensions.getByType(AopExtension::class.java)
            println("------------AOP插件${if (extension.enable) "已启用" else "未启用"}------------")

            if (!extension.enable) {
                return@onVariants
            }

            //过滤不需要扫描的类
            extension.exclude.forEach {
                variant.instrumentation.excludes.add(it)
            }

            variant.instrumentation.transformClassesWith(
                AsmClassVisitorFactoryImpl::class.java, InstrumentationScope.PROJECT
            ) {}
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
    }
}