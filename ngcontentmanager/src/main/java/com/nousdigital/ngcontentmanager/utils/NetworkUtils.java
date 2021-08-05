package com.nousdigital.ngcontentmanager.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.RequiresPermission;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * Return whether network is connected.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * Return whether mobile data is enabled.
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    public static boolean getMobileDataEnabled(Context context) {
        try {
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return false;
            @SuppressLint("PrivateApi")
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Return whether using mobile data.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileData(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return null != info
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return null;
        return manager.getActiveNetworkInfo();
    }


    /**
     * Return whether wifi is connected.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null
                && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }
}