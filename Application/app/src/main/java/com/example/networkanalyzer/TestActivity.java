package com.example.networkanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

public class TestActivity extends AppCompatActivity {

    TextView RSRP_data, RSRQ_data, SNR_data, Ping_data, Download_data, Upload_data, Jitter_data, Jitter_data2;
    TextView Vazao1_data, Loadtime1_data, Vazao2_data, Loadtime2_data, Vazao3_data, Loadtime3_data;
    TextView Avarage_Output, Avarage_Loadtime;
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
        Avarage_Output = findViewById(R.id.Avarage_Output);
        Avarage_Loadtime = findViewById(R.id.Avarage_Loadtime);
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

//        IperfApplication iperfApp = new IperfApplication(this, Upload_data, Jitter_data,
//                Download_data, Jitter_data2);
//        iperfApp.runIperfClient("Upload");
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