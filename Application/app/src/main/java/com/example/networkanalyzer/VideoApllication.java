package com.example.networkanalyzer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class VideoApllication {
    private VideoView videoView1;
    private Context context;
    private TextView downloadValueTextView;
    private TextView tempoDeCarregamentoValueTextView;
    private String videoUrl;
    private Handler handler;

    public String getVazao(){
        return downloadValueTextView.getText().toString();
    }
    public String getLoadtime(){
        return tempoDeCarregamentoValueTextView.getText().toString();
    }
    public VideoApllication(Context c, VideoView vView1, TextView downValueText, TextView timeDownText, String quality_video, Handler handler){
        this.context = c;
        this.videoView1 = vView1;
        this.downloadValueTextView = downValueText;
        this.tempoDeCarregamentoValueTextView = timeDownText;
        this.handler = handler;
        this.videoUrl = "http://192.168.1.140:3001/video/"+quality_video;
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

    public class MyTuple implements Serializable {
        public final double value1;
        public final double value2;

        public MyTuple(double value1, double value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public double getDownloadValue() {
            return value1;
        }

        public double getLoadTimeValue() {
            return value2;

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
            MyTuple tupla = new MyTuple(downloadValor,tempoDeCarregamentoValor);

            sendTupleToMainThread(tupla);
            String downloadValorString = String.valueOf(downloadValor);
            //Log.d("TAG", "Valor de Download: " + downloadValorString);
            Log.d("NetworkAnalyzer", "Fetched the Video");


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Código relacionado à UI (VideoView, TextViews, etc.) deve estar aqui

                    // Exemplo:
                    downloadValueTextView.setText(String.format(Locale.US, "%.2f Mbps", bandwidth));
                    tempoDeCarregamentoValueTextView.setText(String.format(Locale.US, "%.2f s", downloadDuration / 1000.0));
                    videoView1.setVideoPath(videoFile.getAbsolutePath());
                    videoView1.start();
                }
            });

        } catch (Exception e) {
            Log.e("NetworkAnalyzer", "Error", e);
        }
    }
    private void sendTupleToMainThread(MyTuple tuple) {
        Message message = Message.obtain();
        message.what = 1; // Identificador da mensagem

        Bundle bundle = new Bundle();
        bundle.putSerializable("myTuple", tuple);
        message.setData(bundle);

        this.handler.sendMessage(message);
    }
}
