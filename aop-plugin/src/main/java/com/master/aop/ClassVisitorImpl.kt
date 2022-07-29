package com.master.aop

import com.master.aop.methodVisitor.MethodVisitorDelegate
import org.gradle.internal.impldep.org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

open class ClassVisitorImpl(classVisitor: ClassVisitor?) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodVisitorDelegate(visitor, access, name, descriptor)
    }
}