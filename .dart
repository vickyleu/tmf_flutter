import 'package:pigeon/pigeon.dart';
@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/src/messages.g.dart',
  dartOptions: DartOptions(),
  kotlinOut:
  'android/src/main/kotlin/com/uoocuniversity/tmf_flutter/src/Messages.g.kt',
  kotlinOptions: KotlinOptions(
    package: 'com.uoocuniversity.tmf_flutter.src',
  ),
  objcHeaderOut:'ios/Classes/src/Messages.g.h',
  objcSourceOut:'ios/Classes/src/Messages.g.m',
  // swiftOut: 'ios/Classes/src/Messages.g.swift',
  swiftOptions: SwiftOptions(),
  // Set this to a unique prefix for your plugin or application, per Objective-C naming conventions.
  objcOptions: ObjcOptions(prefix: 'PGN'),
  copyrightHeader: 'pigeons/copyright_header.txt',
))

enum Code { TmfLink, TmfId }

enum TMAVersion {
  Develop, // 开发版本
  Preview, // 预览版本
  Audit, // 审核版本
  Online, // 线上版本（全量发布）
  Local; //本地预置版本，不会进行更新}
}

class MessageData {
  MessageData({required this.code, required this.data});
  final Code code;
  final Map<String?, String?> data;
}

/// Native call Flutter
@FlutterApi(package: 'com.uoocuniversity.tmf_flutter')
abstract class TmfFlutterApi {
  @async
  void logout();
}

@HostApi(package: 'com.uoocuniversity.tmf_flutter')
abstract class TmfHostApi {
  @async
  void initTmf();
  @async
  bool loginTmf(String account,String password,bool isOpenLogin);
  @async
  void destroy();
  @async
  bool sendMessage(MessageData message);
}