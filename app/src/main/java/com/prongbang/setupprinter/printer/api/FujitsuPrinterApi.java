package com.prongbang.setupprinter.printer.api;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fujitsu.fcl.libFTP2076210R0_Android.FTPAndroidLib;
import com.prongbang.setupprinter.printer.dialog.MessageDialog;
import com.prongbang.setupprinter.printer.utils.PrinterUtil;

/**
 * Created by prongbang on 5/31/2016.
 */
public class FujitsuPrinterApi {

    public static String modelPrinter = "";
    public static String macAddress = null;

    private static final int POWER_OFF = 2;
    private static int model = FTPAndroidLib.PRINTERMODEL_628WSL210;

    public static FTPAndroidLib ftpAndroidLib;

    public FujitsuPrinterApi() {
        ftpAndroidLib = FTPAndroidLib.getInstance();
    }

    public static boolean checkOpenFujitsuPrinter(Activity activity, String modelPrinter, String address) {
        int printerStatus = openPrinter(address);
        if (printerStatus == POWER_OFF) {
            MessageDialog.openDialog(activity, PrinterUtil.CANNOT_OPEN_POWER_PRINTER + " FUJITSU " + modelPrinter);
            return true;
        }
        return false;
    }

    public static int openPrinter(String address) {
        int iRet = 0;
        ftpAndroidLib.closePrinter();
        iRet = ftpAndroidLib.startPrinter(model, address, handler);
        return iRet;
    }

    private static final Handler handler = new Handler() {

        public void handlerMessage(Message message) {
            Log.i("PrintPreview", "=> " + message.toString());
        }
    };

}
