package com.example.networkanalyzer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class IperfApplication {
    private final Context context;
    private TextView uploadTcpValue;
    private TextView uploadUdpValue;
    private TextView jitteruploadUdpValue;
    private TextView downloadTcpValue;
    private TextView downloadUdpValue;
    private TextView jitterdownloadUdpValue;



    public IperfApplication(Context context, TextView uploadTcpValue, TextView uploadUdpValue, TextView jitteruploadUdpValue,
                            TextView downloadTcpValue, TextView downloadUdpValue, TextView jitterdownloadUdpValue) {
        this.uploadTcpValue = uploadTcpValue;
        this.uploadUdpValue = uploadUdpValue;
        this.jitteruploadUdpValue = jitteruploadUdpValue;
        this.downloadTcpValue = downloadTcpValue;
        this.downloadUdpValue = downloadUdpValue;
        this.jitterdownloadUdpValue = jitterdownloadUdpValue;
        // Instale o Iperf no construtor

        this.context = context; // Inicialize o contexto
//        installIperf(context);
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

        //String localIpAddress = getLocalIpAddress();

        //Log.d("IperfApplication", "Local IP Address: " + localIpAddress);

        // Comando para executar o cliente UDP do Iperf
        String udpCommand = "iperf3 -c 192.168.70.135 -u -t 10";

//        // Comando para executar o cliente TCP do Iperf
//        String tcpCommand = "iperf3 -c 172.27.9.47 -t 10 -p 5002";

        try {
            // Executa o cliente UDP e captura a saída
            Log.d("Running Iperf", "Initialing ...");

            // Crie um processo para executar o Iperf3
            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", udpCommand);
            Process udpProcess = processBuilder.start();
            BufferedReader udpReader = new BufferedReader(new InputStreamReader(udpProcess.getInputStream()));
            String udpLine;
            while ((udpLine = udpReader.readLine()) != null) {
                Log.d("Iperf Client", udpLine);
                // Atualiza o TextView para mostrar a saída do teste UDP
                uploadUdpValue.setText(udpLine);
            }
            udpReader.close();
            udpProcess.waitFor();

//            // Executa o cliente TCP e captura a saída
//            Process tcpProcess = Runtime.getRuntime().exec(tcpCommand);
//            BufferedReader tcpReader = new BufferedReader(new InputStreamReader(tcpProcess.getInputStream()));
//            String tcpLine;
//            while ((tcpLine = tcpReader.readLine()) != null) {
//                System.out.println(tcpLine);
//                // Atualiza o TextView para mostrar a saída do teste TCP
//                uploadTcpValue.setText(tcpLine);
//            }
//            tcpReader.close();
//            tcpProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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

