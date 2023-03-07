package com.qsmp;

import android.util.Log;

import com.qsmplibrary.altbeacon.beacon.Printer;

public class HpPrinter implements Printer
{

    //private static final Logger LOGGER = LoggerFactory.getLogger(HpPrinter.class);
    @Override
    public String print(String message) {
        //LOGGER.info("HP Printer : {}", message);
        Log.d("HP Printer : {}",""+message);
        return message;
    }
}