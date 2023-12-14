package com.example.networkanalyzer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrength;
import android.util.Log;
import android.widget.TextView;

import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class RadioApplication {

    TextView rsrpTextView;
    TextView rsrqTextView;
    TextView snrTextView;

    public RadioApplication(TextView p, TextView q, TextView s) {
        Log.d("Radio Application", "Creating object ...");
        this.rsrpTextView = p;
        this.rsrqTextView = q;
        this.snrTextView = s;
    }

    public void updateRadioInfo(Context context) {
        Log.d("Radio Application", "updateRadioInfo() called");

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
            if (cellInfos != null && !cellInfos.isEmpty()) {
                for (CellInfo cellInfo : cellInfos) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        if (cellInfo instanceof CellInfoNr) {
                            CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr) ((CellInfoNr) cellInfo).getCellSignalStrength();
                            int rsrp = cellSignalStrengthNr.getSsRsrp();
                            int rsrq = cellSignalStrengthNr.getSsRsrq();
                            int sinr = cellSignalStrengthNr.getSsSinr();
                            double snr = calculateSNR(rsrp, sinr);
                            rsrpTextView.setText(String.valueOf(rsrp) + " dBm");
                            rsrqTextView.setText(String.valueOf(rsrq) + " dB");
                            snrTextView.setText(String.valueOf(snr) + " dB");

                            Log.d("Signal Strength", "Net Type 5G | RSRP: " + String.valueOf(rsrp) + " dBm | RSRQ: " + String.valueOf(rsrq) + " dB | SNR: " + String.valueOf(snr) + " dB");
                            break;
                        }
                    } else {
                        Log.d("NetworkAnalyzer", "API level not sufficient for 5G signal strength retrieval");
                    }
                }
            } else {
                Log.d("NetworkAnalyzer", "No cell info available");
            }
        }
    }

    private double calculateSNR(int rsrp, int rsrq) {
        if (rsrq == Integer.MAX_VALUE || rsrp == Integer.MAX_VALUE) {
            return Double.NaN; // Valor inválido
        }

        // Converter valores de dBm para mW
        double rsrp_mW = Math.pow(10, rsrp / 10.0);
        double rsrq_mW = Math.pow(10, rsrq / 10.0);

        // Calcular o SNR
        double snr_mW = rsrp_mW / rsrq_mW;

        // Converter o resultado de volta para dB
        double snr = 10 * Math.log10(snr_mW);

        return snr;
    }

}



