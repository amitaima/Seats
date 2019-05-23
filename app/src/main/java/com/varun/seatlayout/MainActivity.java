package com.varun.seatlayout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static ViewGroup layout;

    String seats2 = "_UUUUUUAAAAARRRR_/"
            + "_________________/"
            + "UU__AAAARRRRR__RR/"
            + "UU__UUUAAAAAA__AA/"
            + "AA__AAAAAAAAA__AA/"
            + "AA__AARUUUURR__AA/"
            + "UU__UUUA_RRRR__AA/"
            + "AA__AAAA_RRAA__UU/"
            + "AA__AARR_UUUU__RR/"
            + "AA__UUAA_UURR__RR/"
            + "_________________/"
            + "UU_AAAAAAAUUUU_RR/"
            + "RR_AAAAAAAAAAA_AA/"
            + "AA_UUAAAAAUUUU_AA/"
            + "AA_AAAAAAUUUUU_AA/"
            + "_________________/";

    public String seats =
              "____________BBBBBBB___U___/"
            + "_______U____BBBBBBB__UUU__/"
            + "_______UU___BBBBBBB__UUU__/"
            + "______UUUA___________AUUU_/"
            + "______UUUU__AAAAAAA__AUUU_/"
            + "____________UUUUUUU_______/"
            + "AA___UUUUU__UUUUUUU__AUUUU/"
            + "AAU__UUUUA__UUUUUUU__AUUUU/"
            + "AAUU_UUUUU__MMMMMMM__AUUUU/"
            + "AAUU_UUUUA__MMMMMMM__AUUUU/"
            + "____________MMMMMMM______U/"
            + "UUUU_UUUUU__MMMMMMM__AUUUU/"
            + "UUUU_UUUUA__MMMMMMM__AUUUU/"
            + "A_UU_UUUUA__MMMMMMM__AUUUU/"
            + "AA___UUUUA___________AUUUU/"
            + "AA________________________/"
//            + "__________________________/"
            + "_________AAAAAA_AAAAAAA__U/"
            + "_________UUUUUU_UUUUUUU_U_/"
            + "__________UUUUU_UUUUUU____/"
            + "___________UUUUUUUUU______/";

    List<TextView> seatViewList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    int seatSize = 35;
    int seatGapingH = 1;
    int seatGapingV = 0;
    DbHandler dbHandler;
    static public final String IP = "192.168.43.43"; // Phone Router
    static public final int PORT = 443;
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;

    int STATUS_MISSING = 1;
    int STATUS_PRESENT = 2;
    String selectedIds = "";

    static int count, countAll, countName, countMsg;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForSmsPermission();
//        String data = dbHelper.getData();
//        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        dbHandler = new DbHandler(this);
//        SQLiteDatabase db = dbHandler.getReadableDatabase();
//        dbHandler.onUpgrade(db,1,2);
//        dbHandler.insertUserDetails("י.בוחניק", "+972544991913", "1", "red");
//        dbHandler.insertUserDetails("לוי", "+972549766158", "204");
//        dbHandler.insertUserDetails("לוי", "+972549766158", "205");
//        dbHandler.insertUserDetails("לוי", "+972549766158", "206");
//        dbHandler.insertUserDetails("מלכה", "+972549766158", "207");
//        dbHandler.insertUserDetails("מלכה", "+972549766158", "208");
//        dbHandler.insertUserDetails("מלכה", "+972549766158", "209");
//        dbHandler.insertUserDetails("מלכה", "+972549766158", "210");
//        dbHandler.insertUserDetails("בוכריס ש", "+972549766185", "211");
//        dbHandler.insertUserDetails("בוכריס ש", "+972549766185", "200");
//        dbHandler.insertUserDetails("test", "+972584966113", "202");
//        dbHandler.DeleteAll();
//        insertToDB();
//        String seatIdTest = dbHandler.searchByPN("0549766185");
//        String seatIdTest = dbHandler.seatIdFromPN("+972549766158");
//        dbHandler.UpdateUserStatus("red", 210);
        String data;
