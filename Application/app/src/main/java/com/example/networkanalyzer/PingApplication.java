package com.example.networkanalyzer;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PingApplication {

    TextView pingTextView;

    private final Context context;

    public PingApplication(Context context, TextView pv){
        this.context = context;
        this.pingTextView = pv;
    }

    public void getLatency() {
        final List<Double> rttValues = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String pingCommand = "/system/bin/ping -c 5 192.168.153.96";
                String inputLine;
                try {
                    Process process  = Runtime.getRuntime().exec(pingCommand);
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    while ((inputLine = bufferedReader.readLine()) != null) {
                        if (inputLine.contains("time=")) {
                            int startIndex = inputLine.indexOf("time=");
                            int endIndex = inputLine.indexOf(" ms", startIndex);
                            String rtt = inputLine.substring(startIndex + 5, endIndex);
                            rttValues.add(Double.valueOf(rtt));
                        }
                    }
                } catch (IOException e) {
                    Log.e("NetworkAnalyzer", "Error reading ping output", e);
                }
            }
        });

        thread.start();
        try {
            thread.join(); // Aguarde o término da thread
        } catch (InterruptedException e) {
            Log.e("NetworkAnalyzer", "Thread interrupted", e);
        }

        // Calcula a média dos valores RTT
        double sum = 0;
        for (double rtt : rttValues) {
            sum += rtt;
        }
        double average = sum / rttValues.size();

        Log.d("NetworkAnalyzer", "Average Ping RTT result: " + average + " ms");
        pingTextView.setText(String.valueOf((int) average) + " ms");
    }



}
