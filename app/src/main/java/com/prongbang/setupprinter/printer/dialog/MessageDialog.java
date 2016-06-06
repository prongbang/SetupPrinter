package com.prongbang.setupprinter.printer.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.prongbang.setupprinter.printer.utils.PrinterManager;
import com.prongbang.setupprinter.printer.utils.PrinterUtil;

/**
 * Created by prongbang on 5/31/2016.
 */
public class MessageDialog {

    private static boolean openSetupPrinterStatus = true;

    public static boolean isOpenSetupPrinterStatus() {
        return openSetupPrinterStatus;
    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void openDialog(final Activity activity, final String message) {
        try {
            TextView title = new TextView(activity);
            // You Can Customise your Title here
            title.setText("System Information");
            title.setBackgroundColor(Color.BLUE);
            title.setPadding(10, 15, 15, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setCustomTitle(title);
            alertDialog.setMessage(message);

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    if (message.equals(PrinterUtil.CANNOT_PAIR_PRINTER)) {
                        //openSetupPrinterStatus = PrinterManager.openSetupPrinter(activity);
                        PrinterManager.openSetupPrinter(activity);
                    }

                }
            });

            alertDialog.show();

            // You Can Customise your Message here
            TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
            messageView.setTextSize(18);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
