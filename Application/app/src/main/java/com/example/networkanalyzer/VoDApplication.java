package com.example.networkanalyzer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class VoDApplication {

    private final Context context;
    private final VideoView videoView;
    private final TextView downloadValueTextView;
    private final TextView tempoDeCarregamentoValueTextView;
    private final String videoUrl;
    private final Handler progressHandler = new Handler();
    private int currentChunkIndex = 0;
    private boolean isNextChunkDownloading = false;
    private File nextChunkFile = null;


    public VoDApplication(Context context, VideoView videoView,
                                TextView downloadView, TextView tempoView,
                                String serverIp, String quality) {
        this.context = context;
        this.videoView = videoView;
        this.downloadValueTextView = downloadView;
        this.tempoDeCarregamentoValueTextView = tempoView;
        this.videoUrl = "http://" + serverIp + ":3001/vod/" + quality+"/chunks/";
    }

    public void start() {
        downloadAndPlayChunk(currentChunkIndex);
    }

    private void downloadAndPlayChunk(int chunkIndex) {
        new Thread(() -> {
            try {
                    Log.d("VoD", "Baixando chunk " + chunkIndex);

                long startTime = System.currentTimeMillis();
                File chunkFile = downloadChunk(chunkIndex);
                long endTime = System.currentTimeMillis();

                double downloadTimeSec = (endTime - startTime) / 1000.0;
                double chunkSizeMB = chunkFile.length() / (1024.0 * 1024.0);
                double bandwidth = chunkSizeMB * 8 / downloadTimeSec; // Mbps

                Log.d("VoD", String.format("Chunk %d pronto. %.2f Mbps, %.2f s", chunkIndex, bandwidth, downloadTimeSec));

                int finalChunkIndex = chunkIndex;
                ((Activity) context).runOnUiThread(() -> {
                    downloadValueTextView.setText(String.format(Locale.US, "%.2f Mbps", bandwidth));
                    tempoDeCarregamentoValueTextView.setText(String.format(Locale.US, "%.2f s", downloadTimeSec));

                    videoView.setVideoPath(chunkFile.getAbsolutePath());
                    videoView.start();

                    monitorPlaybackProgress(finalChunkIndex);
                });

            } catch (Exception e) {
                Log.e("VoD", "Erro ao baixar ou tocar chunk", e);
            }
        }).start();
    }

    private File downloadChunk(int next_chunkIndex) throws IOException {
        String final_video_vod_url = videoUrl+String.valueOf(next_chunkIndex);
        HttpURLConnection connection = (HttpURLConnection) new URL(final_video_vod_url).openConnection();
        connection.connect();

        File chunk_file = new File(context.getCacheDir(), "chunk_" + next_chunkIndex + ".mp4");
        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(chunk_file)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            connection.disconnect();
            return chunk_file;
        }
    }

    private void monitorPlaybackProgress(int chunkIndex) {
        isNextChunkDownloading = false;

        videoView.setOnCompletionListener(mp -> {
            currentChunkIndex++;
            if (nextChunkFile != null && nextChunkFile.exists()) {
                playChunk(nextChunkFile);
            } else {
                downloadAndPlayChunk(currentChunkIndex);
            }
        });

        progressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!videoView.isPlaying()) {
                    progressHandler.postDelayed(this, 500);
                    return;
                }

                int duration = videoView.getDuration();
                int current = videoView.getCurrentPosition();

                if (duration > 0 && current >= (duration / 2) && !isNextChunkDownloading) {
                    isNextChunkDownloading = true;

                    // ✅ Pré-download em nova thread
                    new Thread(() -> {
                        try {
                            nextChunkFile = downloadChunk(currentChunkIndex + 1);
                            Log.d("VoD", "Pré-download do chunk " + (currentChunkIndex + 1) + " concluído.");
                        } catch (IOException e) {
                            Log.e("VoD", "Erro no pré-download", e);
                            nextChunkFile = null;
                        }
                    }).start();
                }

                if (videoView.isPlaying()) {
                    progressHandler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    private void playChunk(File chunkFile) {
        ((Activity) context).runOnUiThread(() -> {
            videoView.setVideoPath(chunkFile.getAbsolutePath());
            videoView.start();
            monitorPlaybackProgress(currentChunkIndex);  // continuar monitorando
        });
    }


}
