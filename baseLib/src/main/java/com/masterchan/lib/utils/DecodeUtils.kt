package com.masterchan.lib.utils

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author MasterChan
 * @date 2021-12-21 10:44
 * @describe 解密相关的工具类
 */
object DecodeUtils {

    /**
     * Base64解码
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    @JvmStatic
    fun base64(input: ByteArray, flags: Int = Base64.NO_WRAP): ByteArray {
        return Base64.decode(input, flags)
    }

    @JvmStatic
    fun base64(input: String, flags: Int = Base64.NO_WRAP): ByteArray {
        return base64(input.toByteArray(), flags)
    }

    @JvmStatic
    fun base64ToString(input: String, flags: Int = Base64.NO_WRAP): String {
        return base64(input, flags).decodeToString()
    }

    /**
     * URL解码 若系统不支持指定的解码字符集,则直接将input原样返回
     * @param input String
     * @param charset String?
     * @return String
     */
    @JvmStatic
    fun url(input: String, charset: String? = "UTF-8"): String {
        return try {
            URLDecoder.decode(input, charset)
        } catch (e: UnsupportedEncodingException) {
            input
        }
    }

    /**
     * AES解密
     *
     * @param data 密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun aes(data: ByteArray, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, "AES", "AES/ECB/NoPadding")
    }

    /**
     * DES解密
     *
     * @param data 密文
     * @param key  8字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun des(data: ByteArray, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, "DES", "DES/ECB/NoPadding")
    }

    @JvmStatic
    fun desTemplate(
        data: ByteArray,
        key: ByteArray?,
        algorithm: String?,
        transformation: String?,
    ): ByteArray? {
        try {
            val keySpec = SecretKeySpec(key, algorithm)
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, SecureRandom())
            return cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }
}