package com.varun.seatlayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AlarmReceiver2 extends BroadcastReceiver {
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;
    Context myContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        myContext=context;
        Thread resetThread = new Thread(new resetThread());
        resetThread.start();
//        try {
//            resetThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Toast.makeText(context, "Reseting the seats", Toast.LENGTH_LONG).show();
        DbHandler dbHandler = new DbHandler(context);
        int i=0;
        for (i=0;i<MainActivity.count;i++){
            if (!dbHandler.getNameById(i).equals("פנוי")){
                dbHandler.UpdateUserStatus("red",i);
                MainActivity.setPresent(i);
            }
        }
    }

    class resetThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket(MainActivity.IP, MainActivity.PORT);
                dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "reset statuses";
                String receivedMsg = "";
                dos.writeUTF(message);
                byte[] buffer = new byte[9];
                is = socket.getInputStream();
                is.read(buffer);
                receivedMsg = new String(buffer, "UTF-8");
                if (receivedMsg.equals("done")){
                    Log.d("ResetStatuses", "Success");
                    Toast.makeText(myContext, "Reset statuses", Toast.LENGTH_LONG).show();
                } else{
                    Log.d("ResetStatuses", "Error sending all messages");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
