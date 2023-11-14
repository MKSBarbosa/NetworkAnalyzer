package com.example.networkanalyzer;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_INTERNET_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private TextView uploadTcpValue;
    private TextView uploadUdpValue;
    private TextView jitteruploadUdpValue;
    private TextView downloadTcpValue;
    private TextView downloadUdpValue;
    private TextView jitterdownloadUdpValue;

    private TextView rsrpValue;

    private TextView rsrqValue;

    private TextView sinrValue;

    private TextView pingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.d("Initial", "On creating ...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialize os objetos TextView a partir do layout
        uploadTcpValue = findViewById(R.id.uploadTcpValue);
        uploadUdpValue = findViewById(R.id.uploadUdpValue);
        jitteruploadUdpValue = findViewById(R.id.jitteruploadUdpValue);
        downloadTcpValue = findViewById(R.id.downloadTcpValue);
        downloadUdpValue = findViewById(R.id.downloadUdpValue);
        jitterdownloadUdpValue = findViewById(R.id.jitterdownloadUdpValue);

        rsrpValue = findViewById(R.id.rsrpValue);
        rsrqValue = findViewById(R.id.rsrqValue);
        sinrValue = findViewById(R.id.sinrValue);

        pingValue = findViewById(R.id.pingValue);

        // Crie uma instância de IperfApplication
        IperfApplication iperfApp = new IperfApplication(this,uploadTcpValue, uploadUdpValue, jitteruploadUdpValue,
                downloadTcpValue, downloadUdpValue, jitterdownloadUdpValue);
        
        RadioApplication radioApp = new RadioApplication(rsrpValue, rsrqValue, sinrValue);
        PingApplication pingApp = new PingApplication(this, pingValue);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão ACCESS_FINE_LOCATION não foi concedida, solicite-a ao usuário
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Se a permissão foi concedida, chame a função updateRadioInfo
            radioApp.updateRadioInfo(this);
            pingApp.getLatency();
            uploadTcpValue.setText("80 Mbps");
        }

        // Verifique se a permissão de acesso à rede já foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão ainda não foi concedida, solicite-a ao usuário
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }else{
            // Execute o cliente Iperf a partir da instância de IperfApplication
            //iperfApp.runIperfClient();
            // Execute o servidor Iperf a partir da instância de IperfApplication
            //iperfApp.runIperfServer();
        }
    }
}