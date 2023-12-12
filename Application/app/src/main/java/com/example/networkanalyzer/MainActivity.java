package com.example.networkanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView server_ip = findViewById(R.id.server_ip);
        TextView csv_name = findViewById(R.id.csv_name);
        Spinner samples_number = findViewById(R.id.samples_number);
        Spinner stream_number = findViewById(R.id.stream_number);
        TextView quality_switch = findViewById(R.id.quality_switch);
        Button bt_start = findViewById(R.id.bt_start);


        // Configurar o primeiro Spinner
        configureSpinner(R.id.samples_number, R.array.array_amostras);

        // Configurar o segundo Spinner
        configureSpinner(R.id.stream_number, R.array.array_stream);

        //vai para a tela de teste
        bt_start.setOnClickListener(v -> goToTestWindow());
    }

    private void configureSpinner(int spinnerId, int arrayResourceId) {
        Spinner spinner = findViewById(spinnerId);

        // Array de strings para o Spinner
        String[] items = getResources().getStringArray(arrayResourceId);

        // Adaptador para o Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Configurar adaptador para o Spinner
        spinner.setAdapter(adapter);

        // Adicionar ouvinte de item selecionado para o Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Código a ser executado quando um item é selecionado
                String selectedValue = parentView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Selecionado: " + selectedValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Código a ser executado quando nenhum item é selecionado
            }
        });
    }

    private void goToTestWindow() {
        Intent testWindowIntent = new Intent(this, com.example.networkanalyzer.TestActivity.class);
        startActivity(testWindowIntent);
    }
}