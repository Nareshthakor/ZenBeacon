package com.qsmp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class About_Activity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView img_about_actionbar_back;
    TextView txt_appversion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        img_about_actionbar_back=findViewById(R.id.img_about_actionbar_back);
        txt_appversion=findViewById(R.id.txt_appversion);

        try {


            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;

            txt_appversion.setText("App Version : "+versionName);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



        img_about_actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}