package com.prongbang.setupprinter.printer.api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bixolon.printer.BixolonPrinter;
import com.prongbang.setupprinter.printer.dialog.MessageDialog;
import com.prongbang.setupprinter.printer.utils.PrinterUtil;

/**
 * Created by prongbang on 5/31/2016.
 */
public class BixolonPrinterApi {

    private Context context;
    public static String modelPrinter = "";
    public static String macAddress = null;

    public static boolean openPrinter = false;

    private static PairWithPrinterTask task;
    public static BixolonPrinter bixolonPrinterApi;

    public static boolean connectedPrinter = false;

    //Two constants that some Bixolon printers send, but aren't included in the Bixolon library.
    private static final int MESSAGE_START_WORK = Integer.MAX_VALUE - 2;
    private static final int MESSAGE_END_WORK = Integer.MAX_VALUE - 3;

    // new onResume
    public BixolonPrinterApi(Context context) {
        this.context = context;
        bixolonPrinterApi = new BixolonPrinter(context, handlerBixolon, null);
        task = new PairWithPrinterTask();
        task.execute();
    }

    public void onPause() {
        if (task != null) {
            task.stop();
            task = null;
        }
        if (bixolonPrinterApi != null) {
            bixolonPrinterApi.disconnect();
        }
    }

    public static boolean checkOpenBixolonPrinter(Activity activity, String modelPrinter) {
        if (!openPrinter) {
            if (modelPrinter != null) {
                MessageDialog.openDialog(activity, PrinterUtil.CANNOT_OPEN_POWER_PRINTER + " BIXOLON " + modelPrinter);
            }
            return true;
        }
        return false;
    }

    class PairWithPrinterTask extends AsyncTask<Void, Void, Void> {

        int action = 0;
        boolean running = true;

        public PairWithPrinterTask() {

        }

        public void stop() {
            running = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (running) {
                if (!connectedPrinter) {
                    publishProgress();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (action < 60) {
                bixolonPrinterApi.findBluetoothPrinters();
                action++;
            } else {
                bixolonPrinterApi.disconnect();
                action = 0;
            }
        }
    }

    private final Handler handlerBixolon = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_STATE_CHANGE");
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTED");
                            connectedPrinter = true;
                            break;
                        case BixolonPrinter.STATE_CONNECTING:
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTING");
                            connectedPrinter = false;
                            break;
                        case BixolonPrinter.STATE_NONE:
                            Log.i("Handler", "BixolonPrinter.STATE_NONE");
                            connectedPrinter = false;
                            break;
                    }
                    break;
                case BixolonPrinter.MESSAGE_WRITE:
                    switch (msg.arg1) {
                        case BixolonPrinter.PROCESS_SET_SINGLE_BYTE_FONT:
                            Log.i("Handler", "BixolonPrinter.PROCESS_SET_SINGLE_BYTE_FONT");
                            break;
                        case BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT:
                            Log.i("Handler", "BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT");
                            break;
                        case BixolonPrinter.PROCESS_DEFINE_NV_IMAGE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_DEFINE_NV_IMAGE");
                            break;
                        case BixolonPrinter.PROCESS_REMOVE_NV_IMAGE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_REMOVE_NV_IMAGE");
                            break;
                        case BixolonPrinter.PROCESS_UPDATE_FIRMWARE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_UPDATE_FIRMWARE");
                            break;
                    }
                    break;
                case BixolonPrinter.MESSAGE_READ:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_READ");
                    break;
                case BixolonPrinter.MESSAGE_DEVICE_NAME:
                    Log.i("Handler", "BixolonPrinter.KEY_STRING_DEVICE_NAME - " + msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME));
                    Log.i("Handler", "BixolonPrinter.MESSAGE_DEVICE_NAME - " + msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME));

                    //if (modelPrinter.equals(msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME)))
                    //    Toast.makeText(context, "เครื่องพิมพ์ " + msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME) + " พร้อมใช้งาน", Toast.LENGTH_SHORT).show();

                    openPrinter = true;

                    break;
                case BixolonPrinter.MESSAGE_TOAST:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_TOAST - " + msg.getData().getString("toast"));
                    if (msg.getData().getString("toast").equals("Unable to connect device") || msg.getData().getString("toast").equals("Device connection was lost")) {
                        openPrinter = false;
                    }
                    break;
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if (msg.obj == null) {
                        openPrinter = false;
                    } else {
                        if (PrinterUtil.printer(PrinterUtil.BIXOLON, modelPrinter)) {
                            bixolonPrinterApi.connect(macAddress);
                            Log.i("Handler", "BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET" + msg.obj);
                        }
                    }
                    break;
                case BixolonPrinter.MESSAGE_PRINT_COMPLETE:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_PRINT_COMPLETE");
                    break;
                case BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP");
                    break;
                case MESSAGE_START_WORK:
                    Log.i("Handler", "MESSAGE_START_WORK");
                    break;
                case MESSAGE_END_WORK:
                    Log.i("Handler", "MESSAGE_END_WORK");
                    break;
            }
        }
    };

}
