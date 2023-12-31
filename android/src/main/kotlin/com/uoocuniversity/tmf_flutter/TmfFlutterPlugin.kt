package com.uoocuniversity.tmf_flutter

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import android.widget.Toast
import com.tencent.tmf.base.api.utils.AppUtil
import com.tencent.tmf.mini.api.TmfMiniSDK
import com.tencent.tmf.mini.api.bean.MiniCode
import com.tencent.tmf.mini.api.bean.MiniInitConfig
import com.tencent.tmf.mini.api.bean.MiniScene
import com.tencent.tmf.mini.api.bean.MiniStartLinkOptions
import com.tencent.tmf.mini.api.bean.MiniStartOptions
import com.uoocuniversity.tmf_flutter.src.Code
import com.uoocuniversity.tmf_flutter.src.MessageData
import com.uoocuniversity.tmf_flutter.src.TmfFlutterApi
import com.uoocuniversity.tmf_flutter.src.TmfHostApi
import com.uoocuniversity.tmf_flutter.src.impl.CommonSp
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/** TmfFlutterPlugin */
class TmfFlutterPlugin : FlutterPlugin, ActivityAware, TmfHostApi {
    private var mActivityAware: Activity? = null

    private val simpleFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private val receiver: ResultReceiver =
        object : ResultReceiver(Handler(Looper.getMainLooper()!!)) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == MiniCode.STATUS_CODE_SERVER_REQUEST_DELETE) {
                    //小程序下架逻辑处理, 移除列表小程序
                    val appId = resultData!!.getString(MiniCode.KEY_APPID)
                    val appVerType = resultData.getInt(MiniCode.KEY_APP_VER_TYPE)
//                mAppAdapter.remove(appId, appVerType)
                } else if (resultCode != MiniCode.CODE_OK) {
                    //小程序启动错误
                    val errMsg = resultData!!.getString(MiniCode.KEY_ERR_MSG)
                    val aware = mActivityAware ?: return let {
                        println("当前页面已释放")
                        return@let
                    }
                    Toast.makeText(aware, errMsg + resultCode, Toast.LENGTH_SHORT).show()
                }
            }
        }

    companion object {
        const val TMF_CHANNEL = "com.uooc.tmf_channel"
        private var flutterApi: TmfFlutterApi? = null
        fun create(context: Context) {
            CommonApp.get().onCreate(context.applicationContext as Application)
            val builder = MiniInitConfig.Builder()
            builder.configAssetName(CommonApp.TMF_CONFIGURATIONS)
            val config = builder
                .verifyPkg(false) //可选
                .imei(CommonApp.IMEI) //可选  TODO 设备唯一标识符,目前是写死的
                .debug(false)
                .privacyAuth(true) //隐私授权
                .build()
            TmfMiniSDK.init(context.applicationContext as Application, config)
            if (AppUtil.isMainProcess(context)) {
                //同意隐私授权
                TmfMiniSDK.agreePrivacyAuth()
                //只有隐私授权后才能调用TmfMiniSDK相关API
                TmfMiniSDK.setLocation(
                    CommonApp.COUNTRY,
                    CommonApp.PROVINCE,
                    CommonApp.CITY
                )
                TmfMiniSDK.preloadMiniApp(context, null)
            }
        }

        fun logout() {
            flutterApi?.logout { }
        }
    }

    override fun initTmf(callback: (Result<Unit>) -> Unit) {
        val aware = mActivityAware ?: return let {
            callback.invoke(Result.failure(IllegalStateException("当前页面已释放")))
            return@let
        }
        if (!AppUtil.isMainProcess(aware.application)) {
            return let {
                callback.invoke(Result.failure(IllegalStateException("非app进程")))
                return@let
            }
        }
//        QMUISwipeBackActivityManager.init(aware.application)
        CommonSp.instance.putPrivacyAuth(aware.application, true)
        aware.runOnUiThread {
            callback.invoke(Result.success(Unit))
//            if (application == null) {
//
//            }
        }
    }

    override fun loginTmf(
        account: String,
        password: String,
        isOpenLogin: Boolean,
        callback: (Result<Boolean>) -> Unit
    ) {
        val aware = mActivityAware ?: return let {
            callback.invoke(Result.failure(IllegalStateException("当前页面已释放")))
            return@let
        }
        aware.runOnUiThread {
            TmfMiniSDK.loginTmf(account, password, isOpenLogin) { code, msg, _ ->
                if (code == MiniCode.CODE_OK) {
                    if (!TmfMiniSDK.isLoginOvertime()) {
                        TmfMiniSDK.setUserId(account)
                        CommonSp.instance.putUserName(aware.applicationContext, account)
                        callback.invoke(Result.success(true))
                    } else {
                        callback.invoke(Result.failure(IllegalAccessException("登录超时了")))
                    }
                } else {
                    callback.invoke(Result.failure(IllegalAccessException(msg)))
                }
            }
        }
        /**/
    }

    override fun destroy(callback: (Result<Unit>) -> Unit) {
        CommonSp.instance.removeUserName()
        TmfMiniSDK.logoutTmf()
        CommonSp.instance.removeSkipLogin()

    }

    override fun sendMessage(message: MessageData, callback: (Result<Boolean>) -> Unit) {
        val aware = mActivityAware ?: return let {
            callback.invoke(Result.failure(IllegalStateException("当前页面已释放")))
            return@let
        }
        when (message.code) {
            Code.TMFID -> {
                val data = message.data
                val token = data["token"] ?: ""
                val appId = data["appId"] ?: "0"
                val appVerType = data["appVerType"]?.toIntOrNull() ?: 0
                val miniStartOptions = MiniStartOptions()
                miniStartOptions.resultReceiver = receiver
                miniStartOptions.params = "token=${Uri.encode(token)}" //传递参数
                aware.runOnUiThread {
                    TmfMiniSDK.startMiniApp(
                        aware,
                        appId,
                        MiniScene.LAUNCH_SCENE_SEARCH,
                        appVerType,
                        miniStartOptions
                    )
                }

            }

            Code.TMFLINK -> {
                val data = message.data
                val token = data["token"] ?: ""
                val link = data["link"] ?: ""
                val miniStartOptions = MiniStartLinkOptions()
                miniStartOptions.resultReceiver = receiver
                miniStartOptions.params = "token=${Uri.encode(token)}" //传递参数
                aware.runOnUiThread {
                    TmfMiniSDK.startMiniAppByLink(
                        aware,
                        link,
                        miniStartOptions
                    )
                }

            }
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivityAware = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        mActivityAware = null
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        TmfHostApi.setUp(binding.binaryMessenger, this)
        flutterApi = TmfFlutterApi(binding.binaryMessenger)
        privateChannel = MethodChannel(binding.binaryMessenger, TMF_CHANNEL)
        binding.applicationContext.registerReceiver(
            communicationBroadcast,
            IntentFilter("com.uooc.tmf_channel")
        )
    }

    private val communicationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            val broadcast = this
            if (intent.action == "com.uooc.tmf_channel") {
                val channel = privateChannel
                val aware = mActivityAware
                if (channel == null || aware == null) return
                val value = intent.getStringExtra("key")
                val currentResult = goAsync() // 使用此方法让广播接收器保持活动状态
                coroutineScope.launch {
                    when (value) {
                        "getToken" -> {
                            async {
                                val token = """
                                {"token":"${getTokenFromFlutter()}"}
                            """.trimIndent()
                                withContext(Dispatchers.Main){
                                    currentResult.resultData = token
                                    currentResult.finish()
                                }
                            }.await()
                        }
                        else -> {
                            currentResult.finish()
                        }
                    }
                }
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        TmfHostApi.setUp(binding.binaryMessenger, null)
        flutterApi = null
        binding.applicationContext.unregisterReceiver(communicationBroadcast)
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        onDetachedFromActivityForConfigChanges()
    }
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private var privateChannel: MethodChannel? = null
    private suspend fun getTokenFromFlutter(): String? {
        val channel = privateChannel ?: return null
        return withContext(Dispatchers.IO){
            val completer = CompletableDeferred<String?>()
            withContext(Dispatchers.Main){
                channel.invokeMethod("getToken", null, object : MethodChannel.Result {
                    override fun success(result: Any?) {
                        completer.complete(result as? String)
                    }

                    override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                        Log.wtf("processJsEvent", "getToken errorMessage:$errorMessage")
                        completer.completeExceptionally(Exception(errorMessage))
                    }

                    override fun notImplemented() {
                        Log.wtf("processJsEvent", "getToken notImplemented")
                        completer.completeExceptionally(NotImplementedError("getToken not implemented"))
                    }
                })
            }
            completer.await()
        }
    }
}
