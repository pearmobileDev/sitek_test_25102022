package com.example.sitek_test;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class utils
{

    public class Root{
        @SerializedName("Users")
        public Users users;
    }

    public class Users{
        @SerializedName("ListUsers")
        public ArrayList<ListUser> listUsers;
    }

    public class ListUser{
        @SerializedName("User")
        public String user;
        @SerializedName("Uid")
        public String uid;
        @SerializedName("Language")
        public String language;
    }


    public class RootAuth{
        @SerializedName("Authentication")
        public Authentication authentication;
    }

    public class Authentication{
        @SerializedName("Response")
        public String Response;
        @SerializedName("ContinueWork")
        public String ContinueWork;
        @SerializedName("PhotoHash")
        public String PhotoHash;
        @SerializedName("CurrentDate")
        public String CurrentDate;
    }


    // Парсим Полученные данные c таблицей пользователей
    public static Root parseUsersJSON(String jString) {
        //Gson gson = new Gson();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Root parseResult = null;

        try {
            parseResult = gson.fromJson(jString, Root.class);
        } catch (Exception e) {
            Log.e("102500", "Error Parsing "+e.toString());
        }

        return parseResult;

    }

    // Парсим Полученные данные с аутентификацией, ммм копипаст:)
    public static RootAuth parseAuthJSON(String jString) {
        //Gson gson = new Gson();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        RootAuth parseResult = null;

        try {
            parseResult = gson.fromJson(jString, RootAuth.class);
        } catch (Exception e) {
            Log.e("102500", "Error Parsing "+e.toString());
        }

        return parseResult;

    }

    public static String fileToString(String path)
    {
        String rVal = "";

        File file = new File(path);

        if (file.exists())
        {
            try {
                final InputStream isFile = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(isFile));

                String line="";
                while ((line = reader.readLine())!= null) rVal = rVal + line;

                reader.close();
                isFile.close();
            } catch (IOException ioe) { ioe.printStackTrace(); }
        }

        return rVal;
    }


    // Получаем IMEI или IO или рандомный ID
    public static String getUUID(Context ctx) {

        String rVal = "";

        try {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    rVal = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);;

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    rVal = telephonyManager.getImei();

                } else {

                    telephonyManager.getDeviceId();

                }
            }
        }
        catch (Exception e) {};

        if ((rVal==null) || (rVal.length()==0)) {
            rVal = UUID.randomUUID().toString();
        }

        return rVal;
   }

}
