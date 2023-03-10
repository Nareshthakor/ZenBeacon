package com.qsmplibrary.altbeacon.beacon.client;

import com.qsmplibrary.altbeacon.beacon.Beacon;
import com.qsmplibrary.altbeacon.beacon.BeaconDataNotifier;

/**
 * This can be configured for the public beacon data store, or a private beacon data store.
 * In the public data store, you can read any value but only write to the values to the beacons you created
 *
 * @author dyoung
 *
 */
public interface BeaconDataFactory {
    /**
     * Asynchronous call
     * When data is available, it is passed back to the beaconDataNotifier interface
     * @param beacon
     */
    public void requestBeaconData(Beacon beacon, BeaconDataNotifier notifier);
}

