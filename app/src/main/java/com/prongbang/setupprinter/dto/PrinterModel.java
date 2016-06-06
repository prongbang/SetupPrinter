package com.prongbang.setupprinter.dto;

import java.io.Serializable;

public class PrinterModel implements Serializable {

    private String deviceName;
    private String deviceAddress;
    private String status;

    public PrinterModel() {

    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
