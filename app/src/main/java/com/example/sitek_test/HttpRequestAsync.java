package com.example.sitek_test;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpRequestAsync extends AsyncTask<Void, Integer, Integer>
{
    private Context ctx;
    private String requestGetUsers;
    private String requestAuth;
    private String uidAuth;
    private utils.Root rootUsersJSon;
    private utils.RootAuth rootAuthJSon;
    private int responseCode = 404;

    // В потоке могут обрабатываются оба запроса к серверу, в соответсвии с результатом в postExecute соответствующая обработка
    HttpRequestAsync(Context ctx, String requestGetUsers, String requestAuth, String uidAuth)
        {
            this.ctx = ctx;

            if (requestGetUsers!=null && requestAuth!=null) {

                this.requestGetUsers = null;
                this.requestAuth = null;
                this.uidAuth = null;

            }
                else {

                    this.requestGetUsers = requestGetUsers;
                    this.requestAuth = requestAuth;
                    this.uidAuth = uidAuth;
                }
        }

        @Override
        protected void onPreExecute()
        {

            MainActivity activity = null;

            try {
                activity = (MainActivity) ctx;
                activity.showBlockScreen();

            }
            catch (NullPointerException ne) {

            }


        }

        boolean isConnectionLengthZero(HttpURLConnection huc)
        {
            boolean rVal=true;
            if (huc!=null)
                if (huc.getContentLength()>0) rVal = false;

            return rVal;
        }

    HttpsURLConnection doConnect(URL url)
    {
        HttpsURLConnection rVal=null;
        try
        {
            rVal = (HttpsURLConnection) url.openConnection();
            rVal.setConnectTimeout(5*1000);

//                rVal.setDefaultUseCaches(true);
//                rVal.setUseCaches(true);
//                rVal.setReadTimeout(3*1000);
            //rVal.setChunkedStreamingMode(1024);
        }

        catch(SocketTimeoutException ss)
        {
            if (rVal!=null) {rVal.disconnect(); rVal=null;}
        }
        catch (MalformedURLException e)
        {
            if (rVal!=null) {rVal.disconnect(); rVal=null;}
        }
        catch (IOException e)
        {
            if (rVal!=null) {rVal.disconnect(); rVal=null;}
        }

        return rVal;
    }

    // Заплатка для проброса SSL
    private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {

            Log.e("102500", "getAcceptedIssuers");

            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {

            Log.e("102500", "checkClientTrusted");

        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {

            Log.e("102500", "checkServerTrusted");

        }
    } };

    @Override
    protected Integer doInBackground(Void... unused)
    {
        long time = System.currentTimeMillis();

        String request = null;
        if (requestGetUsers!=null) request = requestGetUsers;
        else request = requestAuth;

        if (request == null) return 100500;


        String basicAuth = "Basic " + new String(Base64.encode((new String (constanta.HTTPRequestLogin+":"+constanta.HTTPRequestPassword)).getBytes(),Base64.NO_WRAP ));
        URL url = null;
        HttpsURLConnection urlConnection = null;

        File dir = new File(constanta.internalCacheDir);
        if (!dir.exists()) dir.mkdirs();
        File cacheFile = new File(constanta.internalCacheFile);

        // *** *** ***
        // Заплатка для проброса SSL сертификата
        // *** *** ***
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            Log.e("102500", "Error sslcontext = null");
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {

            Log.e("102500", "KeyManagementException " + e.toString());

        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // *** *** ***
        // *** *** ***
        // *** *** ***

        try {
            url = new URL(request);
            urlConnection = doConnect(url);
        } catch (IOException e) {

        }

        if (urlConnection != null) {

            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            urlConnection.setUseCaches(true);
            urlConnection.setDefaultUseCaches(true);

            urlConnection.setReadTimeout(3000);
            urlConnection.setConnectTimeout(5000);

            urlConnection.setRequestProperty ("Authorization", basicAuth);

            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;

            try {
                fileOutputStream = new FileOutputStream(cacheFile);
                inputStream = urlConnection.getInputStream();
            } catch (IOException e) {
                Log.e("102500","FAILED TO VREATE INPUT STREAM "+ e.toString());
            }

            if (inputStream!=null&&fileOutputStream!=null) {

                byte[] buffer = new byte[4 * 1024];
                int bufferLength = -1; //used to store a temporary size of the buffer

                try {
                    while ((bufferLength = inputStream.read(buffer)) > 0)
                        fileOutputStream.write(buffer, 0, bufferLength);
                } catch (IOException e) {
                }

                try {
                    fileOutputStream.flush();
                    fileOutputStream.getFD().sync();
                    fileOutputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Если запрос по получению таблицы - Читаем и разбираем полученный файл
            if ( cacheFile!=null && requestGetUsers !=null )
            {
                if (cacheFile.exists())
                {
                    String parsedString = utils.fileToString(cacheFile.getPath());

                    if (parsedString!=null) {

                        rootUsersJSon = utils.parseUsersJSON(parsedString);

                    }

                } else Log.e("102500", "Cache File not exist: " + cacheFile.getName());
            } else {Log.e("102500", "File =null");}

            // Если запрос по аутентификации - Читаем и разбираем полученный файл
            if ( cacheFile!=null && requestAuth !=null )
            {
                if (cacheFile.exists())
                {
                    String parsedString = utils.fileToString(cacheFile.getPath());

                    if (parsedString!=null) {

                        rootAuthJSon = utils.parseAuthJSON(parsedString);

                    }

                } else Log.e("102500", "Cache File not exist: " + cacheFile.getName());
            } else {Log.e("102500", "File =null");}


            urlConnection.disconnect();
        }

        Log.e("102500", "responseCode " + responseCode);

        // Чтобы блок скрин не мигал
        while (System.currentTimeMillis()<time+1500) {

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return 10000;
    }

        @Override
        protected void onProgressUpdate(Integer... values)
        {

        }

        @Override
        protected void onPostExecute(Integer result)
        {
            MainActivity activity = null;

            // аккуратно работаем с основной активити, может получиться, что активити здохла, а поток все еще работает
            try {
                activity = (MainActivity) ctx;
                activity.disableBlockScreen();
            }
                catch (NullPointerException ne ) {}

            // обновляем спиннер
            if (rootUsersJSon!=null) {

                // аккуратно работаем с основной активити, может получиться, что активити здохла, а поток все еще работает
                try {

                    if (rootUsersJSon.users.listUsers!=null) {

                        Spinner spiner = (Spinner)activity.findViewById(R.id.spinner);
                        MainActivity.SpinnerCustomAdapter spinnerAdapter = (MainActivity.SpinnerCustomAdapter)spiner.getAdapter();
                        spinnerAdapter.updateAdapter(rootUsersJSon.users.listUsers);

                    }

                }
                catch (NullPointerException ne ) {}

            }
                else if (requestGetUsers!=null) Toast.makeText(ctx,"Ошибка получения пользователей с сервера или ответ не распознан.", Toast.LENGTH_LONG).show();


            // переходим на следующую активити
            if (rootAuthJSon!=null) {

                if (responseCode == 200) {
                    if (rootAuthJSon != null && rootAuthJSon.authentication!=null) {

                        try {

                            if (rootAuthJSon.authentication != null) {

                                // аккуратно работаем с основной активити, может получиться, что активити здохла, а поток все еще работает
                                try {
                                    DatabaseObject.writeSessionToDb(rootAuthJSon, uidAuth);
                                } catch (Exception e) {}
                            }

                        } catch (NullPointerException ne) { }

                        Intent intent = new Intent(ctx, ChildActivity.class);
                        intent.putExtra("uid", uidAuth);

                        try {
                            ctx.startActivity(intent);
                        }
                        catch (Exception e) {}

                    }
                    else
                        Toast.makeText(ctx, "Ошибка - пустой ответ от сервера или ответ не распознан.", Toast.LENGTH_LONG).show();

                } // response code == 200
                    else
                        Toast.makeText(ctx, "Пароль неверный. Попробуйте еще раз.", Toast.LENGTH_LONG).show();

            }
            else if (requestAuth!=null) Toast.makeText(ctx, "Пароль неверный. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
            // rootAuthJSON not null


        }

}