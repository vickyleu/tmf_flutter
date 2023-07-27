package com.uoocuniversity.tmf_flutter.proxyimpl

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.launcher.core.IMiniAppContext
import com.tencent.tmfmini.sdk.launcher.core.proxy.AsyncResult
import com.tencent.tmfmini.sdk.launcher.core.proxy.BaseMiniAppProxyImpl
import com.tencent.tmfmini.sdk.launcher.core.proxy.MiniAppProxy
import com.tencent.tmfmini.sdk.launcher.ui.MoreItem
import com.tencent.tmfmini.sdk.launcher.ui.MoreItemList
import com.tencent.tmfmini.sdk.launcher.ui.OnMoreItemSelectedListener
import com.uoocuniversity.tmf_flutter.CommonApp.Companion.get
import com.uoocuniversity.tmf_flutter.R
import com.uoocuniversity.tmf_flutter.TmfAsyncDrawable
import com.uoocuniversity.tmf_flutter.src.impl.CommonSp
import org.json.JSONException
import org.json.JSONObject

@ProxyService(proxy = MiniAppProxy::class)
class MiniAppProxyImpl : BaseMiniAppProxyImpl() {
    //    /**
    //     * 自定义小程序检查更新loading页面
    //     * @param context
    //     * @return
    //     */
    //    @Override
    //    public IMiniLoading updateLoadingView(Context context) {
    ////        return new IMiniLoading() {
    ////            @Override
    ////            public View create() {
    ////                return LayoutInflater.from(context).inflate(R.layout.applet_activity_custom_update_loading, null);
    ////            }
    ////
    ////            @Override
    ////            public void show(View v) {
    ////
    ////            }
    ////
    ////            @Override
    ////            public void stop(View v) {
    ////
    ////            }
    ////        };
    //
    //        return null;
    //    }
    //    /**
    //     * 自定义小程序加载loading页面
    //     * 调用环境：主进程
    //     *
    //     * @param context 小程序上下文
    //     * @param app 小程序信息
    //     * @return 返回小程序loading UI
    //     */
    //    @Override
    //    public IMiniLoading startLoadingView(Context context, MiniAppLoading app) {
    ////        return new IMiniLoading() {
    ////            @Override
    ////            public View create() {
    ////                return LayoutInflater.from(context).inflate(R.layout.applet_activity_custom_start_loading, null);
    ////            }
    ////
    ////            @Override
    ////            public void show(View v) {
    ////
    ////            }
    ////
    ////            @Override
    ////            public void stop(View v) {
    ////
    ////            }
    ////        };
    //
    //        return null;
    //    }
    /**
     * 用户账号,可选配置，设置后数据会按账号隔离存储小程序数据
     * 调用环境：主进程
     */
    override fun getAccount(): String {
        return CommonSp.instance.getUserName(get().application)?:""
    }

