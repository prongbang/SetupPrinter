package com.prongbang.setupprinter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.prongbang.setupprinter.activities.SetupPrinterActivity;
import com.prongbang.setupprinter.printer.api.BixolonPrinterApi;
import com.prongbang.setupprinter.printer.api.FujitsuPrinterApi;
import com.prongbang.setupprinter.printer.dialog.MessageDialog;
import com.prongbang.setupprinter.printer.dto.TestPrinterDto;
import com.prongbang.setupprinter.printer.slip.TestPrinterSlip;
import com.prongbang.setupprinter.printer.utils.BluetoothUtil;
import com.prongbang.setupprinter.printer.utils.PrinterManager;
import com.prongbang.setupprinter.printer.utils.PrinterUtil;
import com.prongbang.setupprinter.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private static Context context;
    private static BixolonPrinterApi bixolonPrinterApi;
    private static FujitsuPrinterApi fujitsuPrinterApi;
    private SessionManager session;

    private Button btnPrint;
    private Button btnSetupPrinter;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        session = new SessionManager(context);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnPrint = (Button) findViewById(R.id.btn_print);
        btnSetupPrinter = (Button) findViewById(R.id.btn_setup_printer);

        btnPrint.setOnClickListener(this);
        btnSetupPrinter.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bixolonPrinterApi.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        fujitsuPrinterApi = new FujitsuPrinterApi();
        bixolonPrinterApi = new BixolonPrinterApi(this);
        BluetoothUtil.startBluetooth();

        if (!(session.getConnectedPrinter()).equals(""))
            tvStatus.setText("Paired : " + session.getConnectedPrinter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_print:

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                TestPrinterDto testPrinterDto = new TestPrinterDto();
                testPrinterDto.setDate(sdf.format(new Date()));

                // Print Slip
                testPrinterSlip(testPrinterDto);

                break;
            case R.id.btn_setup_printer:
                Intent intent = new Intent(MainActivity.this, SetupPrinterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void testPrinterSlip(TestPrinterDto testPrinterDto) {

        BixolonPrinterApi.modelPrinter = session.getModelPrinter();
        BixolonPrinterApi.macAddress = session.getKeyPrinter();

        FujitsuPrinterApi.modelPrinter = session.getModelPrinter();
        FujitsuPrinterApi.macAddress = session.getKeyPrinter();

        PrinterManager.clearModelAndAddressUnpair(context);

        if (PrinterManager.checkNullAddress(this, BixolonPrinterApi.macAddress)) return;

        // check printer by model
        if (PrinterUtil.printer(PrinterUtil.BIXOLON, BixolonPrinterApi.modelPrinter)) {

            testPrinterDto.setModel("BIXOLON");

            // open bixolon
            if (BixolonPrinterApi.checkOpenBixolonPrinter(this, BixolonPrinterApi.modelPrinter))
                return;

            // bixolon print
            TestPrinterSlip testPrinterSlip = new TestPrinterSlip(BixolonPrinterApi.bixolonPrinterApi, btnPrint);
            testPrinterSlip.bixolon(testPrinterDto);

        } else if (PrinterUtil.printer(PrinterUtil.FUJITSU, FujitsuPrinterApi.modelPrinter)) {

            testPrinterDto.setModel("FUJITSU");

            // open fujitsu
            if (FujitsuPrinterApi.checkOpenFujitsuPrinter(this, FujitsuPrinterApi.modelPrinter, FujitsuPrinterApi.macAddress))
                return;

            // fujitsu print
            TestPrinterSlip testPrinterSlip = new TestPrinterSlip(FujitsuPrinterApi.ftpAndroidLib, btnPrint);
            testPrinterSlip.fujitsu(testPrinterDto);

        } else {
            MessageDialog.showToastMessage(getApplicationContext(), PrinterUtil.NOT_FOUND_PRINTER);
        }
    }
}
