package com.example.networkanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

public class TestActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        TextView RSRP_data = findViewById(R.id.RSRP_data);
        TextView RSRQ_data = findViewById(R.id.RSRQ_data);
        TextView SNR_data = findViewById(R.id.SNR_data);
        TextView Ping_data = findViewById(R.id.Ping_data);
        TextView Download_data = findViewById(R.id.Download_data);
        TextView Upload_data = findViewById(R.id.Upload_data);
        TextView Jitter_data = findViewById(R.id.Jitter_data);
        TextView Vazao1_data = findViewById(R.id.Vazao1_data);
        TextView Loadtime1_data = findViewById(R.id.Loadtime1_data);
        TextView Vazao2_data = findViewById(R.id.Vazao2_data);
        TextView Loadtime2_data = findViewById(R.id.Loadtime2_data);
        // exibir quando o botão for clicado
        Button bt_start = findViewById(R.id.bt_stop);
        bt_start.setOnClickListener(v -> showSaveTestDialog());
    }

    private void showSaveTestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Você gostaria de salvar o teste?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Lógica para salvar o teste aqui
                        Toast.makeText(TestActivity2.this, "Teste salvo!", Toast.LENGTH_SHORT).show();
                        // Ir para ResultActivity
                        Intent resultIntent = new Intent(TestActivity2.this, ResultActivity.class);
                        startActivity(resultIntent);

                        // Encerrar a TestActivity2
                        finish();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Lógica para lidar com o "Não" aqui
                        Toast.makeText(TestActivity2.this, "Teste não salvo.", Toast.LENGTH_SHORT).show();
                        // Voltar para MainActivity
                        Intent mainIntent = new Intent(TestActivity2.this, MainActivity.class);
                        startActivity(mainIntent);
                        // Encerrar a TestActivity2
                        finish();
                    }
                });

        // Crie e exiba o AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}