package com.ling.ding.sighed.txtbean

import com.ling.ding.sighed.txtmain.FirstRunFun

// 1. 定义环境接口与配置装载器
interface EnvironmentProvider {
    val config: ConfigBean
    val isProduction: Boolean
}

object EnvironmentManager {

    private val environments = mutableMapOf<String, EnvironmentProvider>()
    private var currentEnvKey = ""

    // 3. 初始化时注册环境
    init {
        registerEnvironment(
            key = "TEST",
            provider = object : EnvironmentProvider {
                override val config = ConfigBean(
                    appid = "114FE8DB631B3389BDDDD15D81E45E39",
                    openid = "0A600053F2B2775FF79B1CD046A0098C",
                    upUrl = "https://test-symbol.healthbmishapetrackloport.com/adverse/concede/parmesan",
                    adminUrl = "https://shape.healthbmishapetrackloport.com/apitest/bmishow/guso/",
                    appsflyId = "5MiZBZBjzzChyhaowfLpyR"
                )
                override val isProduction = false
            }
        )

        registerEnvironment(
            key = "PROD",
            provider = object : EnvironmentProvider {
                override val config = ConfigBean(
                    appid = "CFCF8BF2948E6C1CAF8275D84DF6DEBF",
                    openid = "581B0101BF293AF6EFB819837739BAFB",
                    upUrl = "https://symbol.healthbmishapetrackloport.com/inhere/street/reap",
                    adminUrl = "https://shape.healthbmishapetrackloport.com/api/bmishow/guso/",
                    appsflyId = "DZffXqFmucM4KLbRT8r3wS"
                )
                override val isProduction = true
            }
        )
    }

    // 4. 环境注册方法
    fun registerEnvironment(key: String, provider: EnvironmentProvider) {
        environments[key] = provider
        if (environments.size == 1) currentEnvKey = key
    }

    // 5. 动态切换方法
    fun switchEnvironment(key: String) {
        environments[key]?.let { currentEnvKey = key }
    }

    // 6. 获取当前配置（适配原有逻辑）
    fun getCurrentConfig(): ConfigBean {
        return environments[currentEnvKey]?.config ?: throw IllegalStateException("Environment not initialized")
    }

    // 7. 兼容原有判断逻辑
    fun isProductionEnv() = environments[currentEnvKey]?.isProduction ?: false
}

// 8. 改造后的获取方法
fun getConfig(isXS: Boolean = FirstRunFun.isVps): ConfigBean {
    return if (isXS) EnvironmentManager.getCurrentConfig()
    else EnvironmentManager.getCurrentConfig().also {
    }
}
