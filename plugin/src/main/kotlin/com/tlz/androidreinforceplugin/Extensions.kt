package com.tlz.androidreinforceplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.tlz.androidreinforceplugin.tasks.AndroidReinforceTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

/**
 * Created by Tomlezen.
 * Date: 2019/1/20.
 * Time: 6:56 PM.
 */

/**
 * 获取系统属性.
 * @param key String
 * @param default String?
 * @return String?
 */
fun getProperty(key: String, default: String? = ""): String? = System.getProperty(key, default)

/**
 * 获取系统环境变量.
 * @param key String
 * @param default String
 * @return String
 */
fun getEnv(key: String, default: String = ""): String = System.getenv(key) ?: default


/**
 * 获取所有应用变体.
 */
val DefaultTask.applicationVariants
    get() = project.applicationVariants

/**
 * 获取所有应用变体.
 */
val Project.applicationVariants
    get() = project.extensions
        .findByType(AppExtension::class.java)
        ?.applicationVariants
        ?.filter { it.buildType.name == "release" }

/**
 * 执行shell文件.
 * @receiver File
 */
fun File.doShell() = "sh $absolutePath >> logfile".doCommand()


/**
 * 执行命令.
 * @receiver String
 */
fun String.doCommand() {
    println("run command: $this")
    val process = Runtime.getRuntime().exec(this)
    val read = BufferedReader(InputStreamReader(process.inputStream))
    var line: String? = read.readLine()
    while (line != null) {
        println(line)
        line = read.readLine()
    }
    read.close()
    val error = BufferedReader(InputStreamReader(process.errorStream))
    line = error.readLine()
    var isError = false
    while (line != null) {
        println(line)
        line = error.readLine()
        isError = true
    }
//    process.waitFor()
    process.destroy()
    // 如果出现错误直接抛出异常 终止执行
    if (isError) throw Exception("cmd命令执行失败，$this")
}

/**
 * 获取签名.
 */
val ApplicationVariant.keystore: AndroidReinforceTask.KeyStore
    get() = with(signingConfig) {
        if (storeFile == null || !storeFile.exists()) {
            throw IllegalArgumentException("您没有配置签名")
        }
        AndroidReinforceTask.KeyStore(storeFile.absolutePath, storePassword, keyAlias, keyPassword)
    }

/**
 * 判断是否是window系统.
 */
val isWindowOs: Boolean
    get() = System.getProperty("os.name")?.toLowerCase() == "window"

/**
 * 获取项目local.properties文件的属性.
 * @receiver Project
 * @param key String
 * @param default String
 * @return String
 */
fun Project.getLocalProperty(key: String, default: String = ""): String =
    Properties().also { p ->
        p.load(rootProject.file("local.properties").inputStream())
    }.getProperty(key, default)

/**
 * 删除文件.
 * @receiver String
 */
fun String.deleteFile() {
    val file = File(this)
    if (file.exists()) {
        file.deleteRecursively()
    }
}