//        Toast.makeText(this, "data: " +data, Toast.LENGTH_LONG).show();
        data = dbHandler.getData();
        if (data.equals("")){
            insertToDB();
        }

//        setAds();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 6);
        c.set(Calendar.HOUR_OF_DAY, 15);
        c.set(Calendar.MINUTE, 15);
        c.set(Calendar.SECOND, 0);

        startAlarm(c);

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.DAY_OF_WEEK, 5);
        c1.set(Calendar.HOUR_OF_DAY, 18);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);

        startAlarm1(c1);

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.DAY_OF_WEEK, 7);
        c2.set(Calendar.HOUR_OF_DAY, 22);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);

        startAlarm2(c2);

        int seatHeight = (Resources.getSystem().getDisplayMetrics().heightPixels)/22;
        int seatWidth = (Resources.getSystem().getDisplayMetrics().widthPixels)/30;
        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels));
        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels/24));

        layout = findViewById(R.id.layoutSeat);

        seats = "/" + seats;

        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(6 * seatGapingH, 8 * seatGapingV, 6 * seatGapingH, 8 * seatGapingV);
        layout.addView(layoutSeat);

        LinearLayout layout = null;
        count=0;
        countAll=0;
        countName=0;
        countMsg=0;

        for (int index = 0; index < seats.length(); index++) {
            if (seats.charAt(index) == '/') {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);
            } else if (seats.charAt(index) == 'U') {
                count++;
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatWidth, seatHeight);
//                layoutParams.weight = 1;
                layoutParams.setMargins(seatGapingH, seatGapingV, seatGapingH, seatGapingV);
                view.setLayoutParams(layoutParams);
                view.setTypeface(null, Typeface.BOLD);
//                view.setPadding(0, 0, 0, 2 * seatGapingV);
                view.setPadding(0, 0, 0, 0);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_red);
//                view.setBackgroundColor(Color.RED);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_PRESENT);
//                view.setText(count + "");
//                view.setText("מלכה");
                String name[] = dbHandler.getNameById(count).split(" ");
                if(name.length==2){view.setText(name[1]);}
                else {
                    if (name[0].split(".").length==2){view.setText(name[0].split(".")[1]);}
                    else{view.setText(name[0]);}
                }

//                view.setAutoSizeTextTypeUniformWithConfiguration(3,10,1,TypedValue.COMPLEX_UNIT_DIP);
//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 3, 10, 1, TypedValue.COMPLEX_UNIT_DIP);
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, seatWidth/13);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            } else if (seats.charAt(index) == 'A') {
                count++;
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatWidth, seatHeight);
//                layoutParams.weight = 1;
                layoutParams.setMargins(seatGapingH, seatGapingV, seatGapingH, seatGapingV);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 2 * seatGapingV);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.ic_seats_green);
//                view.setBackgroundColor(Color.GREEN);
//                view.setText(count + "");
//                view.setText(dbHandler.getNameById(count));
                String name[] = dbHandler.getNameById(count).split(" ");
                if(name.length==2){view.setText(name[1]);}
                else {
                    if (name[0].split(".").length==2){view.setText(name[0].split(".")[1]);}
                    else{view.setText(name[0]);}
                }
