package com.example.networkanalyzer;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_INTERNET_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_PHONE_CALL_PERMISSION = 3;
    private static final int REQUEST_STORAGE_PERMISSION = 4;
    private static final int REQUEST_SYSTEM_BIN_PERMISSION = 5;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String radio_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermissions();

        setContentView(R.layout.activity_main);

        Button bt_start = findViewById(R.id.bt_start);

        // Configurar o primeiro Spinner
        configureSpinner(R.id.samples_number, R.array.array_amostras);

        // Inicialize o RadioGroup
        radioGroup = findViewById(R.id.radioGroup);

        // O listener para verificar qual RadioButton está selecionado:
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Verifica qual RadioButton está selecionado
                radioButton = findViewById(checkedId);
                Toast.makeText(getApplicationContext(), "Selecionado: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                radio_value = radioButton.getText().toString();
                Log.d("RADIO", "radio selected " + radio_value);

            }
        });



        //vai para a tela de teste
        bt_start.setOnClickListener(v -> goToTestWindow());
    }

    private void checkAndRequestPermissions() {
        checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION_PERMISSION);
        checkAndRequestPermission(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL_PERMISSION);
        checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
        checkAndRequestPermission(Manifest.permission.INTERNET, REQUEST_INTERNET_PERMISSION);
    }

    private void checkAndRequestPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission not granted: " + permission);
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            Log.d(TAG, "Permission already granted: " + permission);
        }
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
        TextView server_ip = findViewById(R.id.server_ip);
        StorageClass.server_ip_Value = server_ip.getText().toString();

        TextView csv_name = findViewById(R.id.csv_name);
        StorageClass.csv_name_Value = csv_name.getText().toString();

        Spinner samples_number = findViewById(R.id.samples_number);
        StorageClass.samples_number_Value = samples_number.getSelectedItem().toString();

        StorageClass.quality_video_value = radio_value;
        Intent testWindowIntent = new Intent(this, com.example.networkanalyzer.TestActivity.class);
        startActivity(testWindowIntent);
    }
}