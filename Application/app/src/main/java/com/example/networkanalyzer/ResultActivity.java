package com.example.networkanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);



        Button btnBackToMain = findViewById(R.id.bt_finish);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar uma Intent para ir para a MainActivity
                Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(mainIntent);

                // Encerrar a ResultActivity
                finish();
            }
        });
    }
}