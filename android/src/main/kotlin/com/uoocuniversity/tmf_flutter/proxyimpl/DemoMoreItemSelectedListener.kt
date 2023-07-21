package com.uoocuniversity.tmf_flutter.proxyimpl

import android.widget.Toast
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext
import com.tencent.tmfmini.sdk.launcher.log.QMLog
import com.tencent.tmfmini.sdk.ui.DefaultMoreItemSelectedListener

class DemoMoreItemSelectedListener : DefaultMoreItemSelectedListener() {
    override fun onMoreItemSelected(miniAppContext: IMiniAppContext, moreItemId: Int) {
        //处理开发者自定义点击事件(自定义分享事件除外)
        when (moreItemId) {
            CLOSE_MINI_APP -> {
                close(miniAppContext)
                return
            }

            ShareProxyImpl.Companion.OTHER_MORE_ITEM_1 -> {
                miniAppContext.attachedActivity.runOnUiThread {
                    Toast.makeText(
                        miniAppContext.attachedActivity,
                        "custom menu click",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }

        //处理内置分享和开发者自定义分享，例如：微博、twitter等
        super.onMoreItemSelected(miniAppContext, moreItemId)
    }

    fun close(miniAppContext: IMiniAppContext) {
        val activity = miniAppContext.attachedActivity
        if (activity != null && !activity.isFinishing) {
            val moved = activity.moveTaskToBack(true)
            if (!moved) {
                QMLog.e("Demo", "moveTaskToBack failed, finish the activity.")
                activity.finish()
            }
        }
    }

    companion object {
        const val CLOSE_MINI_APP = 150
    }
}