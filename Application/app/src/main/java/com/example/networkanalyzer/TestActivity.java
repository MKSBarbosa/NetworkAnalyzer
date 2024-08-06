package com.example.networkanalyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    TextView RSRP_data, RSRQ_data, SNR_data, Ping_data, Download_data, Upload_data;
    TextView round;
    TextView Vazao1_data, Loadtime1_data;
    VideoView videoView1;
    Button bt_stop;
    String server_ip_Value, csv_name_Value, samples_number_Value, quality_video_value;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initializeViews();
        retrieveStorageValues();
        LogStorageValues();

        bt_stop.setOnClickListener(v -> showSaveTestDialog());
        initializeApplications();
    }

    private void initializeViews() {
        counter = 0;

        RSRP_data = findViewById(R.id.RSRP_data);
        RSRQ_data = findViewById(R.id.RSRQ_data);
        SNR_data = findViewById(R.id.SNR_data);

        Ping_data = findViewById(R.id.Ping_data);
        Download_data = findViewById(R.id.Download_data);
        Upload_data = findViewById(R.id.Upload_data);

        Vazao1_data = findViewById(R.id.Vazao1_data);
        Loadtime1_data = findViewById(R.id.Loadtime1_data);

        videoView1 = findViewById(R.id.videoView1);
        bt_stop = findViewById(R.id.bt_stop);
        round = findViewById(R.id.counter);
    }

    private void retrieveStorageValues() {
        server_ip_Value = StorageClass.server_ip_Value;
        csv_name_Value = StorageClass.csv_name_Value;
        samples_number_Value = StorageClass.samples_number_Value;
        quality_video_value = StorageClass.quality_video_value;
    }

    private void LogStorageValues() {
        Log.d("Storage Class", "server_ip_Value: " + server_ip_Value);
        Log.d("Storage Class", "csv_name_Value: " + csv_name_Value);
        Log.d("Storage Class", "samples_number_Value: " + samples_number_Value);
        Log.d("Storage Class", "quality_video_value: " + quality_video_value);
    }

    private void sendCSV(Map<String, Object> dados) {
        new Thread(() -> {
            try {
                String serverURL = "http://" + server_ip_Value + ":3001/registrar_dados";
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject(dados);
                try (OutputStream os = conn.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(jsonParam.toString());
                    writer.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        Log.d("ServerResponse", response.toString());
                    }
                } else {
                    Log.e("ServerResponse", "Erro ao enviar os dados para o servidor. Código de resposta: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initializeApplications() {
        videoView1.stopPlayback();

        round.setText(String.valueOf(counter));
        Log.d("Initialize App", "Round: " + counter);

        Map<String, Object> dados = new HashMap<>();
        dados.put("id", csv_name_Value);

        PingApplication pingApp = new PingApplication(this, Ping_data, server_ip_Value);
        int latency = pingApp.getLatency();
        dados.put("ping", latency);

        RadioApplication radioApp = new RadioApplication(RSRP_data, RSRQ_data, SNR_data);
        RadioApplication.nTuple radioInfo = radioApp.updateRadioInfo(this);
        dados.put("rsrp", radioInfo.getRsrp());
        dados.put("rsrq", radioInfo.getRsrq());
        dados.put("snr", radioInfo.getSnr());

        dados.put("upload", 0);
        dados.put("download", 0);

        VideoApllication videoApp = new VideoApllication(this, videoView1, Vazao1_data, Loadtime1_data, quality_video_value, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    Bundle bundle = msg.getData();
                    VideoApllication.MyTuple receivedTuple = (VideoApllication.MyTuple) bundle.getSerializable("myTuple");
                    if (receivedTuple != null) {
                        double averageThroughput = receivedTuple.getDownloadValue();
                        double averageLoadTime = receivedTuple.getLoadTimeValue();
                        // Atualiza os TextViews Vazao1_data e Loadtime1_data
                        Vazao1_data.setText(String.format(Locale.US, "%.2f Mbps", averageThroughput));
                        Loadtime1_data.setText(String.format(Locale.US, "%.2f s", averageLoadTime));

                        dados.put("vazao", averageThroughput);
                        dados.put("tempoDeCarregamento", averageLoadTime);
                        sendCSV(dados);

                        int numSamples = Integer.parseInt(samples_number_Value);
                        if (counter < numSamples) {
                            counter++;
                            initializeApplications();
                        }
                    }
                }
            }
        });

        new Thread(videoApp::fetchAndDisplayVideo).start();
    }

    private void showSaveTestDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Você gostaria de salvar o teste?")
                .setPositiveButton("Sim", (dialog, id) -> {
                    Toast.makeText(TestActivity.this, "Teste salvo!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TestActivity.this, ResultActivity.class));
                    finish();
                })
                .setNegativeButton("Não", (dialog, id) -> {
                    Toast.makeText(TestActivity.this, "Teste não salvo.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TestActivity.this, MainActivity.class));
                    finish();
                })
                .create()
                .show();
    }
}
