package com.master.aop

/**
 * 具体支持的注解
 * @author: MasterChan
 * @date: 2022-08-01 18:13
 */
object Annotation {
    const val Aspect = "com.master.aop.Aspect"
    const val PrintCost = "Lcom/master/aop/PrintCost;"
    const val RequestPermission = "Lcom/master/aop/RequestPermission;"
    val values = arrayOf(PrintCost, RequestPermission)
}