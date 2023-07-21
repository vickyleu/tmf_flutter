package com.uoocuniversity.tmf_flutter.proxyimpl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.AppLoaderFactory
import com.tencent.tmfmini.sdk.launcher.core.proxy.BaseShareProxy
import com.tencent.tmfmini.sdk.launcher.core.proxy.ShareProxy
import com.tencent.tmfmini.sdk.launcher.model.ShareData
import com.tencent.tmfmini.sdk.launcher.ui.MoreItem

@ProxyService(proxy = ShareProxy::class)
class ShareProxyImpl : BaseShareProxy() {
    private var mQQShareUiListener: IUiListener? = null

    /**
     * 分享
     *
     * @param shareData 分享数据
     */
    override fun share(activity: Activity, shareData: ShareData) {
        when (shareData.shareTarget) {
            ShareData.ShareTarget.QQ -> {
                Toast.makeText(activity, "QQ", Toast.LENGTH_SHORT).show()
                shareToQQ(activity, shareData)
                return
            }

            ShareData.ShareTarget.QZONE -> {
                Toast.makeText(activity, "QZONE", Toast.LENGTH_SHORT).show()
                shareToQZone(activity, shareData)
                return
            }

            ShareData.ShareTarget.WECHAT_FRIEND -> {
                Toast.makeText(activity, "WECHAT_FRIEND", Toast.LENGTH_SHORT).show()
                return
            }

            ShareData.ShareTarget.WECHAT_MOMENTS -> {
                Toast.makeText(activity, "WECHAT_MOMENTS", Toast.LENGTH_SHORT).show()
                return
            }

            else -> {}
        }
        if (MoreItem.isValidExtendedItemId(shareData.shareTarget)) {
            shareToOther(activity, shareData)
        }
    }

    /**
     * 启动第三方分享结果的返回
     */
    override fun onShareActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mQQShareUiListener)
    }

    /**
     * 调用互联SDK，分享到QQ
     */
    fun shareToQQ(activity: Activity?, shareData: ShareData) {
        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.title)
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.summary)
        params.putString(
            QQShare.SHARE_TO_QQ_TARGET_URL,
            if (TextUtils.isEmpty(shareData.targetUrl)) "https://www.qq.com" else shareData.targetUrl
        )
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareData.sharePicPath)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "MiniSDKDemo")
        mQQShareUiListener = QQShareListener(activity, shareData)
        tencent!!.shareToQQ(activity, params, null)
    }

    /**
     * 调用互联SDK，分享到QQ空间
     */
    fun shareToQZone(activity: Activity?, shareData: ShareData) {
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareData.title) //必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareData.summary) //选填
        params.putString(
            QzoneShare.SHARE_TO_QQ_TARGET_URL,
            if (TextUtils.isEmpty(shareData.targetUrl)) "https://www.qq.com" else shareData.targetUrl
        ) //必填
        val imageUrlList: ArrayList<String> = arrayListOf()
        imageUrlList.add(shareData.sharePicPath)
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrlList)
        mQQShareUiListener = QQShareListener(activity, shareData)
        tencent!!.shareToQzone(activity, params, mQQShareUiListener)
    }

    /**
     * 调用第三方分享
     */
    fun shareToOther(activity: Activity?, shareData: ShareData) {
        when (shareData.shareItemId) {
            OTHER_MORE_ITEM_2 -> Toast.makeText(activity, "custom share", Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    private inner class QQShareListener internal constructor(
        private val mContext: Context?,
        private val mShareData: ShareData
    ) : IUiListener {
        override fun onComplete(o: Any) {
//            Toast.makeText(mContext, "share Complete", Toast.LENGTH_SHORT).show();
            mShareData.notifyShareResult(mContext, ShareData.ShareResult.SUCCESS)
        }

        override fun onError(uiError: UiError) {
            Toast.makeText(
                mContext,
                "share Error: " + uiError.errorCode + uiError.errorMessage,
                Toast.LENGTH_SHORT
            ).show()
            mShareData.notifyShareResult(mContext, ShareData.ShareResult.FAIL)
        }

        override fun onCancel() {
//            Toast.makeText(mContext, "share Cancel", Toast.LENGTH_SHORT).show();
            mShareData.notifyShareResult(mContext, ShareData.ShareResult.CANCEL)
        }
    }

    companion object {
        private const val TAG = "ShareProxyImpl"
        private const val QQ_APP_ID = "1108836394"

        // this init is so slow...
        @Volatile
        private var tencent: Tencent? = null
            private get() {
                if (field == null) {
                    synchronized(ShareProxyImpl::class.java) {
                        if (field == null) {
                            field = Tencent
                                .createInstance(QQ_APP_ID, AppLoaderFactory.g().context)
                        }
                    }
                }
                return field
            }

        // 扩展按钮的ID需要设置为[100, 200]这个区间中的值，否则，添加无效。
        const val OTHER_MORE_ITEM_1 = 101
        const val OTHER_MORE_ITEM_2 = 102
        const val OTHER_MORE_ITEM_INVALID = 201
    }
}