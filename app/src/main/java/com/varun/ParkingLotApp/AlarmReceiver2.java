package com.varun.ParkingLotApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Calendar;

public class AlarmReceiver2 extends BroadcastReceiver {
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;
    Context myContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        myContext=context;
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int day = date.get(Calendar.DAY_OF_WEEK);
        if (day==4 && hour==11 && MainActivity.first3==0) {
            Toast.makeText(context, "Reseting the seats", Toast.LENGTH_LONG).show();
            DbHandler dbHandler = new DbHandler(context);
            int i = 0;
            String name;
            for (i = 1; i <= MainActivity.count; i++) {
                name = dbHandler.getNameById(i);
                if (!name.equals("פנוי")) {
                    dbHandler.UpdateUserStatus("red", i);
                    MainActivity.setPresent(i);
                }
            }
            MainActivity.first3=1;
            MainActivity.first1=0;
            MainActivity.first2=0;
        }
    }
}
