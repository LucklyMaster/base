package com.master.aop

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnPermissionDenied(val permissions: Array<String>)