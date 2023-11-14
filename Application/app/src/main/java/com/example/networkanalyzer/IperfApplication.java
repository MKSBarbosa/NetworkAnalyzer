package com.example.networkanalyzer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class IperfApplication {
    private final Context context;
    private TextView uploadUdpValue;
    private TextView jitteruploadUdpValue;
    private TextView downloadUdpValue;
    private TextView jitterdownloadUdpValue;

    public IperfApplication(Context context, TextView uploadUdpValue, TextView jitteruploadUdpValue,
                            TextView downloadUdpValue, TextView jitterdownloadUdpValue) {
        this.uploadUdpValue = uploadUdpValue;
        this.jitteruploadUdpValue = jitteruploadUdpValue;
        this.downloadUdpValue = downloadUdpValue;
        this.jitterdownloadUdpValue = jitterdownloadUdpValue;
        this.context = context;
    }

//    private void installIperf(Context context) {
//            // Instala o Iperf
//            try {
//                ProcessBuilder processBuilder = new ProcessBuilder("su", "-c", "chmod 777 " + getFilesDir().getAbsolutePath() + "/iperf");
//                Process chmodProcess = processBuilder.start();
//                chmodProcess.waitFor();
//
//                processBuilder = new ProcessBuilder("su", "-c", getFilesDir().getAbsolutePath() + "/iperf -c 192.168.1.5 -t 10 -p 5001");
//                Process iperfProcess = processBuilder.start();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(iperfProcess.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Processar a saída do Iperf como necessário
//                }
//                iperfProcess.waitFor();
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

//    private boolean executeCommand(String... command) {
//        try {
//            Process process = Runtime.getRuntime().exec(command);
//            process.waitFor();
//            return true;
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public void runIperfClient() {
        final List<Double> iperfValues = new ArrayList<>();
        // Comando para executar o cliente UDP do Iperf
        String udpCommand = "/system/bin/iperf -c 192.168.70.135 -u -t 10 -b 100M -i 1";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Executa o cliente UDP e captura a saída
                    Log.d("Running Iperf", "Initialing ...");
                    // Crie um processo para executar o Iperf3
                    Process udpProcess = Runtime.getRuntime().exec(udpCommand);
                    BufferedReader udpReader = new BufferedReader(new InputStreamReader(udpProcess.getInputStream()));
                    String udpLine;
                    while ((udpLine = udpReader.readLine()) != null) {
                        Log.d("Iperf Client", udpLine);
                        if(udpLine.contains("Mbits/sec")){
                            int startIndex = udpLine.indexOf("Bytes");
                            int endIndex = udpLine.indexOf("Mbits/sec");
                            String bandwidth = udpLine.substring(startIndex + 7, endIndex);
                            Log.d("Iperf Client", "Bandwidth: "+ bandwidth);
                            iperfValues.add(Double.valueOf(bandwidth));
                        }
                    }
                    udpReader.close();
                    udpProcess.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join(); // Aguarde o término da thread
        } catch (InterruptedException e) {
            Log.e("NetworkAnalyzer", "Thread interrupted", e);
        }
        DecimalFormat formato = new DecimalFormat("#.###");

        // Calcula a média dos valores iPerf
        double sum = 0;
        for (double upload : iperfValues) {
            sum += upload;
        }
        double average = sum / iperfValues.size();
        String numeroFormatado = formato.format(average);

        Log.d("NetworkAnalyzer", "Average Upload result: " + average + " Mbps");
        uploadUdpValue.setText(numeroFormatado + " Mbps");
}
    public void runIperfServer() {
        String localIpAddress = getLocalIpAddress();
        Log.d("IperfApplication", "Local IP Address: " + localIpAddress);

        // Comando para executar o servidor Iperf
        String serverCommand = "iperf -s";

        try {
            // Executa o servidor Iperf e captura a saída
            Process serverProcess = Runtime.getRuntime().exec(serverCommand);
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
            String serverLine;
            while ((serverLine = serverReader.readLine()) != null) {
                System.out.println(serverLine);
                // Atualiza o TextView para mostrar a saída do servidor Iperf
                // Lógica para atualizar as TextViews do servidor aqui
            }
            serverReader.close();
            serverProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("IperfApplication", "Error getting local IP address: " + e.getMessage());
        }
        return null;
    }
}

