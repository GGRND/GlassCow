package com.eaaa.glasscow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mirasense.scanditsdk.LegacyPortraitScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKScanSettings;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

public class ScanBarCodeActivity extends Activity implements ScanditSDKListener {

    private ScanditSDKAutoAdjustingBarcodePicker mPicker;

    @Override
    protected void onResume() {
        mPicker.startScanning();
        super.onResume();
    }
    @Override
    protected void onPause() {
        mPicker.stopScanning();
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scan_bar_code);

        // Instantiate the default barcode picker
        ScanditSDKScanSettings settings = ScanditSDKScanSettings.getDefaultSettings();
        settings.enableSymbology(ScanditSDK.Symbology.QR);
        settings.enableSymbology(ScanditSDK.Symbology.GS1_DATABAR);
        settings.enableSymbology(ScanditSDK.Symbology.DATAMATRIX);

        mPicker = new
                ScanditSDKAutoAdjustingBarcodePicker(this, "VzpD8N9wSVn0GEoCl+lOQRSnCfH5FrpVEKN7GTE1vo8",settings);
        mPicker.setQrEnabled(true);
// Specify the object that will receive the callback events
        mPicker.getOverlayView().addListener(this);

        setContentView(mPicker);
        mPicker.startScanning();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_bar_code, menu);
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


/*
    @Override
    public void didScan(ScanditSDKScanSession session) {
        List<ScanditSDKCode> newlyDecoded = session.getNewlyDecodedCodes();
        // because the callback is invoked inside the thread running the barcode
        // recognition, any UI update must be posted to the UI thread.
        // In this example, we want to show the first decoded barcode in a
        // splash screen covering the full display.
        Message msg = mHandler.obtainMessage(UIHandler.SHOW_BARCODE,
                newlyDecoded.get(0));
        mHandler.sendMessage(msg);
        // pause scanning and clear the session. The scanning itself is resumed
        // when the user taps the screen.
        session.pauseScanning();
        session.clear();
    }

*/

    @Override
    public void didScanBarcode(String barcode, String symbology) {
        Log.d("Scan", barcode + "," + symbology);


        Intent resultIntent = new Intent();
        resultIntent.putExtra("SCAN_RESULT",barcode);
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
    @Override
    public void didManualSearch(String entry) {
        Log.d("Scan",entry);
        // This callback is called when you use the Scandit SDK search bar.
    }
    @Override
    public void didCancel() {
        Log.d("Scan","Cancel");
        // This callback is deprecated since Scandit SDK 3.0
    }



    /*@Override
    public void didCancel() {

    }

    @Override
    public void didScanBarcode(String s, String s2) {

    }

    @Override
    public void didManualSearch(String s) {

    }*/
}
