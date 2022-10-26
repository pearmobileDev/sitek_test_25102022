package com.example.sitek_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ChildActivity extends AppCompatActivity {

    String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        getSupportActionBar().hide();

        Bundle args = getIntent().getExtras();

        if(args!=null){
            uid = args.get("uid").toString();

        }

        TextView uidTxt  = (TextView)findViewById(R.id.child_uid);
        uidTxt.setText(uid);

        ListView lv = (ListView) findViewById(R.id.childList);

        Cursor crs = DatabaseObject.getAuthTableCursor(uid);
        if (crs!=null)
            if (crs.getCount()>0) {

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                        R.layout.child_list_row,
                        crs,
                        new String[] { "response", "continue_work","photohash","current_date" },
                        new int[] { R.id.response, R.id.continue_work, R.id.photohash, R.id.current_date },
                        0);

                lv.setAdapter(adapter);

            }
            else Toast.makeText(ChildActivity.this, "Не удалось получить данные из базы данных!", Toast.LENGTH_LONG).show();


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