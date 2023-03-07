package com.qsmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.SparseLongArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class ErrorPage_Qn_Activtiy extends AppCompatActivity implements ConnectivityReceiver_Qn.ConnectivityReceiverListener{

    Button btn_retry;
    private boolean backPressedToExitOnce=false;
    connectionDector dector;
    private ConnectivityReceiver_Qn mNetworkReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_error_page_qn_activtiy);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mNetworkReceiver = new ConnectivityReceiver_Qn();
        registerNetworkBroadcastForNougat();
        dector=new connectionDector(ErrorPage_Qn_Activtiy.this);

        btn_retry=findViewById(R.id.btn_retry);






        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (dector.checkNetwork())
                {
                    Intent i = new Intent(ErrorPage_Qn_Activtiy.this, SplashActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(ErrorPage_Qn_Activtiy.this, "No Internet Connection", Toast.LENGTH_LONG).show();

                }


            }
        });





    }


    private void registerNetworkBroadcastForNougat()
    {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChanges();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplicationApollo.getInstance().setConnectivityListener(ErrorPage_Qn_Activtiy.this);
        registerNetworkBroadcastForNougat();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void showSnack(boolean isConnected) {

        if (dector.checkNetwork())
        {
            Intent i = new Intent(ErrorPage_Qn_Activtiy.this, SplashActivity.class);
            startActivity(i);

            //finish();

        }
        else
        {
            Toast.makeText(ErrorPage_Qn_Activtiy.this, "No Internet Connection", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            //super.onBackPressed();
            moveTaskToBack(false);
              /*  this.finish();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);*/

            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else
        {
            this.backPressedToExitOnce = true;
            Toast.makeText(ErrorPage_Qn_Activtiy.this, "Press again to exit", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;


                }
            }, 3000);
        }
        //super.onBackPressed();

    }


}