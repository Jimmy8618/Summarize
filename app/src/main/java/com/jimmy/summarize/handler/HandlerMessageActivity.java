package com.jimmy.summarize.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jimmy.summarize.R;
import com.jimmy.summarize.message.HandlerMessage;

public class HandlerMessageActivity extends Activity implements View.OnClickListener{

    private TextView statusTextView;
    private Button btnDownload;

    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    statusTextView.setText("文件下载完成!");
                    System.out.println("handleMessage thread id " + Thread.currentThread().getId());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handler_message_activity);
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
                Message message = new Message();
//                Message message1 = Message.obtain();
                message.what = 1;
//                message.arg1
                uiHandler.sendMessage(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
