package com.master.aop

import com.android.build.gradle.AppExtension
import com.android.builder.model.AndroidProject
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * @author: MasterChan
 * @date: 2022-08-08 17:14
 */
class JavassistPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.getByType(AndroidProject::class.java).bootClasspath
        println("------------------开始----------------------");
        val android = project.extensions.getByType(AppExtension::class.java)
        val classTransform = JavassistTransform(project)
        android.registerTransform(classTransform)
        println("------------------结束----------------------")
    }
}