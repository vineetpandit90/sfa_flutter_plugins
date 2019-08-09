package com.girnarsfa.sfa_flutter_plugins;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.app.Activity.RESULT_OK;
import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;

/**
 * SfaFlutterPluginsPlugin
 */
public class SfaFlutterPluginsPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {
    public static final int SELECT_PICTURE = 1111;
    public static final int CAMERA_REQUEST = 2222;
    public static final int GPS_SETTING = 3333;
    Activity activity;
    MethodChannel methodChannel;
    Result result;
    GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;
    private boolean isReplied = false;

    SfaFlutterPluginsPlugin(Activity activity, MethodChannel methodChannel) {
        this.activity = activity;
        this.methodChannel = methodChannel;
        methodChannel.setMethodCallHandler(this);
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "sfa_flutter_plugins");
        SfaFlutterPluginsPlugin girnarSfaPluginsPlugin = new SfaFlutterPluginsPlugin(registrar.activity(), channel);
        channel.setMethodCallHandler(girnarSfaPluginsPlugin);
        registrar.addActivityResultListener(girnarSfaPluginsPlugin);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        this.result = result;
        if (call.method.equals("requestAllPermission")) {


            Dexter.withActivity(activity)
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).onSameThread().check();
        } else if (call.method.equals("getCurrentLocation")) {
            isReplied=false;
            requestLocation();
        } else if (call.method.equals("downLoadFileFromUrl")) {
            HashMap<String, String> map = (HashMap<String, String>) call.arguments;

            requestDownLoadFile(map.get("urlPath"), map.get("title"), map.get("description"), map.get("folderName"));
        } else if (call.method.equals("shareMessage")) {
            shareCreateChooser(call.arguments);
        } else if (call.method.equals("shareOnGmail")) {
            shareOnGmail(call.arguments);
        } else if (call.method.equals("getImage")) {
            requestGalleryCamera((int) call.arguments);
        } else if (call.method.equals("getDifference")) {
            HashMap<String, Double> map = (HashMap<String, Double>) call.arguments;
            double lat1 = map.get("latitude1");
            double lng1 = map.get("longitude1");
            double lat2 = map.get("latitude2");
            double lng2 = map.get("longitude2");

            result.success(getDifference(lat1, lng1, lat2, lng2));
        } else {
            result.notImplemented();
        }
    }

    private double getDifference(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(
                deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;//miles
        // dist = dist / 0.621371192;//kilometer
        dist = dist * 1609.344;//meter
        return dist;
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private void showAlertForPermission() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setCancelable(false);
//        builder.setMessage("Please grant required permissions in Application Settings under Permissions")
//                .setCancelable(false)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.setTitle("Permission Required");
//        alert.show();
    }

    public void requestLocation() {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            googleApiClient = new GoogleApiClient.Builder(activity)
                                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                        @Override
                                        public void onConnected(@Nullable Bundle bundle) {

                                            getMyLocation();
                                        }

                                        @Override
                                        public void onConnectionSuspended(int i) {

                                        }
                                    })
                                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                        @Override
                                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                                        }
                                    })
                                    .addApi(LocationServices.API)
                                    .build();
                            googleApiClient.connect();


                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showAlertForPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                })
                .
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {

                            }
                        })
                .onSameThread()
                .check();
    }

    protected void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {

                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(2000);
                locationRequest.setFastestInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        if (location != null) {
                            if (location.getAccuracy() < 40 && result != null && !isReplied) {
                                HashMap<String, Double> locationMap = new HashMap<>();
                                locationMap.put("latitude", location.getLatitude());
                                locationMap.put("longitude", location.getLongitude());
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                                try {
                                    isReplied = true;
                                    result.success(locationMap);
                                    result = null;
                                    googleApiClient.disconnect();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    isReplied = true;
                                    result.notImplemented();
                                    result = null;
                                }

                            }
                        } else {

                        }
                    }
                });
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                //Gps is on. We can call for latest location.

                                progressDialog = new ProgressDialog(activity, R.style.MyTheme);
                                progressDialog.setCancelable(true);
                                progressDialog.show();


                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                //Gps is off. Automatically check and show popup to turn on Gps.
                                try {
                                    status.startResolutionForResult(activity, GPS_SETTING);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                //Gps is off. Can not show popup to turn on Gps.
                                break;
                        }
                    }
                });

            }
        }
    }

    public void requestGalleryCamera(final int code) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (code == CAMERA_REQUEST) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            } else if (code == SELECT_PICTURE) {
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                activity.startActivityForResult(i, SELECT_PICTURE);
                            }
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showAlertForPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {

                            }
                        })
                .onSameThread()
                .check();
    }

    public void requestDownLoadFile(final String path, final String title, final String description, final String folderName) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri Download_Uri = Uri.parse(path);
                            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setAllowedOverRoaming(false);
                            request.setTitle(title + "-" + folderName + System.currentTimeMillis());
                            request.setDescription(description);
                            request.setVisibleInDownloadsUi(true);
                            request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + "-" + folderName + System.currentTimeMillis() + ".pdf");
                            downloadManager.enqueue(request);


                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showAlertForPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {

                            }
                        })
                .onSameThread()
                .check();
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Video.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            String picturePath = "";
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                picturePath = selectedImage.toString();
            }

            result.success(picturePath);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {

            Bitmap finalBitmap = (Bitmap) data.getExtras().get("data");
            try {
                finalBitmap = Bitmap.createScaledBitmap(finalBitmap, 100, 100, true);
                final ByteArrayOutputStream bao = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bao);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Uri tempUri = getImageUri(finalBitmap);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            String imagePath = finalFile.getAbsolutePath();
            result.success(imagePath);
        } else if (requestCode == GPS_SETTING) {
            if (resultCode == RESULT_OK) {
                getMyLocation();
            } else {
                result.notImplemented();
            }

        }
        return false;
    }

    private void shareCreateChooser(Object arguments) {
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String subject = argsMap.get("subject");

        String message = argsMap.get("message");
        String emailarray = argsMap.get("emailArray");
        String email = argsMap.get("email");

        String str = "";
        if (emailarray != null && emailarray.trim().length() > 0) {
            str = emailarray;
        } else if (email != null && email.trim().length() > 0) {
            str = email;
        } else {
            str = "";
        }


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{str});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        activity.startActivity(Intent.createChooser(intent, title));
    }

    private void shareOnGmail(Object arguments) {
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String subject = argsMap.get("subject");

        String message = argsMap.get("message");
        String emailarray = argsMap.get("emailArray");
        String email = argsMap.get("email");

        String str = "";
        if (emailarray != null && emailarray.trim().length() > 0) {
            str = emailarray;
        } else if (email != null && email.trim().length() > 0) {
            str = email;
        } else {
            str = "";
        }


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{str});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        activity.startActivity(Intent.createChooser(intent, title));


        final PackageManager pm = activity.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                best = info;
            }
        }
        if (best != null) {
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        }
        activity.startActivity(intent);
    }

}
