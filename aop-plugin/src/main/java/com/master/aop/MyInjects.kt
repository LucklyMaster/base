package com.master.aop

import javassist.ClassPool
import org.gradle.api.Project
import java.io.File

/**
 *
 * @author: MasterChan
 * @date: 2022-08-08 17:22
 */
object MyInjects {
    private val pool: ClassPool = ClassPool.getDefault()
    fun inject(path: String, project: Project) {
        //将当前路径加入类池,不然找不到这个类
        pool.appendClassPath(path)
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        // pool.appendClassPath(project.android.bootClasspath[0].toString())

        println("layout = ${project.layout}")
        println("${project.properties}")
        println("${project.gradle}")

        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        // pool.importPackage("android.os.Bundle")

        val dir = File(path);
        if (dir.isDirectory) {
            //遍历文件夹
            listAll(dir).forEach { file ->
                val filePath = file.absolutePath
                println("filePath = $filePath")
                if (file.name.equals("MainActivity.class")) {
                    //获取MainActivity.class
                    val ctClass = pool.getCtClass("com.master.mybase.activity.MainActivity");
                    println("ctClass = $ctClass")
                    //解冻
                    if (ctClass.isFrozen) {
                        ctClass.defrost()
                    }
                    //获取到OnCreate方法
                    val ctMethod = ctClass.getDeclaredMethod("printAppInfo")
                    println("方法名 = $ctMethod")

                    val insetBeforeStr = """ 
                       com.master.lib.log.MLog.INSTANCE.d("拒绝");
                                            """
                    //在方法开头插入代码

                    ctMethod.insertBefore(insetBeforeStr)
                    ctClass.writeFile(path)
                    ctClass.detach()//释放
                }
            }
        }
    }

    private fun listAll(file: File): List<File> {
        val list = mutableListOf<File>()
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                list.addAll(listAll(it))
            } else {
                list.add(it)
            }
        }
        return list
    }
}