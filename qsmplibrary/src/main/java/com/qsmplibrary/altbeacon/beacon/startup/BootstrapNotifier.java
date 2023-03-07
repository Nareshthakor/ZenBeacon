package com.qsmplibrary.altbeacon.beacon.startup;

import android.content.Context;

import com.qsmplibrary.altbeacon.beacon.MonitorNotifier;


/**
 * @deprecated Will be removed in 3.0.  See http://altbeacon.github.io/android-beacon-library/autobind.html
 */
@Deprecated
public interface BootstrapNotifier extends MonitorNotifier {
    public Context getApplicationContext();
}
