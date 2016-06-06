package com.prongbang.setupprinter.printer.utils;


import com.prongbang.setupprinter.dto.PrinterModel;

import java.util.List;

/**
 * Created by prongbang on 5/26/2016.
 */
public class PrinterUtil {

    public static String CANNOT_PAIR_PRINTER = "คุณยังไม่ได้เชื่อมต่อเครื่องพิมพ์";
    public static String CANNOT_OPEN_POWER_PRINTER = "คุณยังไม่เปิดเครื่องพิมพ์";
    public static String NOT_FOUND_PRINTER = "ไม่พบเครื่องพิมพ์";

    public static int FUJITSU = 0;
    public static int BIXOLON = 1;

    public static String[] devices = new String[]{
            "FUJITSU FTP-628WSL220",
            "BIXOLON SPP-R210"
    };

    public static Boolean unique(List<PrinterModel> printerList, String deviceName) {
        Boolean found = true;
        for (int i = 0; i < printerList.size(); i++) {
            if (printerList.get(i).getDeviceName().equals(deviceName)) {
                found = false;
                break;
            }
        }
        return found;
    }

    public static Boolean search(String deviceName) {
        Boolean found = false;
        String[] deviceNames = PrinterUtil.split(deviceName);
        if (deviceNames != null) {
            for (short i = 0; i < devices.length; i++) {
                if (devices[i].indexOf(deviceNames[0]) != -1) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    private static String[] split(String deviceName) {
        String[] deviceNames = null;
        try {
            deviceNames = deviceName.split(" ");
        } catch (Exception e) {

        }
        return deviceNames;
    }

    public static Boolean printer(int printerIndex, String deviceName) {
        Boolean found = false;
        String[] deviceNames = PrinterUtil.split(deviceName);
        if (deviceNames != null) {
            if (devices[printerIndex].indexOf(deviceNames[0]) != -1) {
                found = true;
            }
        }
        return found;
    }

}
