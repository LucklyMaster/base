package com.masterchan.lib.sandbox

/**
 * 沙盒文件访问接口
 * @author: MasterChan
 * @date: 2022-06-09 14:36
 */
interface IFileAccess {

    /**
     * 获取数据库中某个文件夹下的所有文件
     * @param publicDir 公共目录，如果为空，返回数据库的全部文件
     * @return AbsFileBean
     */
    fun listFile(publicDir: String? = null): AbsFileBean?
}