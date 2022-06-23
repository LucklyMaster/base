package com.mc.lib.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager


/**
 * 网络相关工具类
 * @author MasterChan
 * @date 2021-12-21 14:46
 */
object NetUtils {

    const val NET_NONE = -2
    const val NET_UNKNOWN = -1
    const val NET_ETHERNET = 0
    const val NET_WIFI = 1
    const val NET_2G = 2
    const val NET_3G = 3
    const val NET_4G = 4
    const val NET_5G = 5

    /**
     * 是否连接到可用的网络，如果连接的网络无法访问互联网也会返回false
     * @param context Context
     * @return Boolean
     */
    @JvmStatic
    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }

    fun getConnectState(context: Context): Int {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cap = manager.getNetworkCapabilities(manager.activeNetwork) ?: return NET_NONE
        return when {
            cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NET_WIFI
            cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NET_ETHERNET
            cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> getMobileType(context)
            else -> NET_NONE
        }
    }

    private fun getMobileType(context: Context): Int {
        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val netWorkType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.dataNetworkType
        } else {
            manager.networkType
        }
        return when (netWorkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> NET_2G
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_EHRPD -> NET_3G
            TelephonyManager.NETWORK_TYPE_LTE, 19 -> NET_4G
            TelephonyManager.NETWORK_TYPE_NR -> NET_5G
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> NET_UNKNOWN
            else -> NET_UNKNOWN
        }
    }
}