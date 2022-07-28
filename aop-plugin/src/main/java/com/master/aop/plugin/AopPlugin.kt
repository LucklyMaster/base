package com.master.aop.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class AopPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("test AOP plugin")
    }
}