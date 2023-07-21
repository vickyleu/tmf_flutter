package com.uoocuniversity.tmf_flutter.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tmf.share.api.TMFShareService
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class WXEntryActivity : Activity(), IWXAPIEventHandler {
    private var api: IWXAPI? = null
    private var handler: MyHandler? = null

    private class MyHandler(wxEntryActivity: WXEntryActivity) : Handler() {
        var openId = ""
        var accessToken: String? = null
        var refreshToken: String? = null
        var scope = ""
        private val wxEntryActivityWeakReference: WeakReference<WXEntryActivity>

        init {
            wxEntryActivityWeakReference = WeakReference(wxEntryActivity)
        }

        override fun handleMessage(msg: Message) {
            val tag = msg.what
            val data = msg.data
            var json: JSONObject? = null
            when (tag) {
                NetworkUtil.GET_TOKEN -> {
                    try {
                        json = JSONObject(data.getString("result"))
                        Log.i(TAG, "NetworkUtil.GET_TOKEN $json")
                        openId = json.getString("openid")
                        accessToken = json.getString("access_token")
                        refreshToken = json.getString("refresh_token")
                        scope = json.getString("scope")
                        Log.i(TAG, "get weachat login1 ret $json")
                        //                        Toast.makeText(wxEntryActivityWeakReference.get().getApplicationContext(), "获取Token" + json,
//                                Toast.LENGTH_LONG).show();
                        NetworkUtil.sendWxAPI(
                            this, String.format(
                                "https://api.weixin.qq.com/sns/auth?" +
                                        "access_token=%s&openid=%s", accessToken, openId
                            ), NetworkUtil.CHECK_TOKEN
                        )
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message!!)
                    }
                }

                NetworkUtil.GET_INFO -> {
                    try {
                        json = JSONObject(data.getString("result"))
                        Log.i(TAG, "NetworkUtil.GET_INFO get wx accountInfo $json")

//                        Toast.makeText(wxEntryActivityWeakReference.get().getApplicationContext(), "获取账户信息" + json,
//                                Toast.LENGTH_LONG).show();
                        json.put("success", true)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    WxApiUtil.instance.sendLoginCallBack(json)
                }

                NetworkUtil.CHECK_TOKEN -> {
                    try {
                        json = JSONObject(data.getString("result"))
                        Log.i(TAG, "NetworkUtil.CHECK_TOKEN $json")
                        val errcode = json.getInt("errcode")
                        if (errcode == 0) {
                            Log.i(TAG, "CHECK_TOKEN success $accessToken")
                            NetworkUtil.sendWxAPI(
                                this, String.format(
                                    "https://api.weixin.qq.com/sns/userinfo?" +
                                            "access_token=%s&openid=%s", accessToken, openId
                                ), NetworkUtil.GET_INFO
                            )
                        } else {
                            NetworkUtil.sendWxAPI(
                                this, String.format(
                                    "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                                            "appid=%s&grant_type=refresh_token&refresh_token=%s",
                                    WxApiUtil.Companion.APP_ID,
                                    refreshToken
                                ),
                                NetworkUtil.REFRESH_TOKEN
                            )
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message!!)
                    }
                }

                NetworkUtil.REFRESH_TOKEN -> {
                    try {
                        json = JSONObject(data.getString("result"))
                        Log.i(TAG, "NetworkUtil.REFRESH_TOKEN $json")
                        openId = json.getString("openid")
                        accessToken = json.getString("access_token")
                        refreshToken = json.getString("refresh_token")
                        scope = json.getString("scope")
                        NetworkUtil.sendWxAPI(
                            this, String.format(
                                "https://api.weixin.qq.com/sns/userinfo?" +
                                        "access_token=%s&openid=%s", accessToken, openId
                            ), NetworkUtil.GET_INFO
                        )
                    } catch (e: JSONException) {
                        Log.e(TAG, e.message!!)
                    }
                }
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isDebugWxLogin(intent)) {
            api = WXAPIFactory.createWXAPI(this, WxApiUtil.APP_ID, false)
            handler = MyHandler(this)
            try {
                val intent = intent
                api?.handleIntent(intent, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            TMFShareService.getInstance().handleWxIntent(intent)
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isDebugWxLogin(getIntent())) {
            setIntent(intent)
            api!!.handleIntent(intent, this)
        } else {
            TMFShareService.getInstance().handleWxIntent(getIntent())
            finish()
        }
    }

    override fun onReq(req: BaseReq) {
        when (req.type) {
            ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX -> {}
            ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX -> {}
            else -> {}
        }
        //        finish();
    }

    override fun onResp(resp: BaseResp) {
        val result = 0
        //        Log.i(App.TAG,"get data resp type {} code {} errStr {}", resp.getType(), resp.errCode, resp.errStr);
        if (resp.type == ConstantsAPI.COMMAND_SENDAUTH) {
            val authResp = resp as SendAuth.Resp
            val code = authResp.code
            //            Log.i(App.TAG,"wechat http auth {} {}", code);
            NetworkUtil.sendWxAPI(
                handler, String.format(
                    "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                            "appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    WxApiUtil.Companion.APP_ID,
                    "77d441ad8a73dfd17471b6901eade71c",
                    code
                ), NetworkUtil.GET_TOKEN
            )
        }
        finish()
    }

    private val userInfo: Unit
        ///sns/userinfo
        private get() {}

    private fun isDebugWxLogin(intent: Intent): Boolean {
        val data = intent.extras
        val builder = StringBuilder("extra")
        for (key in data!!.keySet()) {
            builder.append(key).append(":").append(data[key]).append("|")
        }
        val wxapi_sendauth_resp_token = data.getString("_wxapi_sendauth_resp_token")
        Log.i(TAG, "wxentry handle intent {} with scope $builder")
        return !TextUtils.isEmpty(wxapi_sendauth_resp_token)
    }

    private fun showLongToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TAG = "TMF_APPLET_DEMO"
    }
}