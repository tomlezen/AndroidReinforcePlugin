package com.tlz.androidreinforceplugin

import org.gradle.api.Action

/**
 * 此配置的优先级关系是如下：
 * extension > 环境配置 > 任务执行参数
 * Created by Tomlezen.
 * Date: 2019/1/20.
 * Time: 11:43 AM.
 */
open class AndroidReinforceExtension {

    /** 360加固配置. */
    var reinforce360: Reinforce360Extension = Reinforce360Extension()

    /** 腾讯加固配置. */
    var reinforceLe: ReinforceLeExtension = ReinforceLeExtension()

    /** 加固类型，默认没有任何加固类型 */
    var reinforceType = ""

    fun reinforce360(action: Action<Reinforce360Extension>) {
        action.execute(reinforce360)
    }

    fun reinforceLe(action: Action<ReinforceLeExtension>) {
        action.execute(reinforceLe)
    }

    open class Reinforce360Extension : ReinforceExtensions() {
        /** 用户. */
        var user = ""
        /** 密码. */
        var pass = ""

        /** 多渠道. */
        var mulpkg = false
        /** 多渠道配置路径. */
        var mulpkgConfigPath = ""
        /** 增值配置 默认配置只支持x86架构. 其它 -data -crashlog -vmp */
        var addConfig = "-x86"
    }

    open class ReinforceLeExtension : ReinforceExtensions() {
        /** 密钥id。 */
        var secretId = ""
        /** 密钥值. */
        var secretKey = ""
    }

    open class ReinforceExtensions {
        /** 自动签名 默认开启自动签名. */
        var autoSign = true
        /** 路径. */
        var path = ""
        /** 是否支持加固  默认都是不支持状态. */
        var support = false
//        /** 加固文件输出路径. 为空则使用默认路径即原来apk路径下的reinforce_[reinforceType]. */
//        var outputPath = ""
//        /** 加固文件输出文件名. 为空则使用默认文件名： app-[flavors]-[buildType]-[reinforceType]-signed */
//        var outputFileName = ""
    }

}