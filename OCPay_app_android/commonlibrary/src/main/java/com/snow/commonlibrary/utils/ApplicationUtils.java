package com.snow.commonlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class ApplicationUtils {

    private static ApplicationUtils instance;
    private final Context appContext;
    private PackageInfo packageInfo = null;
    private String applicationName;
    private String androidId;
    private String deviceId;
    private String channel;
    private String installChannel;

    public ApplicationUtils(Context context) {
        appContext = context.getApplicationContext();
    }

    public static ApplicationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationUtils(context);
            instance.initPackageInfo();
            instance.initApplicationInfo();
            instance.initAndroidId();
//            instance.initDeviceId();
            instance.initChannel();
        }
        return instance;
    }

    private void initPackageInfo() {
        if (packageInfo != null) {
            return;
        }
        try {
            packageInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }

    private void initChannel() {
        if (!TextUtils.isEmpty(channel)) {
            return;
        }
        ApplicationInfo appInfo;
        try {
            appInfo = appContext.getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                channel = appInfo.metaData.getString("channel");
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initAndroidId() {
        if (!TextUtils.isEmpty(androidId)) {
            return;
        }
        try {
            androidId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(androidId)) {
                androidId = getDeviceId();
            }
        } catch (Exception e) {
            Log.e("ApplicationUtils", "androidId:" + e.getMessage());
            e.printStackTrace();
        }
    }

//    private void initDeviceId() {
//        if (!TextUtils.isEmpty(deviceId)) {
//            return;
//        }
//        try {
//            TelephonyManager mTelephonyMgr = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
//            if (mTelephonyMgr != null) {
//                deviceId = mTelephonyMgr.getDeviceId();
//            }
//        } catch (Exception e) {
//            Log.e("ApplicationUtils", "initDeviceId:" + e.getMessage());
//            e.printStackTrace();
//        }
//    }


    private void initApplicationInfo() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo ;
        try {
            packageManager = appContext.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(appContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }

        if (applicationInfo != null) {
            applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        }
    }

    /**
     * 包名
     *
     * @return
     */
    public String getPackageName() {
        initPackageInfo();
        if (packageInfo != null) {
            return packageInfo.packageName;
        }
        return "";
    }

    /**
     * 版本名
     *
     * @return
     */
    public String getVersionName() {
        initPackageInfo();
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    /**
     * 版本号
     *
     * @return
     */
    public int getVersionCode() {
        initPackageInfo();
        if (packageInfo != null) {
            float versionCode = packageInfo.versionCode;
            return (int) versionCode;
        }
        return 0;
    }

    /**
     * 版本号
     *
     * @return
     */
    public String getVersionCodeStr() {
        initPackageInfo();
        if (packageInfo != null) {
            float versionCode = packageInfo.versionCode;
            return versionCode + "";
        }
        return "";
    }

    /**
     * 获取安装时间
     */
    public long getFirstInstallTime() {
        initPackageInfo();
        if (packageInfo != null) {
            return packageInfo.firstInstallTime;
        }
        return 0;
    }


    /**
     * 第一次安装渠道号
     *
     * @return
     */
    public String getInstallChannel() {
        if (TextUtils.isEmpty(installChannel)) {
            SharedPreferences sp = appContext.getSharedPreferences("appinstallinfo", Context.MODE_PRIVATE);
            String ic = sp.getString("installchannel", null);

            if (TextUtils.isEmpty(ic)) {
                ic = getChannel();
                sp.edit().putString("installchannel", ic).apply();
            }

            installChannel = ic;
        }
        return installChannel;
    }

    /**
     * 当前安装包的的渠道号
     *
     * @return
     */
    public String getChannel() {
        initChannel();
        return channel == null ? "" : channel;
    }

    /**
     * 获取AndroidId
     *
     * @return
     */
    public String getAndroidId() {
        initAndroidId();
        return androidId == null ? "" : androidId;
    }

    /**
     * 获取设备Id
     *
     * @return
     */
    private String getDeviceId() {
//        initDeviceId();
        return deviceId == null ? "" : deviceId;
    }

    /**
     * 获取协议定义的设备标识
     *
     * @return
     */
    public String getProDeviceId() {
        return getDeviceId() + getAndroidId();
    }

    public String getApplicationName() {
        initApplicationInfo();
        return applicationName;
    }
}
