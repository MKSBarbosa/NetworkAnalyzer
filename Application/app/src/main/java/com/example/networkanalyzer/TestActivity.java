package com.example.networkanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TestActivity extends AppCompatActivity {

    TextView RSRP_data, RSRQ_data, SNR_data, Ping_data, Download_data, Upload_data, Jitter_data, Jitter_data2;
    TextView Vazao1_data, Loadtime1_data, Vazao2_data, Loadtime2_data, Vazao3_data, Loadtime3_data;
    TextView Average_Output, Average_Loadtime;
    VideoView videoView1, videoView2, videoView3;
    Button bt_start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        RSRP_data = findViewById(R.id.RSRP_data);
        RSRQ_data = findViewById(R.id.RSRQ_data);
        SNR_data = findViewById(R.id.SNR_data);
        Ping_data = findViewById(R.id.Ping_data);
        Download_data = findViewById(R.id.Download_data);
        Upload_data = findViewById(R.id.Upload_data);
        Jitter_data = findViewById(R.id.Jitter_data);
        Jitter_data2 = findViewById(R.id.Jitter_data2);
        Vazao1_data = findViewById(R.id.Vazao1_data);
        Loadtime1_data = findViewById(R.id.Loadtime1_data);
        Vazao2_data = findViewById(R.id.Vazao2_data);
        Loadtime2_data = findViewById(R.id.Loadtime2_data);
        Vazao3_data = findViewById(R.id.Vazao3_data);
        Loadtime3_data = findViewById(R.id.Loadtime3_data);
        Average_Output = findViewById(R.id.Avarage_Output);
        Average_Loadtime = findViewById(R.id.Avarage_Loadtime);
        videoView1 = findViewById(R.id.videoView1);
        videoView2 = findViewById(R.id.videoView2);
        videoView3 = findViewById(R.id.videoView3);
        bt_start = findViewById(R.id.bt_stop);

        String server_ip_Value = StorageClass.server_ip_Value;
        String csv_name_Value = StorageClass.csv_name_Value;
        String samples_number_Value = StorageClass.samples_number_Value;
        String stream_number_Value = StorageClass.stream_number_Value;
        String quality_switch_Value = StorageClass.quality_switch_Value;

        // exibir o AlertDialog quando o botão for clicadp
        bt_start.setOnClickListener(v -> showSaveTestDialog());

        initializeApplications();
    }
    private void sendCSV(Map<String, Object> dados) {
        Thread threadSend = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL do servidor Python
                    String serverURL = "http://172.27.9.239:3001/registrar_dados";

                    // Criar objeto URL com a URL do servidor
                    URL url = new URL(serverURL);

                    // Abrir conexão HTTP
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Converter os dados para JSON
                    JSONObject jsonParam = new JSONObject(dados);

                    // Enviar os dados para o servidor
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    // Verificar a resposta do servidor
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Se o envio foi bem-sucedido
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer response = new StringBuffer();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        Log.d("ServerResponse", response.toString());
                    } else {
                        // Se houve um erro no envio
                        Log.e("ServerResponse", "Erro ao enviar os dados para o servidor. Código de resposta: " + responseCode);
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Iniciar a thread para enviar os dados
        threadSend.start();
    }

    private void initializeApplications() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("id", "Marcelo");
        //['','rsrp', 'rsrq', 'snr', 'download', 'upload', 'jitterD',"jitterU", 'ping', 'vazao', 'tempoDeCarregamento']


        PingApplication pingApp = new PingApplication(this, Ping_data);
        int latency = pingApp.getLatency();
        dados.put("ping",latency);
        //dados.put("ping",10);
        //['','rsrp', 'rsrq', 'snr', 'download', 'upload', 'jitterD',"jitterU", '', 'vazao', 'tempoDeCarregamento']


        // Initialize applications here
        RadioApplication radioApp = new RadioApplication(RSRP_data, RSRQ_data, SNR_data);
        RadioApplication.nTuple radioInfo = radioApp.updateRadioInfo(this);
        dados.put("rsrp",radioInfo.getRsrp());
        dados.put("rsrq",radioInfo.getRsrq());
        dados.put("snr",radioInfo.getSnr());
        //['','', '', '', 'download', 'upload', 'jitterD',"jitterU", '', 'vazao', 'tempoDeCarregamento']

        /*IperfApplication iperfApp = new IperfApplication(this, Upload_data, Jitter_data,Download_data, Jitter_data2);
        IperfApplication.nTuple iperfUpload = iperfApp.runIperfClient("Upload");
        dados.put("upload",iperfUpload.getBitRate());
        dados.put("jitterU",iperfUpload.getJitter());*/
        dados.put("upload",0);
        dados.put("jitterU",0);
        //['','', '', '', 'download', '', 'jitterD',"", '', 'vazao', 'tempoDeCarregamento']

        /*IperfApplication.nTuple iperfDownload = iperfApp.runIperfClient("Download");
        dados.put("download",iperfDownload.getBitRate());
        dados.put("jitterD",iperfDownload.getJitter());*/
        dados.put("download",0);
        dados.put("jitterD",0);
        //['','', '', '', '', '', '','', '', 'vazao', 'tempoDeCarregamento']



        VideoApllication[] videoApp = new VideoApllication[3];
        int numThreads = 3;
        VideoApllication.MyTuple[] myTupleArray = new VideoApllication.MyTuple[3];
        Thread[] threads = new Thread[numThreads];
        final double[] averageThroughput = {0};
        final double[] averageLoadTime = {0};
        final int[] numberMedia = {0};


        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Bundle bundle = msg.getData();
                    VideoApllication.MyTuple receivedTuple = (VideoApllication.MyTuple) bundle.getSerializable("myTuple");
                    // Faça o que for necessário com a receivedTuple
                    averageThroughput[0] +=receivedTuple.getDownloadValue();
                    averageLoadTime[0] +=receivedTuple.getLoadTimeValue();
                    numberMedia[0]++;
                    Average_Output.setText(String.format(Locale.US, "%.2f Mbps", averageThroughput[0]/3));
                    Average_Loadtime.setText(String.format(Locale.US, "%.2f s", averageLoadTime[0] / 3));
                    if(numberMedia[0]==3){
                        dados.put("vazao",averageThroughput[0]/3);
                        dados.put("tempoDeCarregamento",averageLoadTime[0]/3);
                        sendCSV(dados);

                    }


                    Log.d("TupleValues", "Download: " + String.valueOf(receivedTuple.getDownloadValue()) + " Tempo de carregamento: " + String.valueOf(receivedTuple.getLoadTimeValue()));
                }
            }
        };
        videoApp[0] = new VideoApllication(this, videoView1,Vazao1_data, Loadtime1_data,handler);
        videoApp[1] = new VideoApllication(this, videoView2,Vazao2_data, Loadtime2_data, handler);
        videoApp[2] = new VideoApllication(this, videoView3,Vazao3_data, Loadtime3_data, handler);

        for(int i =0; i< numThreads; i++){
            final int index = i; // Criando uma cópia final de i

            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Realize qualquer ação que precise ser feita na thread secundária aqui
                    // Utilize a cópia final de i (index) para acessar o elemento correto do array videoApp
                    videoApp[index].fetchAndDisplayVideo();

                }
            });
            threads[i].start();
        }
    }
    private void showSaveTestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Você gostaria de salvar o teste?")
                .setPositiveButton("Sim", (dialog, id) -> {
                    // Lógica para salvar o teste aqui
                    Toast.makeText(TestActivity.this, "Teste salvo!", Toast.LENGTH_SHORT).show();
                    // Ir para ResultActivity
                    Intent resultIntent = new Intent(TestActivity.this, ResultActivity.class);
                    startActivity(resultIntent);

                    // Encerrar a TestActivity
                    finish();
                })
                .setNegativeButton("Não", (dialog, id) -> {
                    // Lógica para lidar com o "Não" aqui
                    Toast.makeText(TestActivity.this, "Teste não salvo.", Toast.LENGTH_SHORT).show();
                    // Voltar para MainActivity
                    Intent mainIntent = new Intent(TestActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    // Encerrar a TestActivity
                    finish();
                });

        // Crie e exiba o AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}