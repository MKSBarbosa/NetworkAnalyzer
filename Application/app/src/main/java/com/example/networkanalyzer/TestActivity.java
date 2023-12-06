package com.example.networkanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        /*
        RSRPValue = findViewById(R.id.RSRP_data);
        RSRQValue = findViewById(R.id.RSRQ_data);
        SNRValue = findViewById(R.id.SNR_data);
        PingValue = findViewById(R.id.Ping_data);
        DownloadValue = findViewById(R.id.Download_data);
        UploadValue = findViewById(R.id.Upload_data);
        JitterValue = findViewById(R.id.Jitter_data);
        StreamOutput1 = findViewById(R.id.Vazao1_data);
        StreamLoadtime1 = findViewById(R.id.Loadtime1_data);
        StreamOutput2 = findViewById(R.id.Vazao2_data);
        StreamLoadtime2 = findViewById(R.id.Loadtime2_data);
        StreamOutput3 = findViewById(R.id.Vazao3_data);
        StreamLoadtime3 = findViewById(R.id.Loadtime3_data);
        AvarageOutput = findViewById(R.id.Avarage_Output);
        AvarageLoadtime = findViewById(R.id.Avarage_Loadtime);*/
        Button bt_start = findViewById(R.id.bt_stop);
        // exibir o AlertDialog quando o botão for clicadp
        bt_start.setOnClickListener(v -> showSaveTestDialog());
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