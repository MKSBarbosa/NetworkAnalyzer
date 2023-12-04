package com.example.tela;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

        // Restante do seu código...

        // Adicione este trecho para exibir o AlertDialog quando o botão for clicado
        Button bt_start = findViewById(R.id.bt_stop);
        bt_start.setOnClickListener(v -> showSaveTestDialog());
    }

    private void showSaveTestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Você gostaria de salvar o teste?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Lógica para salvar o teste aqui
                        Toast.makeText(TestActivity.this, "Teste salvo!", Toast.LENGTH_SHORT).show();
                        // Ir para ResultActivity
                        Intent resultIntent = new Intent(TestActivity.this, ResultActivity.class);
                        startActivity(resultIntent);

                        // Encerrar a TestActivity
                        finish();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Lógica para lidar com o "Não" aqui
                        Toast.makeText(TestActivity.this, "Teste não salvo.", Toast.LENGTH_SHORT).show();
                        // Voltar para MainActivity
                        Intent mainIntent = new Intent(TestActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        // Encerrar a TestActivity
                        finish();
                    }
                });

        // Crie e exiba o AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}