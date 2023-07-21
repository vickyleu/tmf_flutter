package com.uoocuniversity.tmf

import com.uoocuniversity.tmf_flutter.TmfFlutterPlugin
import io.flutter.app.FlutterApplication

class TMFApplication: FlutterApplication() {
    override fun onCreate() {
        super.onCreate()
//        TmfFlutterPlugin.create(this)
        TmfFlutterPlugin.create(this)


    }
}