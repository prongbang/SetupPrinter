package com.prongbang.setupprinter.printer.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothUtil {

    public static final String TAG = "BLUETOOTH";

    private final static int REQUEST_BT_ENABLE = 1;
    private final static int REQUEST_BT_SETTINGS = 2;
    private final static int REQUEST_BT_PAIRING = 3;

    public static void startBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    public static void stopBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private void unPairedDeviceAll() {
        List<String[]> devicePaireds = BluetoothUtil.searchPaired();
        for (String[] devicePaired : devicePaireds) {
            BluetoothUtil.unpairMac(devicePaired[1]);
        }
    }

    public static void pairedDevice(Context context, BluetoothDevice device) {
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        Intent intent = new Intent(ACTION_PAIRING_REQUEST);
        String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
        intent.putExtra(EXTRA_DEVICE, device);
        String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static List<String[]> searchPaired() {

        List<String[]> devicePaireds = new ArrayList<String[]>();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            // Device does not support Bluetooth

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {

                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    String[] devicePaired = new String[2];
                    devicePaired[0] = device.getName();
                    devicePaired[1] = device.getAddress();
                    devicePaireds.add(devicePaired);
                }
            }
        }
        return devicePaireds;
    }

    public static Set<BluetoothDevice> devicePaired() {

        Set<BluetoothDevice> pairedDevices = null;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            // Device does not support Bluetooth

            pairedDevices = mBluetoothAdapter.getBondedDevices();

        }
        return pairedDevices;
    }

    public static void requireBluetoothEnable(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
    }

    public static void openBluetoothSettings(Activity activity) {
        Intent settingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        activity.startActivityForResult(settingsIntent, REQUEST_BT_SETTINGS);
    }

    public static void startBluetoothPairing(Activity activity, BluetoothDevice device) {
        Intent pairingIntent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION);
        pairingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(pairingIntent, REQUEST_BT_PAIRING);
    }

    public static void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void unpairMac(String macToRemove) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        try {
            Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class.getCanonicalName());
            Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
            boolean cleared = false;
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                String mac = bluetoothDevice.getAddress();
                if (mac.equals(macToRemove)) {
                    removeBondMethod.invoke(bluetoothDevice);
                    Log.i(TAG, "Cleared Pairing");
                    cleared = true;
                    break;
                }
            }

            if (!cleared) {
                Log.i(TAG, "Not Paired");
            }
        } catch (Throwable th) {
            Log.e(TAG, "Error pairing", th);
        }
    }
}
