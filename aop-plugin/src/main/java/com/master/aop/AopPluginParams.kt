package com.master.aop

import com.android.build.api.instrumentation.InstrumentationParameters

open class AopPluginParams : InstrumentationParameters {
    var extension: AopExtension? = null
}