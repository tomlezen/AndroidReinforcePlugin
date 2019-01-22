package com.tlz.androidreinforceplugin.tasks

import com.tlz.androidreinforceplugin.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * 加固任务.
 * 一定要注意权限问题 一定要给加固工具添加相应的权限.
 * Created by Tomlezen.
 * Date: 2019/1/20.
 * Time: 6:17 PM.
 */
open class AndroidReinforceTask : DefaultTask() {

    @TaskAction
    fun doTask() {
        log("******************AndroidReinforceTask Start******************")
        // 配置信息
        val reinforceExtension = project.extensions.getByType(AndroidReinforceExtension::class.java)
        // 获取环境变量里的签名配置
        val keyStore = getKeystoreEnv()
        // 读取加固类型
        val reinforceType = getProperty(Constants.Params.P_REINFORCE_TYPE, reinforceExtension.reinforceType)
        when (reinforceType) {
            Constants.ReinforceType.TYPE_360 -> do360Reinforce(reinforceExtension, keyStore)
            Constants.ReinforceType.TYPE_LE -> doLeReinforce(reinforceExtension, keyStore)
            Constants.ReinforceType.TYPE_QUERY -> {
                do360Reinforce(reinforceExtension, keyStore, true)
                doLeReinforce(reinforceExtension, keyStore, true)
            }
            Constants.ReinforceType.TYPE_ALL -> {
                do360Reinforce(reinforceExtension, keyStore)
                doLeReinforce(reinforceExtension, keyStore)
            }
            else -> log("disable reinforce")
        }
        log("******************AndroidReinforceTask End******************")
    }

    /**
     * 执行360加固.
     * @param keyStore KeyStore
     * @param isQuery Boolean 是否是查询模式.
     */
    private fun do360Reinforce(
        reinforceExtension: AndroidReinforceExtension,
        keyStore: KeyStore,
        isQuery: Boolean = false
    ) {
        // 判断是否支持360加固
        if (!(getProperty(
                Constants.Params.P_SUPPORT_360,
                reinforceExtension.reinforce360.support.toString()
            )?.toBoolean() ?: reinforceExtension.reinforce360.support)
        ) {
            log("do not support 360 reinforce")
            return
        }

        log("==================360 Start")
        // 检查360的环境以及参数.
        val jarPath = getProperty(
            Constants.Params.P_360_PATH,
            getEnv(Constants.ENV_360_PATH, reinforceExtension.reinforce360.path)
        ).checkNull("缺少360加固路径: ${Constants.Params.P_360_PATH}")
        val user =
            getProperty(
                Constants.Params.P_360_USER,
                getEnv(Constants.ENV_360_USER, reinforceExtension.reinforce360.user)
            ).checkNull("缺少360加固平台账户")
        val pass =
            getProperty(
                Constants.Params.P_360_PASS,
                getEnv(Constants.ENV_360_PASS, reinforceExtension.reinforce360.pass)
            ).checkNull("缺少360加固平台账户密码")
        val addConfig =
            getProperty(
                Constants.Params.P_360_ADD_CONFIG,
                reinforceExtension.reinforce360.addConfig
            ) ?: ""
        val autoSign =
            getProperty(
                Constants.Params.P_360_AUTO_SIGN,
                reinforceExtension.reinforce360.autoSign.toString()
            )?.toBoolean() ?: true
        val mulpkg =
            getProperty(
                Constants.Params.P_360_MULPKG,
                reinforceExtension.reinforce360.mulpkg.toString()
            )?.toBoolean() ?: false
        val mulPkgConfigPath =
            getProperty(
                Constants.Params.P_360_MULPKG_CONFIG_PATH,
                reinforceExtension.reinforce360.mulpkgConfigPath
            ) ?: ""
        // 如果开启多渠道 则检查配置路径参数
        if (mulpkg) {
            mulPkgConfigPath.checkNull("缺少多渠道配置文件路径")
        }
        // 找到apk执行加固
        applicationVariants?.filter {
            val fName = it.flavorName.toLowerCase()
            !isQuery || KEYWORDS_360.any { k -> fName.contains(k) }
        }?.forEach { appVariant ->
            // 获取签名信息.
            val (storePath, storePassword, keyAlias, keyPassword) = if (keyStore.path.isBlank()) appVariant.keystore else keyStore
            appVariant.outputs.firstOrNull { it.outputFile.exists() }
                ?.let { variant ->
                    // apk文件
                    val apkFile = variant.outputFile
                    // apk路径
                    val apkPath = apkFile.absolutePath
                    // apk所在的文件夹路径
                    val outApkPath = File(apkFile.parentFile, "reinforce_360").let {
                        if (!it.exists()) {
                            it.mkdirs()
                        }
                        it.absolutePath
                    }

                    // 这里不用shell脚本是为了兼容window
                    "java -jar $jarPath -version".doCommand()
                    "java -jar $jarPath -login $user $pass".doCommand()
                    "java -jar $jarPath -config $addConfig".doCommand()
                    "java -jar $jarPath -importsign $storePath $storePassword $keyAlias $keyPassword".doCommand()
                    "java -jar $jarPath -showsign".doCommand()
                    if (mulpkg) {
                        "java -jar $jarPath -importmulpkg $mulPkgConfigPath".doCommand()
                        "java -jar $jarPath -showmulpkg".doCommand()
                    }
                    "java -jar $jarPath -jiagu $apkPath $outApkPath${if (autoSign) " -autosign" else ""}${if (mulpkg) " -automulpkg" else ""}".doCommand()
                } ?: log("not found 360 reinforce apk for ${appVariant.flavorName}")
        } ?: log("not found 360 reinforce apk")
        log("==================360 End")
    }

