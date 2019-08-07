import 'dart:async';

import 'package:flutter/services.dart';

class SfaFlutterPlugins {
  static final int SELECT_PICTURE = 1111;
  static final int CAMERA_REQUEST = 2222;
  static const MethodChannel _channel =
      const MethodChannel('sfa_flutter_plugins');

  static Future<String> getImage(int code) async {
    final String image = await _channel.invokeMethod('getImage', code);
    return image;
  }

  static Future<Map> get getCurrentLocation async {
    final Map map = await _channel.invokeMethod('getCurrentLocation');
    return map;
  }

  static Future<Map> requestAllPermission() async {
    await _channel.invokeMethod('requestAllPermission');
  }

  static Future<double> getDifferenceBetweenPoints(
      double lat1, double lng1, double lat2, double lng2) async {
    Map map = {
      'latitude1': lat1,
      'longitude1': lng1,
      'latitude2': lat2,
      'longitude2': lng2,
    };
    final double distance = await _channel.invokeMethod('getDifference', map);
    return distance;
  }

  static void downLoadPdf(String url, String title, String description,
       String folderName) async {
    Map map = {
      'urlPath': url,
      'title': title,
      'description': description,
      'folderName': folderName,
    };
     await _channel.invokeMethod('downLoadFileFromUrl', map);

  }
}
