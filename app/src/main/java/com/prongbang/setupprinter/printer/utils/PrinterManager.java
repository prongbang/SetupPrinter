package com.prongbang.setupprinter.printer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.prongbang.setupprinter.activities.SetupPrinterActivity;
import com.prongbang.setupprinter.printer.dialog.MessageDialog;
import com.prongbang.setupprinter.utils.SessionManager;

import java.util.List;

/**
 * Created by prongbang on 5/31/2016.
 */
public class PrinterManager {

    private static String TAG = PrinterManager.class.getSimpleName();
    public static SessionManager session;

    public static void clearModelAndAddressUnpair(Context context){
        List<String[]> pairedPrinter = BluetoothUtil.searchPaired();
        if(pairedPrinter.size() == 0){
            session = new SessionManager(context);
            session.setModelPrinter("");
            session.setKeyPrinter(null);
        }
    }

    public static boolean checkNullAddress(Activity activity, String address){
        if (address == null) {
            Log.i(TAG, "address => " + address);
            MessageDialog.openDialog(activity, PrinterUtil.CANNOT_PAIR_PRINTER);
            //if (MessageDialog.isOpenSetupPrinterStatus()) return true;
            return true;
        }
        return false;
    }

    public static boolean openSetupPrinter(Activity activity){
        List<String[]> pairedPrinter = BluetoothUtil.searchPaired();
        if(pairedPrinter.size() == 0){
            Intent intent = new Intent(activity, SetupPrinterActivity.class);
            activity.startActivity(intent);
            return true;
        }
        return false;
    }

}