    /**
     * 执行乐固加固.
     * @param keyStore KeyStore
     * @param isQuery Boolean
     */
    private fun doLeReinforce(
        reinforceExtension: AndroidReinforceExtension,
        keyStore: KeyStore,
        isQuery: Boolean = false
    ) {
        // 判断是否支持乐固
        if (!(getProperty(
                Constants.Params.P_SUPPORT_LE,
                reinforceExtension.reinforceLe.support.toString()
            )?.toBoolean() ?: reinforceExtension.reinforceLe.support)
        ) {
            log("do not support le reinforce")
            return
        }
        log("==================LE Start")
        // 检查360的环境以及参数.
        val jarPath = getProperty(
            Constants.Params.P_LE_PATH,
            getEnv(Constants.ENV_LE_PATH, reinforceExtension.reinforceLe.path)
        ).checkNull("缺少乐固路径: ${Constants.Params.P_LE_PATH}")
        val secretId =
            getProperty(
                Constants.Params.P_LE_SECRET_ID,
                getEnv(Constants.ENV_LE_SECRET_ID, reinforceExtension.reinforceLe.secretId)
            ).checkNull("缺少乐固认证id: secretId")
        val secretKey =
            getProperty(
                Constants.Params.P_LE_SECRET_KEY,
                getEnv(Constants.ENV_LE_SECRET_KEY, reinforceExtension.reinforceLe.secretKey)
            ).checkNull("缺少乐固认证key: secretKey")
        val autoSign =
            getProperty(
                Constants.Params.P_LE_AUTO_SIGN,
                reinforceExtension.reinforceLe.autoSign.toString()
            )?.toBoolean() ?: true

        // 找到apk加固
        applicationVariants?.filter {
            val fName = it.flavorName.toLowerCase()
            !isQuery || KEYWORDS_LE.any { k -> fName.contains(k) }
        }?.forEach { appVariant ->
            // 获取签名信息.
            val (storePath, storePassword, keyAlias, keyPass) = if (keyStore.path.isBlank()) appVariant.keystore else keyStore
            appVariant.outputs.firstOrNull { it.outputFile.exists() }
                ?.let { variant ->
                    // apk文件
                    val apkFile = variant.outputFile
                    // apk路径
                    val apkPath = apkFile.absolutePath
                    // apk所在的文件夹路径
                    val outApkPath = File(apkFile.parentFile, "reinforce_360").absolutePath

                    // 执行加固操作
                    "java -Dfile.encoding=utf-8 -jar $jarPath -sid $secretId -skey $secretKey -uploadPath $apkPath -downloadPath $outApkPath".doCommand()

                    // 如果不是window环境 则执行加固操作
                    if (!isWindowOs && autoSign) {
                        // android sdk 路径
                        val androidHome = getEnv(Constants.ENV_ANDROID_HOME).checkNull("缺少android sdk环境变量")
                        // 编译工具
                        val buildToolPath = File("$androidHome/build-tools").listFiles().last().absolutePath
                        // 签名工具路径
                        val signerPath = "$buildToolPath/apksigner"
                        // 对齐工具路径
                        val zipalignPath = "$buildToolPath/zipalign"
                        val apkFileName = apkFile.nameWithoutExtension
                        val leguFilePath = "${apkFileName}_legu.apk"
                        val leguSignedFilePath = "${apkFileName}_legu_signed.apk"
                        val zipFilePath = "${apkFileName}_le.apk"

                        // 构建shell文件内容
                        val shellString = StringBuilder()
                        shellString.append("#!/bin/bash\n\n")
                        shellString.append("echo \"------ sign start ------\"\n")
//                        shellString.append(
//                            "$signerPath sign --ks $storePath $leguSignedFilePath <<EOF\n" +
//                                    "$storePassword\n" +
//                                    "EOF"
//                        )
                        shellString.append("$signerPath sign --ks $storePath $leguSignedFilePath --ks-pass $storePassword --ks-key-alias $keyAlias --ks-pass $keyPass\n")
                        shellString.append("echo \"------ sign finish ------\"\n")
                        shellString.append("echo \"------ zipalign start ------\"\n")
                        shellString.append("$zipalignPath -v 4 $leguSignedFilePath $zipFilePath\n")
                        shellString.append("echo \"------ zipalign finish ------\"\n")

                        val shellFile = newShellFile(Constants.SHELL_FILE_NAME_LE)
                        shellFile.writeText(shellString.toString())
                        shellFile.doShell()

                        // 删除中间文件
                        File(leguFilePath).delete()
                        File(leguSignedFilePath).delete()
                        log("auto sign completed")
                    } else if (isWindowOs) {
                        log("乐固暂时不支持window自动签名")
                    }
                }
        } ?: log("not found 360 reinforce apk")
        log("==================LE End")
    }

