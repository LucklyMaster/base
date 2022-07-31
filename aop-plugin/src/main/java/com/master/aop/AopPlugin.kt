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
        components.onVariants {
            // it.instrumentation.excludes
            // it.getExtension(AopExtension::class.java)
            it.instrumentation.transformClassesWith(
                AsmClassVisitorFactoryImpl::class.java, InstrumentationScope.PROJECT
            ) { params ->
                // params.extension.set(project.extensions.getByType(AopExtension::class.java))
                params.extension.set(it.getExtension(AopExtension::class.java))
            }
            it.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
    }
}