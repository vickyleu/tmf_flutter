import 'package:flutter/material.dart';
import 'package:tmf_flutter/tmf_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}



class _MyAppState extends State<MyApp> {
  final _tmfFlutterPlugin = TmfFlutter();

  @override
  void initState() {
    super.initState();
    _tmfFlutterPlugin.initTmf().then((value) async {
      //tmf://applet/?appId=tmf50bbymfr06vcixz&type=1&businessId=6991&timestamp=1689323164448
      _tmfFlutterPlugin
          .loginTmf(account: "shendayouke", password: "sdyk@TMF123")
          .then((value) {
        print("登录结果:${value}");
        if (value) {
          _tmfFlutterPlugin
              .openAppletId("tmf50bbymfr06vcixz", TMAVersion.Develop, {"token": "123"});
          // _tmfFlutterPlugin.openAppletLink("tmf://applet/?appId=tmf50bbymfr06vcixz&type=1&businessId=6991&timestamp=1689323164448",{"token":"123"});
        }
      }).catchError((onError){
        print("错误原因:${onError}");
      });
      // item.appId=tmf50bbymfr06vcixz
      // item.appVerType=1
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: \n'),
        ),
      ),
    );
  }
}
