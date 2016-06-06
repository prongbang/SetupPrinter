package com.prongbang.setupprinter.activities;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.prongbang.setupprinter.R;
import com.prongbang.setupprinter.adapters.PrinterAdapter;
import com.prongbang.setupprinter.dto.PrinterModel;
import com.prongbang.setupprinter.printer.utils.BluetoothUtil;
import com.prongbang.setupprinter.printer.utils.PrinterUtil;
import com.prongbang.setupprinter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetupPrinterActivity extends AppCompatActivity {

    private static String TAG = SetupPrinterActivity.class.getSimpleName();

    private ListView listView;
    private PrinterAdapter printerAdapter;
    private SessionManager session;

    private ProgressDialog mProgressDlg;
    private BluetoothAdapter mBluetoothAdapter;
    private List<PrinterModel> printerList;
    private List<BluetoothDevice> mDeviceList;
    private String MODEL_NAME;
    private String MAC_ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_printer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listView);

        // TODO new code
        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);

        BluetoothUtil.startBluetooth();

        session = new SessionManager(getApplicationContext());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        printerList = new ArrayList<PrinterModel>();
        mDeviceList = new ArrayList<BluetoothDevice>();

        // Add Device Paired
        Set<BluetoothDevice> pairedDevices = BluetoothUtil.devicePaired();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                addPrinter(device);
            }
            if (pairedDevices.size() == PrinterUtil.devices.length) {
                populatePrinter();
                checkPairedDevice();
            }
        }

    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    mBluetoothAdapter.startDiscovery();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                if (printerList.size() != PrinterUtil.devices.length) {
                    mProgressDlg.show();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                mProgressDlg.dismiss();

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                if (printerList.size() != PrinterUtil.devices.length) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Add the name and address to an array adapter to show in a ListView
                    if (PrinterUtil.search(device.getName())) {

                        if (PrinterUtil.unique(printerList, device.getName())) {
                            addPrinter(device);
                            Log.i(TAG, device.getName() + " " + device.getAddress());
                        }

                        populatePrinter();

                        checkPairedDevice();
                    }
                }

                mProgressDlg.dismiss();

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {

                    saveModelAndAddress(MODEL_NAME, MAC_ADDRESS);

                    checkPairedDevice();

                    showToast("Paired");

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {

                    checkUnPairedDevice();

                    showToast("Unpaired");

                }
            }
        }

    };

    private void addPrinter(BluetoothDevice device) {
        PrinterModel printerModel = new PrinterModel();
        printerModel.setDeviceName(device.getName());
        printerModel.setDeviceAddress(device.getAddress());
        printerList.add(printerModel);
        mDeviceList.add(device);
    }

    private void checkUnPairedDevice() {

        PrinterAdapter printerAdapter = (PrinterAdapter) listView.getAdapter();

        for (int i = 0; i < printerAdapter.getCount(); i++) {
            PrinterModel printerModel = (PrinterModel) listView.getAdapter().getItem(i);
            printerModel.setStatus("N");
        }

        saveModelAndAddress("", null);

        ((PrinterAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    private void checkPairedDevice() {

        if (mDeviceList.size() > 0) {

            List<String[]> devicePaireds = BluetoothUtil.searchPaired();
            PrinterAdapter printerAdapter = (PrinterAdapter) listView.getAdapter();

            // change status connect
            for (int i = 0; i < printerAdapter.getCount(); i++) {
                PrinterModel printerModel = (PrinterModel) listView.getAdapter().getItem(i);
                if (printerModel.getDeviceAddress().equals(session.getKeyPrinter())) {
                    printerModel.setStatus("Y");
                } else {
                    printerModel.setStatus("N");
                }
            }

            if (devicePaireds.size() == 0) {

                for (int i = 0; i < printerAdapter.getCount(); i++) {
                    PrinterModel printerModel = (PrinterModel) listView.getAdapter().getItem(i);
                    printerModel.setStatus("N");
                }
                saveModelAndAddress("", null);
            }

            ((PrinterAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void populatePrinter() {

        printerAdapter = new PrinterAdapter(SetupPrinterActivity.this, printerList);

        listView.setAdapter(printerAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BluetoothDevice device = mDeviceList.get(position);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

                    saveModelAndAddress(device.getName(), device.getAddress());

                    checkPairedDevice();

                } else {

                    Log.i("New Device", device.getName() + " " + device.getAddress());

                    MODEL_NAME = device.getName();
                    MAC_ADDRESS = device.getAddress();

                    BluetoothUtil.pairDevice(device);

                }

            }
        });
    }

    private void saveModelAndAddress(String model, String address) {
        session.setModelPrinter(model);
        session.setKeyPrinter(address);
        session.setConnectedPrinter(model);
    }

}
