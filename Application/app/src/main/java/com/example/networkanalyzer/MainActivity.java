package com.example.networkanalyzer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_INTERNET_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_PHONE_CALL_PERMISSION = 3;
    private static final int REQUEST_STORAGE_PERMISSION = 4;
    private static final int REQUEST_SYSTEM_BIN_PERMISSION = 5;

    private TextView uploadUdpValue;
    private TextView jitteruploadUdpValue;
    private TextView downloadUdpValue;
    private TextView jitterdownloadUdpValue;
    private TextView rsrpValue;
    private TextView rsrqValue;
    private TextView sinrValue;
    private TextView pingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        checkAndRequestPermissions();
        initializeApplications();
    }

    private void initializeViews() {
        uploadUdpValue = findViewById(R.id.uploadUdpValue);
        jitteruploadUdpValue = findViewById(R.id.jitteruploadUdpValue);
        downloadUdpValue = findViewById(R.id.downloadUdpValue);
        jitterdownloadUdpValue = findViewById(R.id.jitterdownloadUdpValue);
        rsrpValue = findViewById(R.id.rsrpValue);
        rsrqValue = findViewById(R.id.rsrqValue);
        sinrValue = findViewById(R.id.sinrValue);
        pingValue = findViewById(R.id.pingValue);
    }

    private void checkAndRequestPermissions() {
        checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION_PERMISSION);
        checkAndRequestPermission(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL_PERMISSION);
        checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
        checkAndRequestPermission(Manifest.permission.INTERNET, REQUEST_INTERNET_PERMISSION);

        // Additional permission checks or requests can be added here
    }

    private void checkAndRequestPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission not granted: " + permission);
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            Log.d(TAG, "Permission already granted: " + permission);
        }
    }

    private void initializeApplications() {

        PingApplication pingApp = new PingApplication(this, pingValue);
        pingApp.getLatency();

        // Initialize applications here
        RadioApplication radioApp = new RadioApplication(rsrpValue, rsrqValue, sinrValue);
        radioApp.updateRadioInfo(this);

        IperfApplication iperfApp = new IperfApplication(this, uploadUdpValue, jitteruploadUdpValue,
                downloadUdpValue, jitterdownloadUdpValue);
        iperfApp.runIperfClient("Upload");
    }

    private void executeShellCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("SystemBinContent", line);
            }
            process.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Handle permission results if needed
        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "Permission: " + permissions[i] + " Result: " + grantResults[i]);
        }
    }
}
