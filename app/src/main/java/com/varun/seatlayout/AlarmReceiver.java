package com.varun.seatlayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AlarmReceiver extends BroadcastReceiver {
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;
    Context myContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        myContext=context;
        Thread getStatusesThread = new Thread(new getStatusesThread());
        getStatusesThread.start();
        Toast.makeText(context, "Updating seat statuses", Toast.LENGTH_LONG).show();
//        try {
//            getStatusesThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Toast.makeText(context, "Updating seats from DB", Toast.LENGTH_LONG).show();
//        DbHandler dbHandler = new DbHandler(context);
//        String status;
//        // Get all the new data from the DataBase and update the list
//        for(int i=0;i<MainActivity.count;i++){
//            status = dbHandler.statusFromSeatId(Integer.toString(i));
//            if (status.equals("green")){
//                MainActivity.setMissing(i);
//            }
//        }
    }

    class getStatusesThread implements Runnable {

        @Override
        public void run() {
            try {
                DbHandler dbHandler = new DbHandler(myContext);
                socket = new Socket(MainActivity.IP, MainActivity.PORT);
                dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "get statuses";
                String receivedMsg = "";
                dos.writeUTF(message);
                dos.flush();
                dos = new DataOutputStream(socket.getOutputStream());
                byte[] bufferSize = new byte[2];
                is = socket.getInputStream();
                is.read(bufferSize);
                receivedMsg = new String(bufferSize, "UTF-8");
                byte[] buffer = new byte[Integer.parseInt(receivedMsg)];
                message = "received num";
                dos.writeUTF(message);
                dos.flush();
                is = socket.getInputStream();
                is.read(buffer);
                receivedMsg = new String(buffer, "UTF-8");
                if (receivedMsg!=null){
                    String[] statuses = receivedMsg.split(" ");
                    Log.d("getStatuses", "Received statuses");
                    for(int i=0;i<statuses.length;i++) {
                        if (statuses[i] != "" && statuses[i] != null) {
                            dbHandler.UpdateUserStatus("green", Integer.parseInt(statuses[i]));
                            MainActivity.setMissing(Integer.parseInt(statuses[i]));
                        }
                    }
                } else{
                    Log.d("getStatuses", "Error Receiving statuses");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
