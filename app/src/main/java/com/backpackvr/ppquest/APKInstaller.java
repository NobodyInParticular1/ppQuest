package com.backpackvr.ppquest;
import android.Manifest;
import android.app.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class APKInstaller {

    class AppInfo {
        ApplicationInfo info;
        String packageName;
        String displayName;
        Boolean oculus;
        Boolean head;
        Boolean hands;
        Boolean quest2;
    }

    public void getSettingsPermissions(Activity activity) {
        if (!Settings.System.canWrite(activity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
            activity.startActivity(intent);
        }
    }
    public void setBrightness(Activity activity, int brightness){
        getSettingsPermissions(activity);
        if(brightness < 0)
            brightness = 0;
        else if(brightness > 255)
            brightness = 255;

        ContentResolver cResolver = activity.getApplicationContext().getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

    }

    public void LaunchApp(String packageId, Activity activity) {
        PackageManager pm = activity.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageId);
        activity.startActivity(launchIntent);
    }
    public void OpenSettings2(Activity activity) {
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        activity.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI));
    }
    public void OpenSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public void requestPermissions(Activity activity) {
        //Install Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {

                activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", activity.getPackageName()))), 1234);
            }
        }

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        //Storage Permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
    }
    public void getApks(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        List<AppInfo> installedApps = new LinkedList<>();
        for(ApplicationInfo app : pm.getInstalledApplications(PackageManager.GET_META_DATA)) {
            if((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
                // Skip system app
                continue;
            }

            AppInfo appInfo = new AppInfo();
            appInfo.info = app;
            appInfo.packageName = app.packageName;
            appInfo.displayName = (String) pm.getApplicationLabel(app);

            appInfo.oculus = false;
            appInfo.quest2 = false;
            appInfo.hands = false;
            appInfo.head = false;
            if (app.metaData != null) {
                Bundle bundle = app.metaData;
                for (String key: bundle.keySet())
                {
                    if (key.equals("com.samsung.android.vr.application.mode")) {

                        if (bundle.getString(key).equals("vr_only")) {
                            appInfo.oculus = true;
                        }
                    }
                    if (key.equals("com.oculus.supportedDevices")) {
                        if (bundle.getString(key).equals("quest|delmar")) {
                            appInfo.quest2 = true;
                        }
                    }
                }
            } else {
                // metadata is null
            }


            try {
                PackageInfo info = pm.getPackageInfo(app.packageName, PackageManager.GET_CONFIGURATIONS);
                if (info.reqFeatures == null) {
                    // required features is null
                } else {
                    for (FeatureInfo f : info.reqFeatures) {
                        if (f.name != null) {
                            if (f.name.equals("android.hardware.vr.headtracking")) {
                                appInfo.head = true;
                            }
                            if (f.name.equals("oculus.software.handtracking")) {
                                appInfo.hands = true;
                            }
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (appInfo.head && appInfo.oculus) {
                installedApps.add(appInfo);
            }

        }

        for (AppInfo appInfo : installedApps) {
            Log.d ("InstalledAPKS", appInfo.toString());
        }

    }

    public static void installAPK(String apkPath, Activity activity) {
        //Install Permission
        Log.i("installAPK", apkPath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {

                activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", activity.getPackageName()))), 1234);
            }
        }

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        //Storage Permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }

        File f = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri downloadedApk =
                    FileProvider.getUriForFile(
                            activity.getApplicationContext(), activity.getPackageName(), f);
            intent.setDataAndType(downloadedApk, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(f), type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        activity.startActivity(intent);
    }
}
