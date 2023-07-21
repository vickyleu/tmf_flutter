package com.uoocuniversity.tmf_flutter.src.impl

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.uoocuniversity.tmf_flutter.CommonApp.Companion.get
import com.uoocuniversity.tmf_flutter.src.BaseSp
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @author robincxiao 2019/9/3 11:11
 */
class CommonSp private constructor() : BaseSp() {
    init {
        val context: Context? = get().application
        mSharedPreferences = context!!.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences!!.edit()
    }

    val configFilePath: String?
        get() = getString(mSharedPreferences, KEY_CONFIG_FILE_PATH, "")

    fun putConfigFilePath(path: String?) {
        putString(mEditor, KEY_CONFIG_FILE_PATH, path)
    }

    fun removeConfigFilePath() {
        remove(mEditor, KEY_CONFIG_FILE_PATH)
    }

    fun getUserName(context: Context?): String? {
        return getString(
            context!!.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE),
            KEY_USER_NAME,
            ""
        )
    }

    fun putUserName(context: Context, name: String?) {
        putString(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit(),
            KEY_USER_NAME,
            name
        )
    }

    fun removeUserName() {
        remove(mEditor, KEY_USER_NAME)
    }

    fun putSkipLogin(isSkipLogin: Boolean) {
        putBoolean(mEditor, KEY_SKIP_LOGIN, isSkipLogin)
    }

    val isSkipLogin: Boolean
        get() = getBoolean(mSharedPreferences, KEY_SKIP_LOGIN, false)

    fun removeSkipLogin() {
        remove(mEditor, KEY_SKIP_LOGIN)
    }

    @Synchronized
    fun putUser(name: String, pwd: String) {
        val string = getString(mSharedPreferences, KEY_USER, "")
        try {
            var jsonArray = JSONArray()
            if (!TextUtils.isEmpty(string)) {
                jsonArray = JSONArray(string)
            }
            val length = jsonArray.length()
            var isFind = false
            for (i in 0 until length) {
                val jsonObject = jsonArray.optJSONObject(i)
                val name1 = jsonObject.optString("name")
                val pwd1 = jsonObject.optString("pwd")
                if (name1.equals(name, ignoreCase = true) && pwd1.equals(pwd, ignoreCase = true)) {
                    isFind = true
                    break
                }
            }
            if (!isFind) {
                val jsonObject = JSONObject()
                jsonObject.put("name", name.trim { it <= ' ' })
                jsonObject.put("pwd", pwd.trim { it <= ' ' })
                jsonArray.put(jsonObject)
            }
            putString(mEditor, KEY_USER, jsonArray.toString())
            val jsonObject = JSONObject()
            jsonObject.put("name", name.trim { it <= ' ' })
            jsonObject.put("pwd", pwd.trim { it <= ' ' })
            putString(mEditor, KEY_LAST_USER, jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @get:Synchronized
    val users: List<String>
        get() {
            val string = getString(mSharedPreferences, KEY_USER, "")
            val strings: MutableList<String> = ArrayList()
            try {
                val jsonArray = JSONArray(string)
                val length = jsonArray.length()
                for (i in 0 until length) {
                    val jsonObject = jsonArray.optJSONObject(i)
                    val name = jsonObject.optString("name")
                    val pwd = jsonObject.optString("pwd")
                    strings.add(name)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return strings
        }

    @Synchronized
    fun getPwd(name: String): String {
        val string = getString(mSharedPreferences, KEY_USER, "")
        try {
            val jsonArray = JSONArray(string)
            val length = jsonArray.length()
            for (i in 0 until length) {
                val jsonObject = jsonArray.optJSONObject(i)
                val n = jsonObject.optString("name")
                if (name.equals(n, ignoreCase = true)) {
                    return jsonObject.optString("pwd")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    @get:Synchronized
    val lastUser: JSONObject?
        get() {
            val string = getString(mSharedPreferences, KEY_LAST_USER, "")
            if (TextUtils.isEmpty(string)) {
                return null
            }
            try {
                return JSONObject(string)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }

    /**
     * 清除整个文件数据
     */
    fun clearAll() {
        clear(mEditor)
    }

    fun isPrivacyAuth(context: Context): Boolean {
        return getBoolean(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE),
            KEY_IS_PRIVACY,
            false
        )
    }

    fun putPrivacyAuth(context: Context, value: Boolean) {
        putBoolean(
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit(),
            KEY_IS_PRIVACY,
            value
        )
    }

    class User {
        @SerializedName("name")
        var name: String? = null

        @SerializedName("pwd")
        var pwd: String? = null
    }

    companion object {
        /**
         * SharedPreferences文件名
         */
        const val FILE_NAME = "app_common"

        /**
         * 配置文件路径
         */
        private const val KEY_CONFIG_FILE_PATH = "config_file_path"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_SKIP_LOGIN = "skip_login"
        private const val KEY_OPERATE_USER = "operate_user"
        private const val KEY_USER = "user"
        private const val KEY_LAST_USER = "last_user"
        private const val KEY_IS_PRIVACY = "is_privacy_auth"

        @Volatile
        private var mInstatnce: CommonSp? = null

        @get:Synchronized
        val instance: CommonSp
            get() {
                if (mInstatnce == null) {
                    synchronized(CommonSp::class.java) {
                        if (mInstatnce == null) {
                            mInstatnce = CommonSp()
                        }
                    }
                }
                return mInstatnce!!
            }
    }
}