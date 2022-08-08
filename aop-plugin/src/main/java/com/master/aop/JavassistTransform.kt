package com.master.aop

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 *
 * @author: MasterChan
 * @date: 2022-08-08 17:16
 */
class JavassistTransform(private val project: Project) : Transform() {
    override fun getName(): String {
        return "JavassistTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(
        context: Context?,
        inputs: MutableCollection<TransformInput>?,
        referencedInputs: MutableCollection<TransformInput>?,
        outputProvider: TransformOutputProvider?,
        isIncremental: Boolean
    ) {
        inputs?.forEach { input ->
            //遍历文件夹
            input.directoryInputs.forEach { directoryInput ->
                //注入代码
                MyInjects.inject(directoryInput.file.absolutePath, project)
                // 获取output目录
                val dest = outputProvider?.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                )
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            ////遍历jar文件 对jar不操作，但是要输出到out路径
            input.jarInputs.forEach { jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                var jarName = jarInput.name
                println("jar = " + jarInput.file.absolutePath)
                val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }
                val dest = outputProvider?.getContentLocation(
                    jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}