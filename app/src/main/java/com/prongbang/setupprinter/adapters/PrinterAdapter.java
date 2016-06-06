package com.prongbang.setupprinter.adapters;

/**
 * Created by dekdodev9 on 10/11/2015 AD.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prongbang.setupprinter.R;
import com.prongbang.setupprinter.dto.PrinterModel;
import com.prongbang.setupprinter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PrinterAdapter extends BaseAdapter {

    private SessionManager session;

    private Activity activity;
    private LayoutInflater inflater;
    private List<PrinterModel> printerItems;

    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    private ArrayList<String> itemStatus = new ArrayList<String>();

    public PrinterAdapter(Activity activity, List<PrinterModel> printerItems) {

        this.activity = activity;
        this.printerItems = printerItems;

        session = new SessionManager(activity.getApplicationContext());

        // initializes all items value with false
        for (int i = 0; i < this.getCount(); i++) {
            itemChecked.add(i, false);
            itemStatus.add(i, printerItems.get(i).getStatus());
        }
    }

    @Override
    public int getCount() {
        return printerItems.size();
    }

    @Override
    public Object getItem(int location) {
        return printerItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.printer_list_row, null);
        }

        TextView no = (TextView) convertView.findViewById(R.id.no);
        TextView tvDeviceId = (TextView) convertView.findViewById(R.id.tvDeviceId);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);

        PrinterModel printerModel = printerItems.get(position);

        no.setText("" + (position + 1));
        tvDeviceId.setText("" + printerModel.getDeviceName());

        itemStatus.add(position, printerItems.get(position).getStatus());

        if ("Y".equals(printerItems.get(position).getStatus())) {
            tvStatus.setText("Paired");
        } else {
            tvStatus.setText("Not Paired");
        }

        return convertView;
    }

    public ArrayList<Boolean> getItemChecked() {
        return this.itemChecked;
    }
}