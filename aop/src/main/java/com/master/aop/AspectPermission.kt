package com.master.aop

import android.content.Context
import com.master.aop.annotation.RequestPermission
import com.master.lib.ext.toast
import com.master.lib.permission.MPermissions
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 *
 * @author: MasterChan
 * @date: 2022-08-08 9:13
 */
@Aspect
class AspectPermission {

    @Pointcut(
        "execution(@com.master.aop.annotation.RequestPermission * *(..)) && @annotation(requestPermission)"
    )
    fun request(requestPermission: RequestPermission) {
    }

    @Around("request(requestPermission)")
    fun requestPoint(point: ProceedingJoinPoint, requestPermission: RequestPermission) {
        val context = point.getThis() as Context
        MPermissions.with(context).permissions(*requestPermission.permissions).request {
            if (it.allGranted) {
                toast("success")
            } else {
                toast("failed")
            }
        }
    }

}