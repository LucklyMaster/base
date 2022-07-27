package com.master.aop.aspect

import android.app.Activity
import com.master.aop.RequestPermission
import com.master.lib.ext.println
import com.master.lib.permission.MPermissions
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 * 权限申请
 * @author: MasterChan
 * @date: 2022-07-27 21:29
 */
@Aspect
class AspectPermission {
    @Pointcut(
        "execution(@com.master.aop.RequestPermission * *(..)) && @annotation(permissionRequest)"
    )
    fun requestPermissions(permissionRequest: RequestPermission) {
    }

    @Around("requestPermissions(permissionRequest)")
    fun aroundRequest(joinPoint: ProceedingJoinPoint, permissionRequest: RequestPermission) {
        val permissions = permissionRequest.value
        val any = joinPoint.getThis()
        if (any is Activity) {
            MPermissions.with(any).permissions(permissions).request {
                if (it.allGranted) {
                    joinPoint.proceed()
                }
            }
        } else {

        }
    }
}