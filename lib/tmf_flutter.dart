import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:tmf_flutter/src/messages.g.dart';
export 'package:tmf_flutter/src/messages.g.dart';

typedef MethodChannelImpl = Future<dynamic> Function(MethodCall);


class TmfFlutter extends TmfFlutterApi{
  static const String TMF_CHANNEL = "com.uooc.tmf_channel";

  TmfFlutter(){
    TmfFlutterApi.setup(this);
  }

  final TmfHostApi _api = TmfHostApi();

  Future<void> initTmf() {
    return _api.initTmf();
  }

  registerCallback(MethodChannelImpl impl){
    const channel = MethodChannel(TMF_CHANNEL);
    channel.setMethodCallHandler(impl);
  }

  Future<bool> loginTmf({required String account,required String password,bool isOpenLogin=true}) {
    return _api.loginTmf(account,password,isOpenLogin).catchError((onError){
      print("onError:$onError");
      return false;
    });
  }

  Future<void> closeTmf() {
    return _api.destroy();
  }

  Future<bool> openAppletId(
      String appId, TMAVersion appVerType, Map<String, String> data) {
    data["appId"] = appId;
    //ios
        // TMAVersionDevelop = 0,         // 开发版本
        // TMAVersionPreview = 1,         // 预览版本
        // TMAVersionAudit = 2,           // 审核版本
        // TMAVersionOnline = 3,          // 线上版本（全量发布）
        // TMAVersionLocal = 10,          // 本地预置版本，不会进行更新
    //android
        // TYPE_ONLINE = 0;               // 线上版本（全量发布）
        // TYPE_DEVELOP = 1;              // 开发版本
        // TYPE_PREVIEW = 2;              // 预览版本
        // TYPE_EXPERIENCE = 3;           // 审核版本

    switch(appVerType){
      case TMAVersion.Develop:
        data["appVerType"] = Platform.isAndroid ? "1":"0";
        break;
      case TMAVersion.Preview:
        data["appVerType"] = Platform.isAndroid ? "2":"1";
        break;
      case TMAVersion.Audit:
        data["appVerType"] =  Platform.isAndroid ? "3":"2";
        break;
      case TMAVersion.Online:
        data["appVerType"] =  Platform.isAndroid ? "0":"3";
        break;
      case TMAVersion.Local: //Android没有本地版本,用开发版暂时代替
        data["appVerType"] =  Platform.isAndroid ? "1":"10";
        break;
      default: //默认为开发版
        data["appVerType"] = Platform.isAndroid ? "1":"0";
        break;
    }
    return sendMessage(Code.TmfId, data);
  }

  Future<bool> openAppletLink(String link, Map<String, String> data) {
    data["link"] = link;
    return sendMessage(Code.TmfLink, data);
  }

  Future<bool> sendMessage(Code code, Map<String, String> data) {
    return _api.sendMessage(MessageData(code: code, data: data));
  }

  TmfFlutter setLogoutCallback(VoidCallback func){
    _streamController.stream.listen((event) {
      print("sFlutterApi dart == setLogoutCallback");
      func();
    });
    return this;
  }

  final StreamController<bool> _streamController = StreamController.broadcast();
  @override
  Future<void> logout() async{
    print("sFlutterApi dart == logout");
    _streamController.add(true);
  }
}
