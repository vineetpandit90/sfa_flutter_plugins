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

  static Future<Position> get getCurrentLocation async {
    try {
      final Map map = await _channel.invokeMethod('getCurrentLocation');
      Position position = new Position();
      position.latitude = map['latitude'];
      position.longitude = map['longitude'];
      return position;
    } catch (e) {
      print(e);
      return null;
    }
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

  static void shareIntent(
      {String title = '',
      String subject = '',
      String email = '',
      List<String> emailList,
      String message = ''}) async {
    Map map = {
      'title': title,
      'subject': subject,
      'email': email != null ? email : '',
      'emailArray': emailList != null ? emailList.toString().replaceAll('[', '').replaceAll(']', '') : '',
      'message': message,
    };
    _channel.invokeMethod('shareMessage', map);
  }

  static void shareOnGmail(
      {String title = '',
      String subject = '',
      String email = '',
      List<String> emailList,
      String message = ''}) async {
    Map map = {
      'title': title,
      'subject': subject,
      'email': email != null ? email : '',
      'emailArray': emailList != null ? emailList.toString().replaceAll('[', '').replaceAll(']', '') : '',
      'message': message,
    };
    _channel.invokeMethod('shareOnGmail', map);
  }

  static void downLoadPdf(
      String url, String title, String description, String folderName) async {
    Map map = {
      'urlPath': url,
      'title': title,
      'description': description,
      'folderName': folderName,
    };
    await _channel.invokeMethod('downLoadFileFromUrl', map);
  }
}

class Position {
  double _latitude = 0;
  double _longitude = 0;

  double get latitude => _latitude;

  set latitude(double value) {
    _latitude = value;
  }

  double get longitude => _longitude;

  set longitude(double value) {
    _longitude = value;
  }
}
