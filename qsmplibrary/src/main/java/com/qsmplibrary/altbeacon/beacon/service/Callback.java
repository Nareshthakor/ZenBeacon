/**
 * Radius Networks, Inc.
 * http://www.radiusnetworks.com
 *
 * @author David G. Young
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.qsmplibrary.altbeacon.beacon.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qsmplibrary.altbeacon.beacon.BeaconIntentProcessor;
import com.qsmplibrary.altbeacon.beacon.BeaconManager;
import com.qsmplibrary.altbeacon.beacon.IntentHandler;
import com.qsmplibrary.altbeacon.beacon.logging.LogManager;

import java.io.IOException;
import java.io.Serializable;

public class Callback implements Serializable {
    private static final String TAG = "Callback";

    //TODO: Remove this constructor in favor of an empty one, as the package name is no longer needed
    public Callback(String intentPackageName) {
    }

    /**
     * Tries making the callback, first via messenger, then via intent
     *
     * @param context
     * @param dataName
     * @param data
     * @return false if it callback cannot be made
     */
    public boolean call(Context context, String dataName, Bundle data) {
        boolean success = false;

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context.getPackageName(), "org.altbeacon.beacon.BeaconIntentProcessor"));
        intent.putExtra(dataName, data);
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(context);
        if (beaconManager.isScannerInDifferentProcess()) {
            LogManager.d(TAG, "attempting callback via global broadcast intent: %s",intent.getComponent());
            try {
                context.startService(intent);
                success = true;
            } catch (Exception e) {
                LogManager.e(
                        TAG,
                        "Failed attempting to start service: " + intent.getComponent().flattenToString(),
                        e
                );
            }
        }
        else {
            LogManager.d(TAG, "attempting callback via direct method call");
            new IntentHandler().convertIntentsToCallbacks(context, intent);
        }
        return success;
    }

    @SuppressWarnings("unused")
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