//                view.setAutoSizeTextTypeUniformWithConfiguration(3,10,1,TypedValue.COMPLEX_UNIT_DIP);
//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 3, 5, 1, TypedValue.COMPLEX_UNIT_DIP);
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, seatWidth/13);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_MISSING);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }else if (seats.charAt(index) == 'B' || seats.charAt(index) == 'M') {
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams =
//                        new LinearLayout.LayoutParams(seatSize+seatGapingH+seatGapingH, seatSize+seatGapingV+seatGapingV);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(seatWidth+seatGapingH+seatGapingH, seatHeight+seatGapingH+seatGapingH);
//                layoutParams.weight = 1;
                layoutParams.setMargins(0, 0, 0, 0);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.GRAY);
                view.setText("");
                if (seats.charAt(index) == 'B') {
                    view.setId(countName);
                    countName++;
                }
                else {
                    view.setId(countName+countMsg);
                    countMsg++;
                }
                layout.addView(view);
            } else if (seats.charAt(index) == '_') {
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatWidth, seatHeight);
//                layoutParams.weight = 1;
                layoutParams.setMargins(seatGapingH, seatGapingV, seatGapingH, seatGapingV);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setText("");
                layout.addView(view);
            }
        }

        String name = "בית כנסת היכל גבריאל";
        setName(name);
        updateSeats();
        Thread a = new Thread(new adsThread());
        a.start();
    }

    public static void setMissing(int id){
        View view = layout.findViewById(id);
        view.setBackgroundResource(R.drawable.ic_seats_green);
//        view.setTag(1);
    }

    public static void setPresent(int id){
        View view = layout.findViewById(id);
        view.setBackgroundResource(R.drawable.ic_seats_red);
//        view.setTag(1);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        assert alarmManager != null;
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 7* 24 * 60 * 60 * 1000, pendingIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm1(Calendar c1){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver1.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0);

        assert alarmManager != null;
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), 7* 24 * 60 * 60 * 1000, pendingIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm2(Calendar c2){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver2.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 3, intent, 0);

        assert alarmManager != null;
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 7* 24 * 60 * 60 * 1000, pendingIntent);
    }

//    public class AlertReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Pulling new data", Toast.LENGTH_LONG).show();
//            // Get all the new data from the DataBase and update the list
//        }
//    }

//    public class AlertReceiver1 extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) { // Sending sms to all users
//            String msg = "האם תגיע השבת לבית הכנסת? כן/לא";
//            String data = dbHandler.getPhoneNumbers();
//            String[] phoneNumbers = data.split("\n");
//            LinkedHashSet<String> lhSetColors =
//                    new LinkedHashSet<String>(Arrays.asList(phoneNumbers)); // removes duplicate numbers
//            //create array from the LinkedHashSet
//            String[] noDuplicatePN = lhSetColors.toArray(new String[ lhSetColors.size() ]);
////            for(int i=0; i<noDuplicatePN.length;i++){
////                smsSendMessage(noDuplicatePN[i], msg);
////            }
//            Log.d("alertReciever", noDuplicatePN[0] + " " + noDuplicatePN[1]);
//            Toast.makeText(context, noDuplicatePN[0] + " " + noDuplicatePN[1], Toast.LENGTH_LONG).show();
//        }
//    }

//    public class AlertReceiver2 extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Reseting the seats", Toast.LENGTH_LONG).show();
//            int i=0;
//            for (i=0;i<count;i++){
////                layout.findViewById(i).setTag(STATUS_PRESENT);
////                layout.findViewById(i).setBackgroundResource(R.drawable.ic_seats_red);
//                View view = layout.findViewById(i);
//                view.setBackgroundResource(R.drawable.ic_seats_green);
//            }
////            layout.notifyAll();
//        }
//    }

    public static void smsSendMessage(String phoneNumber, String msg) {
        // Phone number of user.
        String destinationAddress = phoneNumber; //Phone number from DataBase
        // Text to send to user.
        String smsMessage = msg;
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(smsMessage);
        smsManager.sendMultipartTextMessage(destinationAddress, null, parts, null, null);
//        smsManager.sendTextMessage
//                (destinationAddress, scAddress, smsMessage,
//                        sentIntent, deliveryIntent);
        Log.d("smsSender", "smsSendMessage: Sent message");
    }

