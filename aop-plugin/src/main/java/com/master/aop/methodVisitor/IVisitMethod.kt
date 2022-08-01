package com.master.aop.methodVisitor

import com.master.aop.Annotation

/**
 * IVisitMethod
 * @author: MasterChan
 * @date: 2022-08-01 21:44
 */
interface IVisitMethod {

    // fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor

    fun isNeedVisitMethod(annotation: String): Boolean {
        return Annotation.values.contains(annotation)
    }

    fun onMethodEnter()

    fun onMethodExit(opcode: Int)
}