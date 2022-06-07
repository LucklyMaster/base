package com.masterchan.lib.utils

import android.util.Base64
import com.masterchan.lib.ext.toHexString
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 加密相关工具类
 * @author MasterChan
 * @date 2021-12-21 13:54
 */
object EncodeUtils {

    /**
     * URL编码 若系统不支持指定的编码字符集,则直接将input原样返回
     * @param input String
     * @param charset String?
     * @return String
     */
    @JvmOverloads
    @JvmStatic
    fun url(input: String, charset: String? = "UTF-8"): String {
        return try {
            URLEncoder.encode(input, charset)
        } catch (e: UnsupportedEncodingException) {
            input
        }
    }

    //    <editor-fold desc="Base64加密">
    /**
     * Base64编码
     *
     * @param input 要编码的字符串
     * @return Base64编码后的字符串
     */
    @JvmStatic
    fun base64(input: String, flags: Int = Base64.NO_WRAP): ByteArray {
        return base64(input.toByteArray(), flags)
    }

    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    @JvmStatic
    fun base64(input: ByteArray?, flags: Int = Base64.NO_WRAP): ByteArray {
        return Base64.encode(input, flags)
    }

    /**
     * Base64编码
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    @JvmStatic
    fun base64String(input: ByteArray?, flags: Int = Base64.NO_WRAP): String {
        return Base64.encodeToString(input, flags)
    }
    //    </editor-fold>

    //    <editor-fold desc="MD2加密">
    /**
     * MD2加密
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun md2(data: ByteArray): ByteArray {
        return encryptAlgorithm(data, "MD2")
    }

    /**
     * MD2加密
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun md2String(data: String): String {
        return md2String(data.toByteArray())
    }

    /**
     * MD2加密
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun md2String(data: ByteArray): String {
        return md2(data).toHexString()
    }
    //    </editor-fold>

    //    <editor-fold desc="MD5加密">
    /**
     * MD5加密
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun md5(data: ByteArray): ByteArray {
        return encryptAlgorithm(data, "MD5")
    }

    @JvmStatic
    fun md5(data: String): ByteArray {
        return md5(data.toByteArray())
    }

    /**
     * MD5加密
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun md5String(data: String): String {
        return md5String(data.toByteArray())
    }

    /**
     * MD5加密
     * @param data 明文字符串
     * @param salt 盐
     * @return 16进制加盐密文
     */
    @JvmStatic
    fun md5String(data: String, salt: String): String {
        return md5((data + salt).toByteArray()).toHexString()
    }

    /**
     * MD5加密
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun md5String(data: ByteArray): String {
        return md5(data).toHexString()
    }

    /**
     * MD5加密
     * @param data 明文字节数组
     * @param salt 盐字节数组
     * @return 16进制加盐密文
     */
    @JvmStatic
    fun md5String(data: ByteArray, salt: ByteArray): String {
        val dataSalt = ByteArray(data.size + salt.size)
        System.arraycopy(data, 0, dataSalt, 0, data.size)
        System.arraycopy(salt, 0, dataSalt, data.size, salt.size)
        return md5(dataSalt).toHexString()
    }

    /**
     * MD5加密文件
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun fileToMd5(filePath: String): ByteArray? {
        return fileToMd5(File(filePath))
    }

    /**
     * MD5加密文件
     * @param filePath 文件路径
     * @return 文件的16进制密文
     */
    @JvmStatic
    fun fileToMd5String(filePath: String): String {
        return fileToMd5String(File(filePath))
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的16进制密文
     */
    @JvmStatic
    fun fileToMd5String(file: File): String {
        return if (fileToMd5(file) != null) fileToMd5(file)!!.toHexString() else ""
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun fileToMd5(file: File): ByteArray? {
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val channel = fis.channel
            val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md = MessageDigest.getInstance("MD5")
            md.update(buffer)
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fis?.close()
        }
        return null
    }
    //</editor-fold>

    //<editor-fold desc="SHA1加密">
    /**
     * SHA1加密
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun sha1(data: ByteArray): ByteArray {
        return encryptAlgorithm(data, "SHA-1")
    }

    /**
     * SHA1加密
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun sha1ToString(data: String): String {
        return sha1ToString(data.toByteArray())
    }

    /**
     * SHA1加密
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun sha1ToString(data: ByteArray): String {
        return sha1(data).toHexString()
    }
    //</editor-fold>

    //<editor-fold desc="SHA224加密">
    /**
     * SHA224加密
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun shA224(data: ByteArray): ByteArray {
        return encryptAlgorithm(data, "SHA-224")
    }

    /**
     * SHA224加密
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun shA224ToString(data: String): String {
        return shA224ToString(data.toByteArray())
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun shA224ToString(data: ByteArray): String {
        return shA224(data).toHexString()
    }
    //</editor-fold>

    //<editor-fold desc="SHA256加密">
    /**
     * SHA256加密
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun shA256(data: ByteArray): ByteArray {
        return encryptAlgorithm(data, "SHA-256")
    }

    /**
     * SHA256加密
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun shA256ToString(data: String): String {
        return shA256ToString(data.toByteArray())
    }

    /**
     * SHA256加密
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun shA256ToString(data: ByteArray): String {
        return shA256(data).toHexString()
    }
    //</editor-fold>

    //<editor-fold desc="SHA512加密">
    /**
     * SHA512加密
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun shA512(data: ByteArray): ByteArray {
        return encryptAlgorithm(data, "SHA-512")
    }

    /**
     * SHA512加密
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun shA512ToString(data: String): String {
        return shA512ToString(data.toByteArray())
    }

    /**
     * SHA512加密
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun shA512ToString(data: ByteArray): String {
        return shA512(data).toHexString()
    }
    //</editor-fold>

    /**
     * 对data进行algorithm算法加密
     * @param data      明文字节数组
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private fun encryptAlgorithm(data: ByteArray, algorithm: String): ByteArray {
        try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(data)
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    //<editor-fold desc="DES、AES加密">
    /**
     * @param data           数据
     * @param key            秘钥
     * @param algorithm      采用何种DES算法
     * @param transformation 转变
     * @return 密文或者明文，适用于DES，3DES，AES
     */
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
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, SecureRandom())
            return cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * DES加密
     * @param data 明文
     * @param key  8字节秘钥
     * @return 密文
     */
    @JvmStatic
    fun des(data: ByteArray, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, "DES", "DES/ECB/NoPadding")
    }

    /**
     * AES加密
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 密文
     */
    @JvmStatic
    fun aes(data: ByteArray, key: ByteArray?): ByteArray? {
        return desTemplate(data, key, "AES", "AES/ECB/NoPadding")
    }
    //</editor-fold>
}