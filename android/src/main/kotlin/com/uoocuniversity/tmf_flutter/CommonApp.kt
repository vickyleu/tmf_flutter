package com.uoocuniversity.tmf_flutter

import android.app.Application
import android.content.Context
import android.os.Bundle
import com.tencent.tmf.portal.Portal

class CommonApp {
    var application: Application? = null
        private set
    private var context: Context? = null
    fun onCreate(application: Application) {
        this.application = application
        COUNTRY = application.resources.getString(R.string.applet_mini_data_country)
        CITY = application.resources.getString(R.string.applet_mini_proxy_city)
        PROVINCE = application.resources.getString(R.string.applet_mini_proxy_province)
        context = application.applicationContext
        initPortal()
    }

    /**
     * 初始化Demo组件框架
     */
    private fun initPortal() {
        // 设置可输出log
        Portal.setDebuggable(false)
        val param = Bundle()
        Portal.init(context, null, param)
    }

    companion object {
        private var sInstance: CommonApp? = null
        fun get(): CommonApp {
            if (sInstance == null) {
                sInstance = CommonApp()
            }
            return sInstance!!
        }

        const val TAG = "TMF_APPLET_DEMO"
        const val IMEI = "test002"
        var COUNTRY = ""
        var PROVINCE = ""
        var CITY = ""
        const val TMF_CONFIGURATIONS = "server/tmf-android-configurations.json"
    }
}