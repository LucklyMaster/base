package com.master.aop

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

abstract class AsmClassVisitorFactoryImpl : AsmClassVisitorFactory<AopPluginParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ClassVisitorImpl(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return false
    }
}