package com.master.aop.methodVisitor

import com.master.aop.Annotation
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class MethodVisitorDelegate(
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
) : AdviceAdapter(Opcodes.ASM9, methodVisitor, access, name, descriptor) {

    private var delegate: IVisitMethod? = null
    private var isNeedVisitMethod = false
    private val annotationList by lazy { mutableListOf<String>() }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (!isNeedVisitMethod(descriptor)) {
            isNeedVisitMethod = true
            annotationList.add(descriptor!!)
            return super.visitAnnotation(descriptor, visible)
        }
        println("注解：$descriptor")
        return object : AnnotationVisitor(
            Opcodes.ASM9, super.visitAnnotation(descriptor, visible)
        ) {
            override fun visit(name: String?, value: Any?) {
                println(
                    "注解 $name 的值为：${if (value is Array<*>) value.contentToString() else value.toString()}"
                )
                super.visit(name, value)
            }

            override fun visitArray(name: String?): AnnotationVisitor {
                println("注解2 $name")
                return object : AnnotationVisitor(Opcodes.ASM9, super.visitArray(name)) {
                    override fun visit(name: String?, value: Any?) {
                        println(
                            "注解4 $name 的值为：${if (value is Array<*>) value.contentToString() else value.toString()}"
                        )
                    }
                }
            }

            override fun visitEnum(name: String?, descriptor: String?, value: String?) {
                println("注解3 $name")
                super.visitEnum(name, descriptor, value)
            }

            override fun visitEnd() {
                super.visitEnd()
                println("访问注解结束")
            }
        }
    }

    override fun visitAnnotationDefault(): AnnotationVisitor {
        return super.visitAnnotationDefault()
    }

    override fun onMethodEnter() {
        if (!isNeedVisitMethod) {
            return
        }
        println("进入方法")
        annotationList.forEach {

        }
    }

    override fun onMethodExit(opcode: Int) {
        if (!isNeedVisitMethod) {
            return
        }
        println("退出方法")
    }

    private fun isNeedVisitMethod(annotation: String?): Boolean {
        return Annotation.values.contains(annotation)
    }
}