    /**
     * 返回账号登录Cookie信息;superCode(key不能修改)对应账号具备查看开发或预览版小程序的权限，才能扫码打开；否则，扫码失败
     * 调用环境：主进程
     * key:superCode不能修改
     * value:开发者自己生成后设置
     *
     * @return
     */
    override fun getCookie(): Map<String, String>? {
//        Map<String, String> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("superCode", "0ee465cc-dd4d-464e-b52e-64885472cbf9");
//        return objectObjectHashMap;
        return null
    }
    //    /**
    //     * 自定义授权弹窗view
    //     * 调用环境：子进程
    //     *
    //     * @param context
    //     * @param authInfo
    //     * @param authView
    //     * @return true:自定义授权view;false:使用内置
    //     */
    //    @Override
    //    public boolean authView(Context context, MiniAuthInfo authInfo, IAuthView authView) {
    //        boolean isCustom = false;
    //        if (isCustom) {
    //            View inflate = LayoutInflater.from(context).inflate(R.layout.mini_auth_view, null);
    //            //必须设置
    //            inflate.findViewById(R.id.mini_auth_btn_refuse).setOnClickListener(authInfo.refuseListener);
    //            //必须设置
    //            inflate.findViewById(R.id.mini_auth_btn_grant).setOnClickListener(authInfo.grantListener);
    //
    //            //返回自定义View
    //            authView.getView(inflate);
    //        }
    //
    //        return isCustom;
    //    }
    /**
     * 获取scope.userInfo授权用户信息
     * 调用环境：子进程
     *
     * @param appId
     * @param result
     */
    override fun getUserInfo(appId: String, result: AsyncResult) {
        val jsonObject = JSONObject()
        Log.wtf("WxApiPlugin", "getUserInfo:::getUserInfo")
        try {
            //返回昵称
            jsonObject.put("nickName", "userInfo测试")
            //返回头像url
            jsonObject.put(
                "avatarUrl",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.daimg.com%2Fuploads%2Fallimg%2F210114%2F1-210114151951.jpg&refer=http%3A%2F%2Fimg.daimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1673852149&t=e2a830d9fabd7e0818059d92c3883017"
            )
            result.onReceiveResult(true, jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * 接入方APP的版本信息
     */
    override fun getAppVersion(): String {
        return "8.2.0"
    }

    /**
     * 接入方APP的名称
     */
    override fun getAppName(): String {
        return "miniApp"
    }

    /**
     * 接入方APP是否是debug版本
     */
    override fun isDebugVersion(): Boolean {
        return true
    }

    /**
     * 获取Drawable对象
     *
     * @param context         上下文
     * @param source          来源，可以是本地或者网络
     * @param width           图片宽度
     * @param hight           图片高度
     * @param defaultDrawable 默认图片,用于加载中和加载失败
     */
    override fun getDrawable(
        context: Context,
        source: String,
        width: Int,
        hight: Int,
        defaultDrawable: Drawable
    ): Drawable {
        //接入方接入自己的ImageLoader
        //demo里使用开源的universalimageloader
        val drawable = TmfAsyncDrawable()
        if (TextUtils.isEmpty(source)) {
            return drawable
        }
        drawable.loadImage(context, source)
        return drawable
    }

    override fun getDrawable(
        context: Context, source: String, width: Int, hight: Int, defaultDrawable: Drawable,
        useApng: Boolean
    ): Drawable {
        return getDrawable(context, source, width, hight, defaultDrawable)
    }

    /**
     * 获取TypeFace对象
     *
     * @param context    上下文
     * @param fontFamily 字体名
     * @param isBold     是否加粗
     */
    override fun getTypeFace(context: Context, fontFamily: String, isBold: Boolean): Typeface {
        if (context != null) {
            try {
                val paths = context.assets.list("")
                for (path in paths!!) {
                    if (path.equals("$fontFamily.ttf", ignoreCase = true)) {
                        return Typeface.createFromAsset(context.assets, path)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.getTypeFace(context, fontFamily, isBold)
    }

    /**
     * 打开选图界面
     *
     * @param context        当前Activity
     * @param maxSelectedNum 允许选择的最大数量
     * @param listner        回调接口
     * @return 不支持该接口，请返回false
     */
    override fun openChoosePhotoActivity(
        context: Context,
        maxSelectedNum: Int,
        listner: IChoosePhotoListner
    ): Boolean {
        return false
    }

    /**
     * 打开图片预览界面
     *
     * @param context       当前Activity
     * @param selectedIndex 当前选择的图片索引
     * @param pathList      图片路径列表
     * @return 不支持该接口，请返回false
     */
    override fun openImagePreview(
        context: Context,
        selectedIndex: Int,
        pathList: List<String>
    ): Boolean {
        return false
    }

    /**
     * 点击胶囊按钮的更多选项
     *
     * @param miniAppContext 小程序运行环境
     * @return 不支持该接口，请返回false
     */
    override fun onCapsuleButtonMoreClick(miniAppContext: IMiniAppContext): Boolean {
        return false
    }

    /**
     * 点击胶囊按钮的关闭选项
     *
     * @param miniAppContext         小程序运行环境(小程序进程，非主进程)
     * @param onCloseClickedListener 点击小程序关闭时回调
     * @return 不支持该接口，请返回false
     */
    override fun onCapsuleButtonCloseClick(
        miniAppContext: IMiniAppContext,
        onCloseClickedListener: DialogInterface.OnClickListener
    ): Boolean {
        return false
    }

    /**
     * 返回自定义分享数据Map
     * key:与getMoreItems方法中添加的MoreItem.id一致
     * value:与getMoreItems方法中添加的MoreItem.shareKey一致
     * 调用环境：子进程
     *
     * @return
     */
    override fun getCustomShare(): Map<String, Int> {
        val objects: MutableMap<String, Int> = HashMap()
        objects[SHARE_TWITTER] = ShareProxyImpl.Companion.OTHER_MORE_ITEM_2
        return objects
    }

    /**
     * 返回胶囊更多面板的按钮，扩展按钮的ID需要设置为[100, 200]这个区间中的值，否则，添加无效
     * 调用环境：子进程
     *
     * @param miniAppContext 小程序运行环境(小程序进程，非主进程)
     * @param builder
     * @return
     */
    override fun getMoreItems(
        miniAppContext: IMiniAppContext,
        builder: MoreItemList.Builder
    ): ArrayList<MoreItem> {
//        val item1 = MoreItem()
//        item1.id = ShareProxyImpl.Companion.OTHER_MORE_ITEM_1
//        item1.text = getString(miniAppContext, R.string.applet_mini_proxy_impl_other1)
//        item1.drawable = R.mipmap.mini_demo_about
//        val item2 = MoreItem()
//        item2.id = ShareProxyImpl.Companion.OTHER_MORE_ITEM_2
//        item2.text = getString(miniAppContext, R.string.applet_mini_proxy_impl_other2)
//        item2.shareKey = SHARE_TWITTER //自定义分享的key,必须设置且唯一，与小程序端调用控制设置时会使用到
//        item2.drawable = R.mipmap.mini_demo_about
//        val item3 = MoreItem()
//        item3.id = DemoMoreItemSelectedListener.Companion.CLOSE_MINI_APP
//        item3.text = getString(miniAppContext, R.string.applet_mini_proxy_impl_float_app)
//        item3.drawable = R.mipmap.mini_demo_about
        val item4 = MoreItem()
        item4.id = ShareProxyImpl.Companion.OTHER_MORE_ITEM_INVALID
        item4.text = getString(miniAppContext, R.string.applet_mini_proxy_impl_out_of_effect)
        item4.drawable = R.mipmap.mini_demo_about

        // 自行调整顺序。
        builder
           /* .addMoreItem(item1)
            .addMoreItem(item2)
            .addMoreItem(item3)*/
           /* .addShareQQ("QQ", R.mipmap.mini_demo_channel_qq)
            .addShareQzone(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_Qzone),
                R.mipmap.mini_demo_channel_qzone
            )
            .addShareWxFriends(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_wechat_friend),
                R.mipmap.mini_demo_channel_wx_friend
            )
            .addShareWxMoments(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_wechat_group),
                R.mipmap.mini_demo_channel_wx_moment
            )*/
            .addRestart(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_restart),
                R.mipmap.mini_demo_restart_miniapp
            )
           /* .addAbout(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_about),
                R.mipmap.mini_demo_about
            )
            .addDebug(
                getString(miniAppContext, com.tencent.qqmini.R.string.mini_sdk_more_item_debug),
                R.mipmap.mini_demo_about
            )
            .addMonitor(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_performance),
                R.mipmap.mini_demo_about
            )
            */
            .addComplaint(
                getString(miniAppContext, R.string.applet_mini_proxy_impl_complain_and_report),
                R.mipmap.mini_demo_browser_report
            )
            .addSetting(
                getString(
                    miniAppContext,
                    com.tencent.qqmini.R.string.mini_sdk_more_item_setting
                ),
                R.mipmap.mini_demo_setting
            )
        return builder.build()
    }

    private fun getString(miniAppContext: IMiniAppContext, id: Int): String {
        return miniAppContext.context.getString(id)
    }

    /**
     * 返回胶囊更多面板按钮点击监听器
     *
     * @return
     */
    override fun getMoreItemSelectedListener(): OnMoreItemSelectedListener {
        return DemoMoreItemSelectedListener()
    }

    override fun uploadUserLog(appId: String, logPath: String): Boolean {
        Log.d("TMF_MINI", "uploadUserLog $appId logPath $logPath")
        return false
    }

    override fun getLiveComponentLicenseUrl(): String {
        //仅可用于demo
        return "https://license.vod2.myqcloud.com/license/v2/1314481471_1/v_cube.license"
    }

    override fun getLiveComponentLicenseKey(): String {
        //仅可用于demo
        return "6ae463dfe484853eef22052ca122623b"
    } //    @Override

    //    public Locale getLocale() {
    //        return Locale.US;
    //    }
    companion object {
        private const val SHARE_TWITTER = "twitter"
    }
}