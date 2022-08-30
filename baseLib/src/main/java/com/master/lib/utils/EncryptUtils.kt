package com.master.lib.utils

import android.annotation.SuppressLint
import android.util.Base64
import com.master.lib.ext.toHexString
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 加密工具类
 * @author: MasterChan
 * @date: 2022-6-23 21:05
 */
object EncryptUtils {
    @JvmStatic
    fun base64(data: String, flags: Int = Base64.NO_WRAP): String {
        return Base64.encodeToString(data.toByteArray(), flags)
    }

    @JvmStatic
    fun base64(data: ByteArray, flags: Int = Base64.NO_WRAP): ByteArray {
        return Base64.encode(data, flags)
    }

    @JvmStatic
    fun md5(data: String): String {
        return md5(data.toByteArray()).toHexString()
    }

    @JvmStatic
    fun md5(data: ByteArray): ByteArray {
        return digestEncryption(data, "MD5")
    }

    @JvmStatic
    fun sha1(data: String): String {
        return sha1(data.toByteArray()).toHexString()
    }

    @JvmStatic
    fun sha1(data: ByteArray): ByteArray {
        return digestEncryption(data, "SHA-1")
    }

    @JvmStatic
    fun sha224(data: String): String {
        return sha224(data.toByteArray()).toHexString()
    }

    @JvmStatic
    fun sha224(data: ByteArray): ByteArray {
        return digestEncryption(data, "SHA-224")
    }

    @JvmStatic
    fun sha256(data: String): String {
        return sha256(data.toByteArray()).toHexString()
    }

    @JvmStatic
    fun sha256(data: ByteArray): ByteArray {
        return digestEncryption(data, "SHA-256")
    }

    @JvmStatic
    fun sha512(data: String): String {
        return sha512(data.toByteArray()).toHexString()
    }

    @JvmStatic
    fun sha512(data: ByteArray): ByteArray {
        return digestEncryption(data, "SHA-512")
    }

    /**
     * 加密
     * @param data 加密数据
     * @param algorithm 加密算法：MD5,SHA-1,SHA-224,SHA-256,SHA-384,SHA-512等
     * @return 16进制字符串
     */
    @JvmStatic
    fun digestEncryption(data: ByteArray, algorithm: String): ByteArray {
        val messageDigest = MessageDigest.getInstance(algorithm)
        messageDigest.update(data)
        return messageDigest.digest()
    }

    @JvmStatic
    fun aes(data: String, key: String, transformation: String = "AES/ECB/NoPadding"): ByteArray {
        return aes(data.toByteArray(), key.toByteArray(), transformation)
    }

    /**
     * AES加密
     * @param data ByteArray
     * @param key 密钥，长度为16的倍数
     * @param transformation: String
     * @return ByteArray
     */
    @JvmStatic
    fun aes(
        data: ByteArray,
        key: ByteArray,
        transformation: String = "AES/ECB/NoPadding"
    ): ByteArray {
        return cipherEncryption(data, key, "AES", transformation)
    }

    @JvmStatic
    fun des(data: String, key: String, transformation: String = "DES/ECB/NoPadding"): ByteArray {
        return des(data.toByteArray(), key.toByteArray(), transformation)
    }

    /**
     * des加密
     * @param data ByteArray
     * @param key 密钥，长度固定为8
     * @param transformation: String
     * @return ByteArray
     */
    @JvmStatic
    fun des(
        data: ByteArray,
        key: ByteArray,
        transformation: String = "DES/ECB/NoPadding"
    ): ByteArray {
        return cipherEncryption(data, key, "DES", transformation)
    }

    /**
     * 加密
     * @param data 加密数据
     * @param secretKey 密钥
     * @param algorithm 算法：AES、DES
     * @param transformation String
     * @return ByteArray
     */
    @JvmStatic
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

    /**
     * 获取RSA密钥对
     * @param keySize Int
     * @return KeyPair
     * @throws InvalidParameterException
     */
    @JvmStatic
    @Throws(InvalidParameterException::class)
    fun getRsaKeyPair(keySize: Int = 1024): KeyPair {
        return KeyPairGenerator.getInstance("RSA").apply { initialize(keySize) }.generateKeyPair()
    }

    /**
     * 获取RSA秘钥
     * @param key ByteArray
     * @return PrivateKey
     * @throws InvalidKeySpecException
     */
    @JvmStatic
    @Throws(InvalidKeySpecException::class)
    fun getRsaPrivateKey(key: ByteArray): PrivateKey {
        return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(key))
    }

    /**
     * 获取RSA公钥
     * @param key ByteArray
     * @return PublicKey
     * @throws InvalidKeySpecException
     */
    @JvmStatic
    @Throws(InvalidKeySpecException::class)
    fun getRsaPublicKey(key: ByteArray): PublicKey {
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(key))
    }

    /**
     * RSA加密
     * @param data ByteArray
     * @param publicKey PublicKey
     * @return ByteArray
     */
    @JvmStatic
    fun rsa(data: ByteArray, publicKey: PublicKey): ByteArray {
        return try {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val blockSize = cipher.blockSize
            val outputSize = cipher.getOutputSize(data.size)
            val blocksSize = if (data.size % blockSize == 0) {
                data.size / blockSize
            } else {
                data.size / blockSize + 1
            }
            val output = ByteArray(outputSize * blocksSize)
            var i = 0
            while (data.size - i * blockSize > 0) {
                if (data.size - i * blockSize > blockSize) {
                    cipher.doFinal(data, i * blockSize, blockSize, output, i * outputSize)
                } else {
                    cipher.doFinal(
                        data, i * blockSize, data.size - i * blockSize, output, i * outputSize
                    )
                }
                i++
            }
            output
        } catch (e: Exception) {
            e.printStackTrace()
            byteArrayOf()
        }
    }
}