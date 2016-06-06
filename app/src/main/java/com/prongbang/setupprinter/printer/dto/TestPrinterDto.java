package com.prongbang.setupprinter.printer.dto;

import java.io.Serializable;

/**
 * Created by prongbang on 6/3/2016.
 */
public class TestPrinterDto implements Serializable {
    private String model;
    private String date;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