//    public class smsReceiver extends BroadcastReceiver {
//        private final String TAG =
//                smsReceiver.class.getSimpleName();
//        public static final String pdu_type = "pdus";
//
//        @TargetApi(Build.VERSION_CODES.M)
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get the SMS message.
//            Bundle bundle = intent.getExtras();
//            SmsMessage[] msgs;
//            String strMessage = "";
//            String format = bundle.getString("format");
//            // Retrieve the SMS message received.
//            Object[] pdus = (Object[]) bundle.get(pdu_type);
//            if (pdus != null) {
//                // Check the Android version.
//                boolean isVersionM =
//                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
//                // Fill the msgs array.
//                msgs = new SmsMessage[pdus.length];
//                for (int i = 0; i < msgs.length; i++) {
//                    // Check Android version and use appropriate createFromPdu.
//                    if (isVersionM) {
//                        // If Android version M or newer:
//                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                    } else {
//                        // If Android version L or older:
//                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                    }
//                    // Build the message to show.
//                    strMessage += "SMS from " + msgs[i].getOriginatingAddress();
//                    strMessage += " :" + msgs[i].getMessageBody() + "\n";
//                    // Log and display the SMS message.
//                    Log.d(TAG, "onReceive: " + strMessage);
//                    Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }

//    public class smsReceiver extends BroadcastReceiver {
//        private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//        private static final String TAG = "SmsBroadcastReceiver";
//        String msg, phoneNo = "";
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //retrieves the general action to be performed and display on log
//            Log.i(TAG, "Intent Received: " +intent.getAction());
//            if (intent.getAction()==SMS_RECEIVED)
//            {
//                //retrieves a map of extended data from the intent
//                Bundle dataBundle = intent.getExtras();
//                if (dataBundle!=null)
//                {
//                    //creating PDU(Protocol Data Unit) object which is a protocol for transferring message
//                    Object[] mypdu = (Object[])dataBundle.get("pdus");
//                    final SmsMessage[] message = new SmsMessage[mypdu.length];
//
//                    for (int i = 0; i<mypdu.length; i++)
//                    {
//                        //for build versions >= API Level 23
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        {
//                            String format = dataBundle.getString("format");
//                            //From PDU we get all object and SmsMessage Object using following line of code
//                            message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
//                        }
//                        else
//                        {
//                            //<API level 23
//                            message[i] = SmsMessage.createFromPdu((byte[])mypdu[i]);
//                        }
//                        msg = message[i].getMessageBody();
//                        phoneNo = message[i].getOriginatingAddress();
//                    }
//                    Toast.makeText(context, "Message: " +msg +"\nNumber: " +phoneNo, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }


    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        //check if the permission is not granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            //if the permission is not been granted then check if the user has denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS))
            {
                //Do nothing as user has denied
                Toast.makeText(this, "no permission", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
            else
            {
                //a pop up will appear asking for required permission i.e Allow or Deny
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        //will check the requestCode
        switch(requestCode)
        {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
            {
                //check whether the length of grantResults is greater than 0 and is equal to PERMISSION_GRANTED
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    //Now broadcastreceiver will work in background
                    Toast.makeText(this, "Thank you for permitting!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(this, "Well I can't do anything until you permit me", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//    private Timer mTimer1;
//    private TimerTask mTt1;
//    private Handler mTimerHandler = new Handler();
//
//    private void stopTimer(){
//        if(mTimer1 != null){
//            mTimer1.cancel();
//            mTimer1.purge();
//        }
//    }
//
//    private void startTimer(){
//        mTimer1 = new Timer();
//        mTt1 = new TimerTask() {
//            public void run() {
//                mTimerHandler.post(new Runnable() {
//                    public void run(){
//                        new Thread(new getUpdate()).start();
//                    }
//                });
//            }
//        };
//
//        mTimer1.schedule(mTt1, 1, 60*60*1000); // every hour
//    }
//
//    class getUpdate implements Runnable {
//
//        @Override
//        public void run() {
//
//        }
//    }

    @Override
    public void onClick(View view) {
////        setMissing(view.getId());
//        smsSendMessage("0549766185", "האם תגיע השבת לבית הכנסת? כן/לא");
//        if ((int) view.getTag() == STATUS_MISSING) {
//            if (selectedIds.contains(view.getId() + ",")) {
//                selectedIds = selectedIds.replace(+view.getId() + ",", "");
//                view.setBackgroundResource(R.drawable.ic_seats_green);
////                view.setBackgroundColor(Color.GREEN);
//            } else {
//                selectedIds = selectedIds + view.getId() + ",";
//                view.setBackgroundResource(R.drawable.ic_seats_red);
////                view.setBackgroundColor(Color.RED);
//            }
//        } else if ((int) view.getTag() == STATUS_PRESENT) {
//            Toast.makeText(this, "Seat " + view.getId() + " is Taken", Toast.LENGTH_SHORT).show();
//        }
        Toast.makeText(this, dbHandler.getNameById(view.getId()), Toast.LENGTH_SHORT).show();
    }

    public void insertToDB(){
        BufferedReader reader;
        String[] splitedLine;
        String name,phoneNumber,seatId;
        try{
            final InputStream file = getAssets().open("Users.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line=reader.readLine();
            while(line != null){
                splitedLine = line.split("\t");
                name = splitedLine[1];
                if (name.equals("פנוי")){
//                    setMissing(Integer.parseInt(seatId));
                    phoneNumber = "0000000000";
                } else {
                    phoneNumber = splitedLine[3];
                }
                seatId = splitedLine[0];
                dbHandler.insertUserDetails(name,phoneNumber,seatId, "red");
                line = reader.readLine();
            }
            Toast.makeText(this, "Inserted new users", Toast.LENGTH_LONG).show();
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void setName(String name){

    }

    public void updateSeats() {
        DbHandler dbHandler = new DbHandler(this);
        String seats = dbHandler.getGreenSeatId();
        Toast.makeText(this, "Updating seats from DB\n" + seats, Toast.LENGTH_LONG).show();
        if (seats != null && seats != "") {
            String[] seatsArr = seats.split(" ");
            // Get all the new data from the DataBase and update the list
            for (int i = 0; i < seatsArr.length; i++) {
                if (seatsArr[i] != null && seatsArr[i] != "") {
                    setMissing(Integer.parseInt(seatsArr[i]));
                }
            }
        }
    }

    public void setAds(){
        int[] ads = {R.drawable.ad1,R.drawable.ad2,R.drawable.ad3,R.drawable.advertise_here};
        ImageView ad1 = findViewById(R.id.ad1);
        ImageView ad2 = findViewById(R.id.ad2);
        ImageView ad3 = findViewById(R.id.ad3);
        ad1.setImageResource(ads[0]);
        ad2.setImageResource(ads[1]);
        ad3.setImageResource(ads[2]);
//        ad1.setBackground(ContextCompat.getDrawable(this, R.drawable.ad1));
//        ad2.setBackground(ContextCompat.getDrawable(this, R.drawable.ad2));
//        ad3.setBackground(ContextCompat.getDrawable(this, R.drawable.advertise_here));
    }

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    int i=0;

    /*@Override
    public void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        Thread a = new Thread(new adsThread());
                        a.start();
                        try {
                            a.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 5000);
    }*/

    class adsThread implements Runnable {
        int[] ads = {R.drawable.ad1,R.drawable.ad2,R.drawable.ad3,R.drawable.advertise_here};
        ImageView ad1 = findViewById(R.id.ad1);
        ImageView ad2 = findViewById(R.id.ad2);
        ImageView ad3 = findViewById(R.id.ad3);
        @Override
        public void run() {
            while (true){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ad1.setImageResource(ads[i]);
                        ad2.setImageResource(ads[i+1]);
                        ad3.setImageResource(ads[3]);
                    }
                });
                i++;
                if (i==2){i=0;}
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
