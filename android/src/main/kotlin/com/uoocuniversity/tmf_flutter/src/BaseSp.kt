package com.uoocuniversity.tmf_flutter.src

import android.content.SharedPreferences
import android.text.TextUtils

/**
 * SharedPreferences基类
 *
 * @author robincxiao 2019/9/3 11:11
 */
open class BaseSp {
    protected var mSharedPreferences: SharedPreferences? = null
    protected var mEditor: SharedPreferences.Editor? = null
    protected fun putString(editor: SharedPreferences.Editor?, key: String?, value: String?) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return
        }
        editor.putString(key, value).commit()
    }

    protected fun putMap(editor: SharedPreferences.Editor?, map: Map<String?, Any?>?) {
        if (editor == null || map == null || map.size <= 0) {
            return
        }
        val iterator = map.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = map[key]
            if (value is String) {
                editor.putString(key, value as String?)
            } else if (value is Long) {
                editor.putLong(key, (value as Long?)!!)
            } else if (value is Int) {
                editor.putInt(key, (value as Int?)!!)
            } else if (value is Boolean) {
                editor.putBoolean(key, (value as Boolean?)!!)
            } else if (value is Float) {
                editor.putFloat(key, (value as Float?)!!)
            }
        }
        editor.commit()
    }

    protected fun getString(sp: SharedPreferences?, key: String?, def: String?): String? {
        return if (sp == null || TextUtils.isEmpty(key)) {
            ""
        } else sp.getString(key, def)
    }

    protected fun putLong(editor: SharedPreferences.Editor?, key: String?, value: Long) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return
        }
        editor.putLong(key, value).commit()
    }

    fun getLong(sp: SharedPreferences?, key: String?, def: Long): Long {
        return if (sp == null || TextUtils.isEmpty(key)) {
            def
        } else sp.getLong(key, def)
    }

    protected fun contains(sp: SharedPreferences?, key: String?): Boolean {
        return if (sp == null || TextUtils.isEmpty(key)) {
            false
        } else sp.contains(key)
    }

    protected fun putInt(editor: SharedPreferences.Editor?, key: String?, value: Int) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return
        }
        editor.putInt(key, value).commit()
    }

    protected fun getInt(sp: SharedPreferences?, key: String?, def: Int): Int {
        return if (sp == null || TextUtils.isEmpty(key)) {
            def
        } else sp.getInt(key, def)
    }

    protected fun putBoolean(editor: SharedPreferences.Editor?, key: String?, value: Boolean) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return
        }
        editor.putBoolean(key, value).commit()
    }

    protected fun getBoolean(sp: SharedPreferences?, key: String?, def: Boolean): Boolean {
        return if (sp == null || TextUtils.isEmpty(key)) {
            def
        } else sp.getBoolean(key, def)
    }

    protected fun remove(editor: SharedPreferences.Editor?, key: String?) {
        if (editor == null) {
            return
        }
        editor.remove(key).commit()
    }

    protected fun clear(editor: SharedPreferences.Editor?) {
        if (editor == null) {
            return
        }
        editor.clear().commit()
    }
}