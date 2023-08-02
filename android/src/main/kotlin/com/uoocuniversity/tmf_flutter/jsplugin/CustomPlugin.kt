package com.uoocuniversity.tmf_flutter.jsplugin

import android.util.Log
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmfmini.sdk.MiniSDK
import com.tencent.tmfmini.sdk.annotation.JsEvent
import com.tencent.tmfmini.sdk.annotation.JsPlugin
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin
import com.uoocuniversity.tmf_flutter.CommonApp
import com.uoocuniversity.tmf_flutter.TmfFlutterPlugin
import com.uoocuniversity.tmf_flutter.src.TmfFlutterApi
import com.uoocuniversity.tmf_flutter.src.impl.CommonSp
import org.json.JSONException
import org.json.JSONObject

@JsPlugin(secondary = true)
class CustomPlugin : BaseJsPlugin() {


    @JsEvent("custom_event")
    fun custom(req: RequestEvent) {
        //获取参数
        //req.jsonParams
        //异步返回数据
        //req.fail();
        //req.ok();
        Log.d(CommonApp.TAG, "CustomPlugin=" + req.jsonParams)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "test")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }

    @JsEvent("getSystemInfo", "getSystemInfoSync")
    fun custom1(req: RequestEvent?): String {
        //获取参数
        //req.jsonParams
        //同步返回数据(必须返回json数据)
        return JSONObject().toString()
    }

    @JsEvent("log")
    fun log(req: RequestEvent) {
        Log.wtf(CommonApp.TAG, "log=>${req.jsonParams}")
        req.ok(JSONObject())
    }

    @JsEvent("destroyTMF")
    fun destroyTMF(req: RequestEvent) {
        Log.wtf(CommonApp.TAG, "destroyTMF")
        try {
            this.mMiniAppContext.attachedActivity?.finish()
        } catch (ignore: Exception) {
        }
        try {
            CommonSp.instance.removeUserName()
            TmfMiniSDK.logoutTmf()
            CommonSp.instance.removeSkipLogin()
        } catch (ignore: Exception) {
        }
        try {
            TmfMiniSDK.setUserId(null)
        } catch (ignore: Exception) {
        }
        try {
            TmfMiniSDK.stopAllMiniApp(this.mMiniAppContext.context.applicationContext)
        } catch (ignore: Exception) {
            Log.wtf("清除缓存","stopAllMiniApp")
            ignore.printStackTrace()
        }
        try {
            TmfFlutterPlugin.logout()
        } catch (ignore: Exception) {
            Log.wtf("清除缓存","stopAllMiniApp")
            ignore.printStackTrace()
        }
        req.ok(JSONObject())
    }
}