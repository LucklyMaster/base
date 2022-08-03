package com.master.lib.utils

import android.Manifest.permission
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import com.master.lib.ext.application
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface

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

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NET_NONE, NET_UNKNOWN, NET_ETHERNET, NET_WIFI, NET_2G, NET_3G, NET_4G, NET_5G)
    annotation class NetType

    /**
     * 是否连接到可用的网络，如果连接的网络无法访问互联网也会返回false
     * @return Boolean
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val manager = application.getSystemService<ConnectivityManager>() ?: return false
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }

    /**
     * 获取网络连接类型
     * @return [NetType]
     */
    @RequiresPermission(allOf = [permission.ACCESS_NETWORK_STATE, permission.READ_PHONE_STATE])
    fun getConnectType(): Int {
        val manager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cap = manager.getNetworkCapabilities(manager.activeNetwork) ?: return NET_NONE
        return when {
            cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NET_WIFI
            cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NET_ETHERNET
            cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> getMobileType(application)
            else -> NET_NONE
        }
    }

    @RequiresPermission(permission.READ_PHONE_STATE)
    private fun getMobileType(context: Context): Int {
        val manager = context.getSystemService<TelephonyManager>() ?: return NET_UNKNOWN
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

    /**
     * 是否是移动网络连接
     * @return Boolean
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isMobileConnected(): Boolean {
        val manager = application.getSystemService<ConnectivityManager>() ?: return false
        val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    }

    @RequiresPermission(permission.ACCESS_WIFI_STATE)
    fun isWifiEnabled(): Boolean {
        val manager = application.getSystemService<WifiManager>() ?: return false
        return manager.isWifiEnabled
    }

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isWifiConnected(): Boolean {
        val manager = application.getSystemService<ConnectivityManager>() ?: return false
        val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    }

    @RequiresPermission(permission.ACCESS_WIFI_STATE)
    fun getWifiAddress(): String {
        val wifiManager = application.getSystemService<WifiManager>()
        val ipInt = wifiManager?.connectionInfo?.ipAddress ?: return ""
        return (ipInt and 0xFF).toString()
            .plus(".")
            .plus(ipInt shr 8 and 0xFF)
            .plus(".")
            .plus(ipInt shr 16 and 0xFF)
            .plus(".")
            .plus(ipInt shr 24 and 0xFF)
    }

    @RequiresPermission(permission.INTERNET)
    fun getIpAddress(useIpv6: Boolean = false): String {
        val nis = NetworkInterface.getNetworkInterfaces()
        while (nis.hasMoreElements()) {
            val element = nis.nextElement()
            val inetAddresses = element.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val nextElement = inetAddresses.nextElement()
                if (nextElement.isLoopbackAddress) continue
                val hostAddress = nextElement.hostAddress ?: continue
                //ipv6
                if (useIpv6 && nextElement is Inet6Address) {
                    return if (hostAddress.contains("%")) {
                        if (hostAddress.split("%")[1].contains("wlan0")) {
                            hostAddress.substringBefore("%")
                        } else {
                            continue
                        }
                    } else {
                        hostAddress
                    }
                }
                //ipv4
                if (!useIpv6 && nextElement is Inet4Address) {
                    return hostAddress
                }
            }
        }
        return ""
    }
}