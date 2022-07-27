package com.master.lib.permission

import androidx.annotation.RequiresApi
import com.master.lib.utils.AndroidVersion

@RequiresApi(AndroidVersion.ANDROID_10)
open class PermissionImplV29 : PermissionImplV26()