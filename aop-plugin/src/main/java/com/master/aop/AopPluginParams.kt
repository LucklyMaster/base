package com.master.aop

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface AopPluginParams : InstrumentationParameters {
    @get:Input
    val extension: Property<AopExtension>
}