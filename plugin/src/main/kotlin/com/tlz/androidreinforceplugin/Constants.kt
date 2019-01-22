package com.tlz.androidreinforceplugin

/**
 * Created by Tomlezen.
 * Date: 2019/1/20.
 * Time: 6:26 PM.
 */
object Constants {
    /** 签名文件路径. */
    const val ENV_STORE_PATH = "KEY_STORE_PATH"
    /** 签名文件密码. */
    const val ENV_STORE_PASS = "KEY_STORE_PASS"
    /** 签名文件别名. */
    const val ENV_KEY_ALIAS = "KEY_KEY_ALIAS"
    /** 签名文件别名密码. */
    const val ENV_KEY_PASS = "KEY_KEY_PASS"

    /** 安卓sdk路径环境变量. */
    const val ENV_ANDROID_HOME = "ANDROID_HOME"
    /** 360账户环境变量. */
    const val ENV_360_USER = "360_USER"
    /** 360密码环境变量. */
    const val ENV_360_PASS = "360_PASS"
    /** 360加固文件路径环境变量. */
    const val ENV_360_PATH = "360_PATH"
    /** 乐固密钥id环境变量. */
    const val ENV_LE_SECRET_ID = "LE_SECRET_ID"
    /** 乐固密钥key环境变量. */
    const val ENV_LE_SECRET_KEY = "LE_SECRET_KEY"
    /** 乐固加固文件路径环境变量. */
    const val ENV_LE_PATH = "LE_PATH"

    /** 360加固shell文件名. */
    const val SHELL_FILE_NAME_360 = "reinforce_360.sh"
    /** 乐固加固shell文件名. */
    const val SHELL_FILE_NAME_LE = "reinforce_le.sh"

    object Params {
        /** 是否支持360加固. */
        const val P_SUPPORT_360 = "support_360"
        /** 是否支持乐固. */
        const val P_SUPPORT_LE = "support_le"

        /** 加固类型. */
        const val P_REINFORCE_TYPE = "reinforce_type"

        /** 360平台账户. */
        const val P_360_USER = "360_user"
        /** 360平台账户密码. */
        const val P_360_PASS = "360_pass"
        /** 360平加固文件路径. */
        const val P_360_PATH = "360_path"
        /** 是否开启多渠道打包. */
        const val P_360_MULPKG = "360_mulpkg"
        /** 多渠道配置文件路径. */
        const val P_360_MULPKG_CONFIG_PATH = "360_mulpkg_config_path"
        /** 增值服务配置. */
        const val P_360_ADD_CONFIG = "360_add_config"
        /** 是否自动签名. */
        const val P_360_AUTO_SIGN = "360_auto_sign"

        /** 乐固密钥id. */
        const val P_LE_SECRET_ID = "le_secret_id"
        /** 乐固密钥key. */
        const val P_LE_SECRET_KEY = "le_secret_key"
        /** 乐固加固文件路径. */
        const val P_LE_PATH = "le_path"
        /** 是否自动签名. */
        const val P_LE_AUTO_SIGN = "le_auto_sign"

        /** 签名文件路径. */
        const val P_STORE_PATH = "store_path"
        /** 签名文件密码. */
        const val P_STORE_PASS = "store_pass"
        /** 签名文件别名. */
        const val P_STORE_KEY_ALIAS = "store_key_alias"
        /** 签名文件别名密码. */
        const val P_STORE_KEY_PASS = "store_key_pass"
    }

    /**
     * 加固类型.
     */
    object ReinforceType {
        /** 360类型. */
        const val TYPE_360 = "360"
        /** 乐固类型. */
        const val TYPE_LE = "le"
        /** 所有类型 每个包都进行所有类型的加固. */
        const val TYPE_ALL = "all"
        /** 查询类型 就是根据flavorName来选择加固类型 例如果含有360字样 就采用360加固. */
        const val TYPE_QUERY = "query"
    }

}