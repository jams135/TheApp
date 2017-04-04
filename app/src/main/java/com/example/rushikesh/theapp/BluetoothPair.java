package com.example.rushikesh.theapp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BluetoothPair extends BroadcastReceiver {
    public BluetoothPair() {
    }

    private static final String tag="TheApp";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //if(intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            try {
                BluetoothDevice bl = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                Log.d(tag, bl.getName());
                Log.d(tag, Integer.toString(pin));
                byte[] pinbytes;
                pinbytes = ("" + pin).getBytes("UTF-8");
                bl.setPin(pinbytes);
                bl.setPairingConfirmation(true);
            } catch (Exception e) {
                e.printStackTrace();
            }


            //throw new UnsupportedOperationException("Not yet implemented");
        //}
    }
}
