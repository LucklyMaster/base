package com.master.aop.methodVisitor

import com.android.build.api.instrumentation.ClassContext
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class MethodVisitorDelegate(
    private val classContext: ClassContext,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
) : AdviceAdapter(Opcodes.ASM9, methodVisitor, access, name, descriptor) {

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("方法注解：${classContext.currentClassData.className}:$descriptor")
        return super.visitAnnotation(descriptor, visible)
    }
}