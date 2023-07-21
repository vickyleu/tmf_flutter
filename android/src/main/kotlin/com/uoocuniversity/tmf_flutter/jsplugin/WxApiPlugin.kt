package com.uoocuniversity.tmf_flutter.jsplugin

import android.util.Log
import com.tencent.tmfmini.sdk.annotation.JsEvent
import com.tencent.tmfmini.sdk.annotation.JsPlugin
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin
import org.json.JSONException
import org.json.JSONObject

@JsPlugin(secondary = true)
class WxApiPlugin : BaseJsPlugin() {
    /**
     * 对应小程序wx.login调用
     *
     * @param req
     */
    @JsEvent("wx.login")
    fun login(req: RequestEvent) {
        //获取参数
        //req.jsonParams
        //异步返回数据
        //req.fail();
        //req.ok();
        Log.wtf("WxApiPlugin", "login")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "wx.login")
            val isSuccess = false
            jsonObject.put("code", if (isSuccess) "1" else "0")
            if (isSuccess) {
                jsonObject.put("authCode", "authCode")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }

    /**
     * 对应小程序wx.getUserInfo调用
     *
     * @param req
     */
    @JsEvent("wx.getUserInfo")
    fun getUserInfo(req: RequestEvent) {
        //获取参数
        //req.jsonParams
        //异步返回数据
        //req.fail();
        //req.ok();
        Log.wtf("WxApiPlugin", "getUserInfo")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "wx.getUserInfo")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }

    /**
     * 对应小程序wx.getUserProfile调用
     *
     * @param req
     */
    @JsEvent("wx.getUserProfile")
    fun getUserProfile(req: RequestEvent) {
        //获取参数
        //req.jsonParams
        //异步返回数据
        //req.fail();
        //req.ok();
        Log.wtf("WxApiPlugin", "getUserProfile")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("key", "wx.getUserProfile")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        req.ok(jsonObject)
    }
}