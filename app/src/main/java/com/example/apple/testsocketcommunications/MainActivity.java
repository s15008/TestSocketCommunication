package com.example.apple.testsocketcommunications;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new TcpServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        findViewById(R.id.btn_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TcpClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private static class TcpClientTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "TcpClientTask";

        @Override
        protected String doInBackground(Void... voids) {
            BufferedReader is = null;
            PrintStream os = null;

            String message = "Hello";

            try {
                Socket socket = new Socket("localhost", 8080);

                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintStream(socket.getOutputStream());

                os.write(message.getBytes());

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            Log.d(TAG, "message : " + message);
        }
    }

    /**
     * TcpServerTask
     * TCPサーバのクラス
     */
    private static class TcpServerTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "TcpServerTask";

        @Override
        protected Void doInBackground(Void... voids) {
            BufferedReader is = null;
            PrintStream os = null;
            try {
                ServerSocket listener = new ServerSocket();
                listener.bind(new InetSocketAddress(8080));
                String address = new String(listener.getInetAddress().getAddress(), "UTF-8");
                Log.d(TAG, "Server listening on port 8080 " + "for " + address);

                while (true) {
                    Socket socket = listener.accept();
                    is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    os = new PrintStream(socket.getOutputStream());

                    String line = is.readLine();
                    os.println("Re: " + line);

                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                if (os != null) {
                    os.close();
                }

            }
            return null;
        }
    }
}
