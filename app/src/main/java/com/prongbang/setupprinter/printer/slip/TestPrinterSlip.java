package com.prongbang.setupprinter.printer.slip;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.bixolon.printer.BixolonPrinter;
import com.fujitsu.fcl.libFTP2076210R0_Android.FTPAndroidLib;
import com.prongbang.setupprinter.printer.dto.TestPrinterDto;
import com.prongbang.setupprinter.printer.utils.BixolonPrinterUtil;

/**
 * Created by prongbang on 5/31/2016.
 */
public class TestPrinterSlip {

    private static final String TAG = TestPrinterSlip.class.getSimpleName();

    private static final long PRINTING_SLEEP_TIME = 300;

    //The time in millis.
    private static final long PRINTING_TIME = 2200;

    // BIXOLON
    private Button btnPrint;
    private BixolonPrinter bixolonPrinterApi;

    // FUJITSU
    private FTPAndroidLib ftpAndroidLib;

    /**
     * FUJITSU
     */
    public TestPrinterSlip(FTPAndroidLib ftpAndroidLib, Button btnPrint) {
        this.btnPrint = btnPrint;
        this.ftpAndroidLib = ftpAndroidLib;
    }

    public void fujitsu(final TestPrinterDto testPrinterDto) {

        printingTime();

        Thread t = new Thread() {

            public void run() {
                try {

                    Rect rect = new Rect(0, 0, 383, 50); // width:384 dots (the case of 2 inch printer) iRet = ftpAndroidLib.startPage(rect);
                    Point point = new Point(0, 50);
                    ftpAndroidLib.printCharacterString(testPrinterDto.getModel() + " : " + testPrinterDto.getDate());
                    ftpAndroidLib.startPage(rect);
                    ftpAndroidLib.setAbsolutePosition(point);
                    ftpAndroidLib.setCharacterStyle(FTPAndroidLib.FONT_8X16, FTPAndroidLib.FONT_16X16, FTPAndroidLib.FONTDSIZE_NONE, false, false, FTPAndroidLib.UNDERLINE_NONE, 0, 0, 0);
                    ftpAndroidLib.printPage(FTPAndroidLib.END_PAGEMODE);

                } catch (Exception e) {
                    Log.e(TAG, "Printing", e);
                }
            }
        };

        t.start();
    }

    /**
     * BIXOLON
     */
    public TestPrinterSlip(BixolonPrinter bixolonPrinterApi, Button btnPrint) {
        this.btnPrint = btnPrint;
        this.bixolonPrinterApi = bixolonPrinterApi;
    }

    public void bixolon(final TestPrinterDto testPrinterDto) {

        printingTime();

        Thread t = new Thread() {

            public void run() {
                try {

                    bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_THAI18);

                    BixolonPrinterUtil.printText(bixolonPrinterApi, testPrinterDto.getModel() + " : " + testPrinterDto.getDate(), BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A);

                    bixolonPrinterApi.lineFeed(3, false);

                } catch (Exception e) {
                    Log.e(TAG, "Printing", e);
                }
            }
        };

        t.start();
    }

    private void printingTime() {

        btnPrint.setEnabled(false);

        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                super.run();
                btnPrint.setEnabled(true);
            }
        }, PRINTING_TIME);

    }

}
