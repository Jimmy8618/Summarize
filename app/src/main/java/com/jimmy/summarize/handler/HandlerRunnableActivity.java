package com.jimmy.summarize.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jimmy.summarize.R;

/**
 * uiHandler.post(runnable);
 */
public class HandlerRunnableActivity extends Activity implements View.OnClickListener{
    private TextView statusTextView;
    private Button btnDownload;
    private Handler uiHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handler_runnable_activity);
        statusTextView = findViewById(R.id.tv_status);
        btnDownload = findViewById(R.id.bt_download);
        btnDownload.setOnClickListener(this);
        System.out.println("Main thread id " + Thread.currentThread().getId());
    }

    @Override
    public void onClick(View view) {
        DownloadThread downloadThread = new DownloadThread();
        downloadThread.start();
    }

    class DownloadThread extends Thread{
        @Override
        public void run() {
            try {
                System.out.println("DownloadThread id " + Thread.currentThread().getId());
                System.out.println("开始下载文件");
                sleep(5000);
                System.out.println("完成下载文件");
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Runnable Thread id " + Thread.currentThread().getId());
                        HandlerRunnableActivity.this.statusTextView.setText("文件下载完成!");
                    }
                };
                uiHandler.post(runnable);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
