package com.example.networkanalyzer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SpeedTestApplication {

    private final TextView downloadResultTextView;
    private final TextView uploadResultTextView;
    private final TextView jitterResultTextView;
    private final TextView delayResultTextView;

    private String serverIp;

    public SpeedTestApplication(TextView downloadResultTextView, TextView uploadResultTextView,
                                TextView jitterResultTextView, TextView delayResultTextView, String serverIp) {
        this.downloadResultTextView = downloadResultTextView;
        this.uploadResultTextView = uploadResultTextView;
        this.jitterResultTextView = jitterResultTextView;
        this.delayResultTextView = delayResultTextView;
        this.serverIp = serverIp;
    }

    public void runSpeedTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("172.27.9.20", 8080); // Substitua pelo endereço IP do seu servidor Python
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    StringBuilder resultStringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        resultStringBuilder.append(line).append("\n");
                    }

                    final String[] results = resultStringBuilder.toString().split("\\n");

                    Log.d("Recieving Data", "Received: " + resultStringBuilder.toString());

                    // Atualizar a UI a partir da thread principal usando Handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            updateTextViews(results);
                        }
                    });

                    socket.close();
                    reader.close();
                } catch (IOException e) {
                    Log.e("SpeedTestApplication", "Error during data retrieval", e);
                    // Lide com o erro se necessário
                }
            }
        }).start();
    }

    private void updateTextViews(String[] results) {
        if (results.length == 4) {
            downloadResultTextView.setText(results[0]);
            uploadResultTextView.setText(results[1]);
            jitterResultTextView.setText(results[2]);
            delayResultTextView.setText(results[3]);
            for (int i= 0; i < 4; i++) {
                Log.d("Recieving Data", "results: " + results[i]);
            }
        } else {
            downloadResultTextView.setText("Error: Invalid result format");
        }
    }
}
