package com.master.lib.utils

import android.annotation.SuppressLint
import android.util.Base64
import com.master.lib.ext.toHexString
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 加密工具类
 * @author: MasterChan
 * @date: 2022-6-23 21:05
 */
object EncodeUtils {

    fun base64(data: String, flags: Int = Base64.NO_WRAP): String {
        return Base64.encodeToString(data.toByteArray(), flags)
    }

    fun base64(data: ByteArray, flags: Int = Base64.NO_WRAP): String {
        return Base64.encodeToString(data, flags)
    }

    fun md5(data: String): String {
        return md5(data.toByteArray())
    }

    fun md5(data: ByteArray): String {
        return digestEncryption(data, "MD5").toHexString()
    }

    fun sha1(data: String): String {
        return sha1(data)
    }

    fun sha1(data: ByteArray): String {
        return digestEncryption(data, "SHA-1").toHexString()
    }

    fun sha224(data: ByteArray): String {
        return digestEncryption(data, "SHA-224").toHexString()
    }

    fun sha224(data: String): String {
        return digestEncryption(data.toByteArray(), "SHA-224").toHexString()
    }

    fun sha256(data: ByteArray): String {
        return digestEncryption(data, "SHA-256").toHexString()
    }

    fun sha256(data: String): String {
        return digestEncryption(data.toByteArray(), "SHA-256").toHexString()
    }

    fun sha512(data: ByteArray): String {
        return digestEncryption(data, "SHA-512").toHexString()
    }

    fun sha512(data: String): String {
        return digestEncryption(data.toByteArray(), "SHA-512").toHexString()
    }

    /**
     * 加密
     * @param data 加密数据
     * @param algorithm 加密算法：MD5,SHA-1,SHA-224,SHA-256,SHA-384,SHA-512等
     * @return 16进制字符串
     */
    fun digestEncryption(data: ByteArray, algorithm: String): ByteArray {
        val messageDigest = MessageDigest.getInstance(algorithm)
        messageDigest.update(data)
        return messageDigest.digest()
    }


    /**
     * AES加密
     * @param data ByteArray
     * @param key 密钥，长度为16的倍数
     * @return ByteArray
     */
    fun aes(data: ByteArray, key: ByteArray): ByteArray {
        return cipherEncryption(data, key, "AES", "AES/ECB/NoPadding")
    }

    fun aes(data: String, key: String): ByteArray {
        return aes(data.toByteArray(), key.toByteArray())
    }

    /**
     * des加密
     * @param data ByteArray
     * @param key 密钥，长度固定为8
     * @return ByteArray
     */
    fun des(data: ByteArray, key: ByteArray): ByteArray {
        return cipherEncryption(data, key, "DES", "DES/ECB/NoPadding")
    }

    fun des(data: String, key: String): ByteArray {
        return des(data.toByteArray(), key.toByteArray())
    }

    /**
     * 加密
     * @param data 加密数据
     * @param secretKey 密钥
     * @param algorithm 算法：AES、DES
     * @param transformation String
     * @return ByteArray
     */
    @SuppressLint("GetInstance")
    fun cipherEncryption(
        data: ByteArray,
        secretKey: ByteArray,
        algorithm: String,
        transformation: String
    ): ByteArray {
        try {
            val keySpec = SecretKeySpec(secretKey, algorithm)
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, SecureRandom())
            return cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return byteArrayOf()
    }
}