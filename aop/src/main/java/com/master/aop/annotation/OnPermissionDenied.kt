package com.master.aop.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnPermissionDenied(val permissions: Array<String>)