    /**
     * 获取签名配置.
     * 先读取属性参数 在读取环境变量
     * @return KeyStore
     */
    private fun getKeystoreEnv(): KeyStore {
        val keyStorePath = getProperty(Constants.Params.P_STORE_PATH, getEnv(Constants.ENV_STORE_PATH))
        val keyStorePass = getProperty(Constants.Params.P_STORE_PASS, getEnv(Constants.ENV_STORE_PASS))
        val keyStoreAlias = getProperty(Constants.Params.P_STORE_KEY_ALIAS, getEnv(Constants.ENV_KEY_ALIAS))
        val keyStoreAliasPass = getProperty(Constants.Params.P_STORE_KEY_PASS, getEnv(Constants.ENV_KEY_PASS))
        return KeyStore(keyStorePath ?: "", keyStorePass ?: "", keyStoreAlias ?: "", keyStoreAliasPass ?: "")
    }

    /**
     * 检查参数是否为空.
     * @receiver String?
     * @param message String
     * @return String
     */
    private fun String?.checkNull(message: String): String {
        if (isNullOrBlank()) {
            throw AssertionError(message)
        }
        return this.toString()
    }

    /**
     * 新建一个shell文件.
     * @param fileName String
     * @return File
     */
    private fun newShellFile(fileName: String): File {
        val shellFile = project.file(fileName)
        if (shellFile.exists()) {
            shellFile.delete()
        }
        shellFile.createNewFile()
        return shellFile
    }

    data class KeyStore(
        val path: String,
        val pass: String,
        val alias: String,
        val aliasPass: String
    )

    companion object {
        /** 乐固关键词. */
        val KEYWORDS_LE = arrayOf("yyb", "legu", "yingyongbao", "tencent")
        /** 360加固关键词. */
        val KEYWORDS_360 = arrayOf("360", "qihu")
    }
}