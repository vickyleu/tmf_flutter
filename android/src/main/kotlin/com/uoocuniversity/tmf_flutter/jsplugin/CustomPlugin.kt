package com.uoocuniversity.tmf_flutter.jsplugin

import android.util.Log
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmfmini.sdk.annotation.JsEvent
import com.tencent.tmfmini.sdk.annotation.JsPlugin
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin
import com.uoocuniversity.tmf_flutter.CommonApp
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
        try {
            TmfMiniSDK.stopAllMiniApp(this.mContext.applicationContext)
        }catch (e:Exception){}
        req.ok(JSONObject())
    }
}