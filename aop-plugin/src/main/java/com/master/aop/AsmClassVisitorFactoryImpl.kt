package com.master.aop

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class AsmClassVisitorFactoryImpl : AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ClassVisitorImpl(classContext, nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.classAnnotations.contains(Annotation.Aspect)
    }
}