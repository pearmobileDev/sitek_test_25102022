package com.example.sitek_test;


import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
public class constanta
{
    public static String HTTPRequestLogin = "http";
    public static String HTTPRequestPassword = "http";
    public static String HTTPRequestBase = "https://dev.sitec24.ru/UKA_TRADE/hs/MobileClient/";
    public static String HTTPRequestUsers = "";
    public static String HTTPRequestAuthUser = "";
    public static String rawDbName = "sitek_db";
    public static String dbName = "sitek_db.db";
    public static String internalDir = "";
    public static String internalCacheDir = "";
    public static String internalCacheFile = "";
    public static String UUID = "";

    public static void init(Context ctx) {

        internalDir = "/data/data/"+ctx.getPackageName()+"/databases/";
        internalCacheDir = "/data/data/"+ctx.getPackageName()+"/cache/";
        internalCacheFile = internalCacheDir+"megacash.cashe";

        UUID = utils.getUUID(ctx);
        HTTPRequestUsers = HTTPRequestBase + UUID + "/form/users/";
        HTTPRequestAuthUser = HTTPRequestBase + UUID + "/authentication/";

    }

}