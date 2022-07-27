package com.masterchan.mybase.aspect

import android.app.Activity
import com.master.lib.ext.println
import com.master.lib.permission.MPermissions
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
class PermissionAspect {

    @Pointcut(
        "execution(@com.masterchan.mybase.aspect.PermissionNeed * *(..)) && @annotation(requestPermission)"
    )
    fun requestPermissions(requestPermission: PermissionNeed) {
    }

    @Around("requestPermissions(requestPermission)")
    fun aroundRequest(joinPoint: ProceedingJoinPoint, requestPermission: PermissionNeed) {
        val permissions = requestPermission.value.toList()
        val any = joinPoint.getThis()
        if (any is Activity) {
            MPermissions.with(any).permissions(permissions).request {
                it.println()
                if (it.allGranted) {
                    joinPoint.proceed()
                }
            }
        } else {

        }
    }

}