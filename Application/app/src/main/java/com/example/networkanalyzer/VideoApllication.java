package com.example.networkanalyzer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoApllication {
    private VideoView videoView1;
    private Context context;
    private TextView downloadValueTextView;
    private TextView tempoDeCarregamentoValueTextView;
    private String videoUrl;

    public VideoApllication(Context c, VideoView vView1){
        this.context = c;
        this.videoView1 = vView1;
//        this.downloadValueTextView = downValueText;
//        this.tempoDeCarregamentoValueTextView = timeDownText;
        this.videoUrl = "http://192.168.1.144:3001/video";
    }


    private File downloadVideo(String videoUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(videoUrl).openConnection();
        connection.connect();

        File videoFile = new File(context.getCacheDir(), "downloaded_video.mp4");
        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(videoFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            return videoFile;
        }
    }


    public void fetchAndDisplayVideo() {
        try {
            Log.d("NetworkAnalyzer", "Fetching the Video");


            long startTime = System.currentTimeMillis();
            File videoFile = downloadVideo(this.videoUrl);
            long endTime = System.currentTimeMillis();

            long downloadDuration = endTime - startTime; // tempo em milissegundos
            double videoSizeMB = videoFile.length() / (1024.0 * 1024.0); // tamanho em MB
            double bandwidth = videoSizeMB * 8 / (downloadDuration / 1000.0); // Mbps

            double downloadValor = bandwidth;
            double tempoDeCarregamentoValor = downloadDuration / 1000.0;

            Log.d("NetworkAnalyzer", "Fetched the Video");

//            this.downloadValueTextView.setText(String.format(Locale.US, "%.2f Mbps", bandwidth));
//            this.tempoDeCarregamentoValueTextView.setText(String.format(Locale.US, "%.2f s", downloadDuration / 1000.0));
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Código relacionado à UI (VideoView, TextViews, etc.) deve estar aqui

                    // Exemplo:
                    videoView1.setVideoPath(videoFile.getAbsolutePath());
                    videoView1.start();
                }
            });

        } catch (Exception e) {
            Log.e("NetworkAnalyzer", "Error", e);
        }
    }
}
