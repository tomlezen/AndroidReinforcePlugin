package com.tlz.androidreinforceplugin

import com.tlz.androidreinforceplugin.tasks.AndroidReinforceTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Tomlezen.
 * Date: 2019/1/20.
 * Time: 11:31 AM.
 */
class Plugin : Plugin<Project> {
    override fun apply(target: Project) {
        // 检查是否是Android工程
        if (target.plugins.hasPlugin("com.android.application")) {
            createExtensions(target)
            createTasks(target)

            target.afterEvaluate {
                applyTask(target)
            }
        } else {
            throw IllegalArgumentException("AndroidReinforcePlugin requires the Android plugin to be configured")
        }
    }

    /**
     * 创建扩展.
     * @param target Project
     */
    private fun createExtensions(target: Project) {
        target.extensions.create("AndroidReinForce", AndroidReinforceExtension::class.java)
    }

    /**
     * 创建任务.
     * @param target Project
     */
    private fun createTasks(target: Project) {
        target.tasks.create("androidReinforce", AndroidReinforceTask::class.java)
    }

    /**
     * 应用任务.
     * @param target Project
     */
    private fun applyTask(target: Project) {
        // 遍历任务
        target.tasks.filter { it.name.contains("assemble(.*)Release".toRegex()) }
            .forEach {
                it.finalizedBy("androidReinforce")
            }
    }
}