package com.uoocuniversity.tmf_flutter.wxapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.webkit.ValueCallback
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONObject

class WxApiUtil {
    var api: IWXAPI? = null
    var callback: ValueCallback<JSONObject?>? = null
    fun regToWx(context: Context) {
        // 通过 WXAPIFactory 工厂，获取 IWXAPI 的实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, true)

        // 将应用的 appId 注册到微信
        api?.registerApp(APP_ID)

        //建议动态监听微信启动广播进行注册到微信
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                // 将该 app 注册到微信
                api?.registerApp(APP_ID)
            }
        }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
    }

    fun sendLogin(jsonObject: JSONObject?, callback: ValueCallback<JSONObject?>?) {
        this.callback = callback
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wechat_sdk_demo_test"
        val ret = api!!.sendReq(req)
        Log.i(WXEntryActivity.Companion.TAG, "send login to wx $ret")
    }

    fun sendLoginCallBack(jsonObject: JSONObject?) {
        if (null != callback) {
            callback!!.onReceiveValue(jsonObject)
        }
    }

    private object Holder {
        internal val sInstance = WxApiUtil()
    }

    companion object {
        val instance:WxApiUtil = Holder.sInstance
        const val APP_ID = "wx8071141a542f9dad"
    }
}