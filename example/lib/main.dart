import 'package:flutter/material.dart';
import 'package:sfa_flutter_plugins/sfa_flutter_plugins.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: <Widget>[
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  String platformVersion = await SfaFlutterPlugins.getImage(
                      SfaFlutterPlugins.CAMERA_REQUEST);
                  print(platformVersion);
                },
                child: Text(
                  'Camera',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  String platformVersion = await SfaFlutterPlugins.getImage(
                      SfaFlutterPlugins.SELECT_PICTURE);
                  print(platformVersion);
                },
                child: Text(
                  'Gallery',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  double platformVersion =
                      await SfaFlutterPlugins.getDifferenceBetweenPoints(
                          26.855786239809586,
                          75.77136933803558,
                          26.85570488039463,
                          75.77222764492035);
                  print(platformVersion);
                },
                child: Text(
                  'Difference',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  await SfaFlutterPlugins.requestAllPermission();
                  print('done call');
                },
                child: Text(
                  'Request',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  Position pos = await SfaFlutterPlugins.getCurrentLocation;
                  if (pos != null) {
                    print('location ${pos.latitude} ${pos.longitude}');
                  }
                },
                child: Text(
                  'Location',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  await SfaFlutterPlugins.downLoadPdf(
                      'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
                      'Sfa',
                      'downloading',
                      'INVOICE');
                  print('done call');
                },
                child: Text(
                  'Download',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  SfaFlutterPlugins.shareIntent(
                      title: 'title', subject: 'subject', message: 'message',emailList: ['abcd','qwer','rtfd']);
                },
                child: Text(
                  'Share Chooser',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(20),
              child: RaisedButton(
                onPressed: () async {
                  SfaFlutterPlugins.shareOnGmail(
                      title: 'title', subject: 'subject', message: 'message',emailList: ['abcd','qwer','rtfd']);
                },
                child: Text(
                  'Share On Gmail',
                  style: TextStyle(
                    fontSize: 30,
                    color: Colors.purple,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
