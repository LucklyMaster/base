package com.master.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequestPermission(val permissions: Array<String>, val isAllGranted: Boolean = true)