package com.uoocuniversity.tmf_flutter.jsplugin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.uoocuniversity.tmf_flutter.TmfFlutterPlugin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object PluginChannel {


    suspend fun processJsEvent(context: Context?, method: String, jsonObject: JSONObject) {
        if (context == null) return
        val intent = Intent(TmfFlutterPlugin.TMF_CHANNEL)
        intent.putExtra("key", "$method")
        val completer = CompletableDeferred<JSONObject>()
        withContext(Dispatchers.IO) {
            context.sendOrderedBroadcast(intent, null, object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val broadcast = this
                    val result = broadcast.resultData // 获取最终结果
                    // 处理最终结果
                    when (method) {
                        "getToken" -> {
                            Log.wtf("getToken 的结果呢", "$result")
                            try {
                                val json = JSONObject(result)
                                for (key in json.keys()) {
                                    jsonObject.put(key, json.get(key))
                                }
                            } catch (ignore: Exception) {
                            }
                            completer.complete(jsonObject)
                        }
                    }
                }
            }, null, Activity.RESULT_OK, null, null)
        }
        completer.await()
    }
}
