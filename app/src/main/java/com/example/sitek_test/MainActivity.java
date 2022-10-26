package com.example.sitek_test;

import static android.view.ViewGroup.LayoutParams.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    // Заплатки для FrameLayout
    void showBlockScreen() {

        try {

            FrameLayout kingLayout = ((FrameLayout)findViewById(R.id.kingLayout));


            for (int i = 0; i < kingLayout.getChildCount(); i++) {
                View v = kingLayout.getChildAt(i);

                v.setVisibility(View.VISIBLE);

                v.setClickable(true);
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                v.postInvalidate();
            }

        }
        catch (NullPointerException ne) {

        }


    }

    void disableBlockScreen() {

        try {
            FrameLayout kingLayout = ((FrameLayout)findViewById(R.id.kingLayout));
            kingLayout.setClickable(true);

            for (int i = 0; i < kingLayout.getChildCount(); i++) {
                View v = kingLayout.getChildAt(i);

                if (v.getId() == R.id.blockScreen) {
                    v.setVisibility(View.INVISIBLE);
                }
                else {
                    v.setVisibility(View.VISIBLE);
                }

                v.setClickable(true);
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                v.postInvalidate();

            }

        }
        catch (NullPointerException ne) {

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Context ctx = this;

        getSupportActionBar().hide();

        constanta.init(this);

        DatabaseObject.init(this);
        DatabaseObject.open();

        // Заплатка для SSL
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        // Получаем экземпляр элемента Spinner
        final Spinner spinner = findViewById(R.id.spinner);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(new ArrayList<utils.ListUser>());

        spinner.setAdapter(adapter);
        spinner.setPrompt("Выберите имя пользователя");
        //spinner.setSelection(0, true);

        // Запрашиваем таблицу пользователей
        new HttpRequestAsync(this, constanta.HTTPRequestUsers, null, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Повторяем запрос таблицы еще раз
        ((Button)findViewById(R.id.btn_try)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBlockScreen();

                new HttpRequestAsync(ctx, constanta.HTTPRequestUsers,null, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        // Обработчик входа пользователя
        ((Button)findViewById(R.id.btn_enter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBlockScreen();
                String authRequest = null;

                utils.ListUser lu = (utils.ListUser) spinner.getSelectedItem();
                if (lu != null && lu.uid!=null) {

                    EditText eView = (EditText) findViewById(R.id.passEditView);

                    String pass = eView.getText().toString();
                    authRequest = constanta.HTTPRequestAuthUser+"?uid="+lu.uid+"&pass="+pass+"&copyFromDevice=false&nfc=";

                }
                else Toast.makeText(MainActivity.this, "Пользователь не выбран. Пожалуйста выберите пользователя и повторите попытку.", Toast.LENGTH_LONG);


                Log.e("102500", "authRequest = "+authRequest);

                new HttpRequestAsync(ctx, null, authRequest, lu.uid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

    }

    public class SpinnerCustomAdapter extends BaseAdapter {

        ArrayList<utils.ListUser> arrayList;

        public SpinnerCustomAdapter(ArrayList<utils.ListUser> arrayList) {

            this.arrayList = arrayList;

        }

        public void updateAdapter(ArrayList<utils.ListUser> arrayList) {

            this.arrayList = arrayList;
            notifyDataSetChanged();

        }


        @Override
        public int getCount() {

            if (arrayList==null) return 0;
            else return arrayList.size();

        }

        @Override
        public utils.ListUser getItem(int i) {

            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);

            TextView label = (TextView) row.findViewById(R.id.text);
            label.setText(getItem(i).user);



            return row;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        DatabaseObject.close();
    }

    @Override
    protected void onStop() {
        super.onStop();

        DatabaseObject.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseObject.open();
    }

}