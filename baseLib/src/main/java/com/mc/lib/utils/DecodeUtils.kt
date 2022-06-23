package com.mc.lib.utils

import android.annotation.SuppressLint
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 解密工具类
 * @author: MasterChan
 * @date: 2022-06-24 00:15
 */
object DecodeUtils {

    fun base64(data: ByteArray, flags: Int = Base64.NO_WRAP): ByteArray {
        return Base64.decode(data, flags)
    }

    fun base64(data: String, flags: Int = Base64.NO_WRAP): ByteArray {
        return base64(data.toByteArray(), flags)
    }

    fun base64ToString(input: String, flags: Int = Base64.NO_WRAP): String {
        return base64(input, flags).decodeToString()
    }

    /**
     * AES解密
     * @param data ByteArray
     * @param key 密钥，长度为16的倍数
     * @return ByteArray
     */
    fun aes(data: ByteArray, key: ByteArray): ByteArray {
        return cipherDecrypt(data, key, "AES", "AES/ECB/NoPadding")
    }

    fun aes(data: String, key: String): ByteArray {
        return aes(data.toByteArray(), key.toByteArray())
    }

    /**
     * DES解密
     * @param data ByteArray
     * @param key 密钥，长度为8
     * @return ByteArray
     */
    fun des(data: ByteArray, key: ByteArray): ByteArray {
        return cipherDecrypt(data, key, "DES", "DES/ECB/NoPadding")
    }

    fun des(data: String, key: String): ByteArray {
        return des(data.toByteArray(), key.toByteArray())
    }

    /**
     * 解密
     * @param data 解密数据
     * @param key 密钥
     * @param algorithm 算法
     * @param transformation String
     * @return ByteArray
     */
    @SuppressLint("GetInstance")
    fun cipherDecrypt(
        data: ByteArray,
        key: ByteArray,
        algorithm: String,
        transformation: String,
    ): ByteArray {
        try {
            val keySpec = SecretKeySpec(key, algorithm)
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, SecureRandom())
            return cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return byteArrayOf()
    }
}