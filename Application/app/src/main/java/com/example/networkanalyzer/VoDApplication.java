package com.example.networkanalyzer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Handler;

public class VoDApplication {

    private final Context context;
    private final String videoUrl;
    private final TextView downloadValueTextView;
    private final TextView tempoDeCarregamentoValueTextView;
    private final int chunkSize = 5 * 1024 * 1024; // 5 MB por chunk

    public VoDApplication(Context context, TextView downloadView, TextView loadTimeView,
                          String server_ip, String quality) {
        this.context = context;
        this.downloadValueTextView = downloadView;
        this.tempoDeCarregamentoValueTextView = loadTimeView;
        this.videoUrl = "http://" + server_ip + ":3001/vod/" + quality;
    }

    public void fetchAndDownloadInChunks() {
        new Thread(() -> {
            try {
                long totalSize = getVideoSize();
                if (totalSize <= 0) return;

                long startByte = 0;
                int chunkIndex = 0;
                double totalDownloadedMB = 0;
                long totalTime = 0;

                File outputFile = new File(context.getCacheDir(), "vod_download.mp4");
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    while (startByte < totalSize) {
                        long endByte = Math.min(startByte + chunkSize - 1, totalSize - 1);

                        long startTime = System.currentTimeMillis();
                        byte[] chunkData = downloadChunk(startByte, endByte);
                        long endTime = System.currentTimeMillis();

                        if (chunkData != null) {
                            outputStream.write(chunkData);
                            long chunkTime = endTime - startTime;

                            double chunkSizeMB = chunkData.length / (1024.0 * 1024.0);
                            double bandwidth = chunkSizeMB * 8 / (chunkTime / 1000.0); // Mbps

                            totalDownloadedMB += chunkSizeMB;
                            totalTime += chunkTime;

                            Log.d("VoD", "Chunk " + (++chunkIndex) + ": " + bandwidth + " Mbps");
                        }

                        startByte = endByte + 1;
                    }
                }

                double avgBandwidth = totalDownloadedMB * 8 / (totalTime / 1000.0); // Mbps
                double totalLoadTime = totalTime / 1000.0;

                updateUI(avgBandwidth, totalLoadTime);

            } catch (Exception e) {
                Log.e("VoD", "Erro ao baixar vídeo em chunks", e);
            }
        }).start();
    }

    private long getVideoSize() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(videoUrl).openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        long size = connection.getContentLengthLong();
        connection.disconnect();
        Log.d("VoD", "Tamanho do vídeo: " + size);
        return size;
    }

    private byte[] downloadChunk(long start, long end) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(videoUrl).openConnection();
        connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
        connection.connect();

        try (InputStream input = connection.getInputStream()) {
            byte[] buffer = new byte[(int)(end - start + 1)];
            int offset = 0;
            int read;

            while ((read = input.read(buffer, offset, buffer.length - offset)) != -1 && offset < buffer.length) {
                offset += read;
            }

            return buffer;
        }
    }

    private void updateUI(double avgBandwidth, double totalLoadTime) {
        ((Activity) context).runOnUiThread(() -> {
            downloadValueTextView.setText(String.format(Locale.US, "%.2f Mbps", avgBandwidth));
            tempoDeCarregamentoValueTextView.setText(String.format(Locale.US, "%.2f s", totalLoadTime));
        });
    }
}
