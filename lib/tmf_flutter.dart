import 'package:tmf_flutter/src/messages.g.dart';

export 'package:tmf_flutter/src/messages.g.dart';

class TmfFlutter {
  final TmfHostApi _api = TmfHostApi();

  Future<void> initTmf() {
    return _api.initTmf();
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
    // TMAVersionDevelop = 0,        // 开发版本
    // TMAVersionPreview = 1,        // 预览版本
    // TMAVersionAudit = 2,            // 审核版本
    // TMAVersionOnline = 3,            // 线上版本（全量发布）
    // TMAVersionLocal = 10,          //本地预置版本，不会进行更新
    switch(appVerType){
      case TMAVersion.Develop:
        data["appVerType"] = "0";
        break;
      case TMAVersion.Preview:
        data["appVerType"] = "1";
        break;
      case TMAVersion.Audit:
        data["appVerType"] = "2";
        break;
      case TMAVersion.Online:
        data["appVerType"] = "3";
        break;
      case TMAVersion.Local:
        data["appVerType"] = "10";
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
}
