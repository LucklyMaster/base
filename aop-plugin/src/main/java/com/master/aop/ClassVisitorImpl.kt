package com.master.aop

import com.android.build.api.instrumentation.ClassContext
import com.master.aop.methodVisitor.MethodVisitorDelegate
import org.gradle.internal.impldep.org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

open class ClassVisitorImpl(private val classContext: ClassContext, classVisitor: ClassVisitor?) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("方法：${classContext.currentClassData.className}:$name")
        // return super.visitMethod(access, name, descriptor, signature, exceptions)
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodVisitorDelegate(classContext, visitor, access, name, descriptor)
    }
}