package com.varun.seatlayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;

public class AlarmReceiver1 extends BroadcastReceiver {
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;
    Context myContext;
    @Override
    public void onReceive(Context context, Intent intent) { // Sending sms to all users
        myContext=context;
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int day = date.get(Calendar.DAY_OF_WEEK);
        if (day==5 && hour==18 && MainActivity.first2==0) {
            //Telling the server to send all sms messages
            Thread sendThread = new Thread(new sendThread());
            sendThread.start();
            MainActivity.first2=1;
            MainActivity.first3=0;
        }


////        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
////        {
////            setAlarm(context);
////        }
//        DbHandler dbHandler = new DbHandler(context);
////        String msg = "האם תגיע השבת לבית הכנסת? כן/לא";
//        String data = dbHandler.getPhoneNumbers();
//        String[] phoneNumbers = data.split("\n");
//        LinkedHashSet<String> lhSetColors =
//                new LinkedHashSet<String>(Arrays.asList(phoneNumbers)); // removes duplicate numbers
//        //create array from the LinkedHashSet
//        String[] noDuplicatePN = lhSetColors.toArray(new String[ lhSetColors.size() ]);
//        String seatIds, msg;
//        String[] splitSeatIds;
////        for(int i=0; i<noDuplicatePN.length;i++){
////            seatIds = dbHandler.seatIdFromPN(noDuplicatePN[i]);
////            splitSeatIds = seatIds.split("\n");
//////            msg = "הודעה מהגבאים : אנא השב בהודעה חוזרת את מספר המקומות הפנויים שלך לשבת הקרובה (מקסימום: " + splitSeatIds.length + ")";
////            msg = "זוהי הודעה מגבאי בית הכנסת : נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה (מקסימום: " + splitSeatIds.length + ")";
////            MainActivity.smsSendMessage(noDuplicatePN[i], msg);
////        }
//        seatIds = dbHandler.seatIdFromPN("+972549766158");
//        splitSeatIds = seatIds.split("\n");
//        msg = "זוהי הודעה מגבאי בית הכנסת : נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה (מקסימום: " + splitSeatIds.length + ")";
//        MainActivity.smsSendMessage("+972549766158", msg);
//        Toast.makeText(context, "Sent message to " + noDuplicatePN.length + " numbers.", Toast.LENGTH_LONG).show();
////        Toast.makeText(context, "Length: " + noDuplicatePN.length, Toast.LENGTH_LONG).show();
    }

    public void setAlarm(Context context)
    {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.DAY_OF_WEEK, 5);
        c1.set(Calendar.HOUR_OF_DAY, 18);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver1.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), 7* 24 * 60 * 60 * 1000, pi); // Millisec * Second * Minute
    }

    class sendThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket(MainActivity.IP, MainActivity.PORT);
                dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "send all";
                String receivedMsg = "";
                dos.writeUTF(message);
                byte[] buffer = new byte[9];
                is = socket.getInputStream();
                is.read(buffer);
                receivedMsg = new String(buffer, "UTF-8");
                if (receivedMsg.equals("done")) {
                    Log.d("SendAll", "Success");
                    Toast.makeText(myContext, "Sent messages", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("SendAll", "Error sending all messages");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
