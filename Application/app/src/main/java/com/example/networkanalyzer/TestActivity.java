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

import java.util.Locale;
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

        // exibir o AlertDialog quando o botão for clicadp
        bt_start.setOnClickListener(v -> showSaveTestDialog());

        initializeApplications();
    }

    private void initializeApplications() {

        PingApplication pingApp = new PingApplication(this, Ping_data);
        pingApp.getLatency();

        // Initialize applications here
        RadioApplication radioApp = new RadioApplication(RSRP_data, RSRQ_data, SNR_data);
        radioApp.updateRadioInfo(this);


        VideoApllication[] videoApp = new VideoApllication[3];


        int numThreads = 3;
        VideoApllication.MyTuple[] myTupleArray = new VideoApllication.MyTuple[3];
        Thread[] threads = new Thread[numThreads];
        final double[] averageThroughput = {0};
        final double[] averageLoadTime = {0};

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Bundle bundle = msg.getData();
                    VideoApllication.MyTuple receivedTuple = (VideoApllication.MyTuple) bundle.getSerializable("myTuple");
                    // Faça o que for necessário com a receivedTuple
                    averageThroughput[0] +=receivedTuple.getDownloadValue();
                    averageLoadTime[0] +=receivedTuple.getLoadTimeValue();
                    Average_Output.setText(String.format(Locale.US, "%.2f Mbps", averageThroughput[0]/3));
                    Average_Loadtime.setText(String.format(Locale.US, "%.2f s", averageLoadTime[0] / 3));


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