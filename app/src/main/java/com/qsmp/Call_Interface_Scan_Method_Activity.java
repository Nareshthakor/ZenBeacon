package com.qsmp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.qsmplibrary.altbeacon.beacon.BeaconController;
import com.qsmplibrary.altbeacon.beacon.Printer;

public class Call_Interface_Scan_Method_Activity extends AppCompatActivity implements Printer
{

    BeaconController hpBeaconController;
    public static final String MESSAGE_TO_PRINT = "hello world hp printer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_interface_scan_method);

        hpBeaconController = new BeaconController(new Call_Interface_Scan_Method_Activity(),Call_Interface_Scan_Method_Activity.this,"f06a8c12f45f80721e5e4777bd8c5df1",Qsmp_Dashboard_Activity.class,true);
       // hpBeaconController.print(MESSAGE_TO_PRINT);

        getBEaconData();

    }


    public void getBEaconData()
    {
        String data_return=hpBeaconController.print(MESSAGE_TO_PRINT);
        Log.d("Call_InterFace",""+data_return);
    }


    @Override
    public String print(String message)
    {
        Log.d("Res_main_class",""+message);
        return null;
    }
}