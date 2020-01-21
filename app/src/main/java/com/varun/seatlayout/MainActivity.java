package com.varun.seatlayout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static ViewGroup layout;

    public String seats =
            "____________B___U___/"
                    + "_______U____X__UUU__/"
                    + "_______UU___X__UUU__/"
                    + "______UUUA___________AUUU_/"
                    + "______UUUU__AAAAAAA__AUUU_/"
                    + "____________UUUUUUU_______/"
                    + "AA___UUUUU__UUUUUUU__AUUUU/"
                    + "AAU__UUUUA__UUUUUUU__AUUUU/"
                    + "AAUU_UUUUU___________AUUUU/"
                    + "AAUU_UUUUA__X__AUUUU/"
                    + "____________X______A/"
                    + "UUUU_UUUUU__X__AUUUU/"
                    + "UUUU_UUUUA__X__AUUUU/"
                    + "A_UU_UUUUA__X__AUUUU/"
                    + "AA___UUUUA__X__AUUUU/"
                    + "AA________________________/"
//            + "__________________________/"
                    + "_________AAAAAA_AAAAAAA__U/"
                    + "_________UUUUUU_UUUUUUU_U_/"
                    + "__________UUUUU_UUUUUU____/"
                    + "___________UUUUUUUUU______/";

    public String seats1 =
              "____________B___U___/"
            + "_______U____X__UUU__/"
            + "_______UU___X__UUU__/"
            + "______UUUA___________AUUU_/"
            + "______UUUU__AAAAAAA__AUUU_/"
            + "____________UUUUUUU_______/"
            + "AA___UUUUU__UUUUUUU__AUUUU/"
            + "AAU__UUUUA__UUUUUUU__AUUUU/"
            + "AAUU_UUUUU___________AUUUU/"
            + "AAUU_UUUUA__X__AUUUU/"
            + "____________X______A/"
            + "UUUU_UUUUU__X__AUUUU/"
            + "UUUU_UUUUA__X__AUUUU/"
            + "A_UU_UUUUA__X__AUUUU/"
            + "AA___UUUUA__X__AUUUU/"
            + "AA________________________/"
//            + "__________________________/"
            + "_________AAAAAA_AAAAAAA__U/"
            + "_________UUUUUU_UUUUUUU_U_/"
            + "__________UUUUU_UUUUUU____/"
            + "___________UUUUUUUUU______/";

    public String seats2 =
            "____________B___U___/"
                    + "_______U____X__UUU__/"
                    + "_______UU___X__UUU__/"
                    + "______UUUA___________AUUU_/"
                    + "______UUUU__AAAAAAA__AUUU_/"
                    + "____________UUUUUUU_______/"
                    + "AA___UUUUU__UUUUUUU__AUUUU/"
                    + "AAU__UUUUA__UUUUUUU__AUUUU/"
                    + "AAUU_UUUUU___________AUUUU/"
                    + "AAUU_UUUUA__X__AUUUU/"
                    + "____________X______A/"
                    + "UUUU_UUUUU__X__AUUUU/"
                    + "UUUU_UUUUA__X__AUUUU/"
                    + "A_UU_UUUUA__X__AUUUU/"
                    + "AA___UUUUA__X__AUUUU/"
                    + "AA________________________/"
//            + "__________________________/"
                    + "_________AAAAAA_AAAAAAA__U/"
                    + "_________UUUUUU_UUUUUUU_U_/"
                    + "__________UUUUU_UUUUUU____/"
                    + "___________UUUUUUUUU______/";

    public String seats3 =
            "____________B___U___/"
                    + "_______U____X__UUU__/"
                    + "_______UU___X__UUU__/"
                    + "______UUUA___________AUUU_/"
                    + "______UUUU__AAAAAAA__AUUU_/"
                    + "____________UUUUUUU_______/"
                    + "AA___UUUUU__UUUUUUU__AUUUU/"
                    + "AAU__UUUUA__UUUUUUU__AUUUU/"
                    + "AAUU_UUUUU___________AUUUU/"
                    + "AAUU_UUUUA__X__AUUUU/"
                    + "____________X______A/"
                    + "UUUU_UUUUU__X__AUUUU/"
                    + "UUUU_UUUUA__X__AUUUU/"
                    + "A_UU_UUUUA__X__AUUUU/"
                    + "AA___UUUUA__X__AUUUU/"
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
    static int BEIT_KNESET_NUMBER = 0;
    String BEIT_KNESET_NAME = "";
    String BEIT_KNESET_NAME1 = "בית כנסת נחלת שי";
    String BEIT_KNESET_NAME2 = "בית כנסת היכל שלמה";
    String BEIT_KNESET_NAME3 = "בית כנסת המלך דוד";
    DbHandler dbHandler;
//    static public final String IP = "192.168.43.43"; // Phone Router
//    static public final String IP = "89.139.205.95"; // Home Router
    static public final String IP = "myseatingapp.ddns.net"; // Home Router
    static public final int PORT = 443;
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;

    int STATUS_MISSING = 1;
    int STATUS_PRESENT = 2;
    int UPDATE_SEATS = 4;
    int RESET_SEATS = 5;
    String selectedIds = "";



    String[] toWrite = {BEIT_KNESET_NAME, "שבת פרשת: ","", "", "", "", "", ""};
    String[] toWrite2 = {BEIT_KNESET_NAME, "שבת פרשת: ","הודעות:", "", "", "", "", ""};
    String[] defaultWrite = {BEIT_KNESET_NAME, "שבת פרשת: ","הודעות:", "", "", "", "", ""};
    String[] noChangeText = {BEIT_KNESET_NAME, "שבת פרשת: ", "לעילוי נשמת", "יוסף מלכה בר רחל", "רחל מלכה בת חנה", "הלנה פנקסוביץ'", "משה לייב פנקסוביץ'", ""};
    static int count, countAll, countX, first1,first2,first3;
    int[] myResources = {R.id.NameText,R.id.ShabbatParasha,R.id.HeadLineText,R.id.FirstText,R.id.SecondText,R.id.ThirdText,R.id.FourthText,R.id.FifthText};
    String[] inMemoryNames = {"","","","","","","","",""};
    static int backgroundCount=0, successfulConnection=0;
    static int currentStatus=0, checkDBUpdate=0,getInMemories=0, checkDBUpdateFirst=1;
    String statuses = "";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        checkForSmsPermission();
//        String data = dbHelper.getData();
//        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        dbHandler = new DbHandler(this);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        dbHandler.onUpgrade(db,1,2);
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
        dbHandler.DeleteAll();
        data = dbHandler.getData();
        if (data.equals("")){
            insertToDB();
        }
        layout = findViewById(R.id.layoutSeat);
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Toast.makeText(this, "saved: " + prefs.getInt("BeitKnesetNumber",0), Toast.LENGTH_LONG).show();
        if (prefs.getBoolean("isLoginKey",false) && prefs.getInt("BeitKnesetNumber",0)!=0){
            BEIT_KNESET_NUMBER = prefs.getInt("BeitKnesetNumber",0);
        }else{
            replaceFragment();
        }
//        if (getSharedPreferences("loginPrefs",MODE_PRIVATE).getBoolean("isLoginKey",false)){
//            Log.d("TEST", "onCreate: saved password! number: " + getSharedPreferences("loginPrefs",MODE_PRIVATE).getInt("BeitKnesetNumber",0));
//        }
//        replaceFragment();

//        BEIT_KNESET_NUMBER=1;

//        if (BEIT_KNESET_NUMBER == 1){
//            BEIT_KNESET_NAME = BEIT_KNESET_NAME1;
//            seats = seats1;
//        } else if (BEIT_KNESET_NUMBER==2){
//            BEIT_KNESET_NAME = BEIT_KNESET_NAME2;
//            seats = seats2;
//        } else if(BEIT_KNESET_NUMBER==3){
//            BEIT_KNESET_NAME = BEIT_KNESET_NAME3;
//            seats = seats3;
//        }
//
//        int seatHeight = (Resources.getSystem().getDisplayMetrics().heightPixels)/22;
//        int seatWidth = (Resources.getSystem().getDisplayMetrics().widthPixels)/30;
//        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels));
//        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels/24));
//
//        layout = findViewById(R.id.layoutSeat);
//
//        seats = "/" + seats;
//        float additionByScreen = getResources().getDimension(R.dimen.text_size);
//
//        LinearLayout layoutSeat = new LinearLayout(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutSeat.setOrientation(LinearLayout.VERTICAL);
//        layoutSeat.setLayoutParams(params);
//        layoutSeat.setPadding(6 * seatGapingH, 8 * seatGapingV, 6 * seatGapingH, 8 * seatGapingV);
//        layout.addView(layoutSeat);
//        layout.setBackgroundResource(R.drawable.b7);
//
//        LinearLayout layout = null;
//        count=0;
//        countAll=0;
//        countX=0;
//        int size = (seatWidth+seatGapingH+seatGapingH)*7;
//
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//
//        for (int index = 0; index < seats.length(); index++) {
//            if (seats.charAt(index) == '/') {
//                layout = new LinearLayout(this);
//                layout.setOrientation(LinearLayout.HORIZONTAL);
//                layoutSeat.addView(layout);
//            }
//            else if (seats.charAt(index) == 'U') {
//                count++;
//                countAll++;
//                TextView view = new TextView(this);
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatWidth, seatHeight);
////                layoutParams.weight = 1;
//                layoutParams.setMargins(seatGapingH, seatGapingV, seatGapingH, seatGapingV);
//                view.setLayoutParams(layoutParams);
//                view.setTypeface(null, Typeface.BOLD);
////                view.setPadding(0, 0, 0, 2 * seatGapingV);
//                view.setPadding(0, 0, 0, 0);
//                view.setId(count);
//                view.setGravity(Gravity.CENTER);
//                view.setBackgroundResource(R.drawable.ic_seats_light_red);
////                view.setBackgroundColor(Color.RED);
//                view.setTextColor(Color.WHITE);
//                view.setTag(STATUS_PRESENT);
////                view.setText(count + "");
////                view.setText("מלכה");
//                String name[] = dbHandler.getNameById(count).split(" ");
//                if(name.length==2){view.setText(name[1]);}
//                else {
//                    if (name[0].split(".").length==2){view.setText(name[0].split(".")[1]);}
//                    else{view.setText(name[0]);}
//                }
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                    view.setAutoSizeTextTypeUniformWithConfiguration(3,12,1,TypedValue.COMPLEX_UNIT_DIP);
////                } else{
////                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 3, 12, 1, TypedValue.COMPLEX_UNIT_DIP);
////                }
//////                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, view.getTextSize()-5);
//                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, seatWidth/13 + additionByScreen); //seatWidth/13+4
//                layout.addView(view);
//                seatViewList.add(view);
//                view.setOnClickListener(this);
//            }
//            else if (seats.charAt(index) == 'A') {
//                count++;
//                countAll++;
//                TextView view = new TextView(this);
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatWidth, seatHeight);
////                layoutParams.weight = 1;
//                layoutParams.setMargins(seatGapingH, seatGapingV, seatGapingH, seatGapingV);
//                view.setLayoutParams(layoutParams);
//                view.setPadding(0, 0, 0, 2 * seatGapingV);
//                view.setId(count);
//                view.setGravity(Gravity.CENTER);
//                view.setBackgroundResource(R.drawable.ic_seats_light_green);
////                view.setBackgroundColor(Color.GREEN);
////                view.setText(count + "");
////                view.setText(dbHandler.getNameById(count));
//                String name[] = dbHandler.getNameById(count).split(" ");
//                if(name.length==2){view.setText(name[1]);}
//                else {
//                    if (name[0].split(".").length==2){view.setText(name[0].split(".")[1]);}
//                    else{view.setText(name[0]);}
//                }
////                view.setAutoSizeTextTypeUniformWithConfiguration(3,10,1,TypedValue.COMPLEX_UNIT_DIP);
////                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 3, 5, 1, TypedValue.COMPLEX_UNIT_DIP);
//                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, seatWidth/13 + additionByScreen); //seatWidth/13+4
//                view.setTextColor(Color.GRAY);
//                view.setTag(STATUS_MISSING);
//                layout.addView(view);
//                seatViewList.add(view);
//                view.setOnClickListener(this);
//            }
//            else if (seats.charAt(index) == 'X') {
//                countAll++;
//                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams =
//                        new LinearLayout.LayoutParams(size, seatHeight+seatGapingH+seatGapingH);
//                layoutParams.setMargins(0, 0, 0, 0);
//                view.setLayoutParams(layoutParams);
////                view.setBackgroundColor(getResources().getColor(R.color.veryLiightGray));
//                view.setId(myResources[countX]);
//                view.setText("");
//                view.setTag(UPDATE_SEATS);
//                view.setTextColor(Color.GRAY);
//                view.setOnClickListener(this);
//                layout.addView(view);
//                countX++;
//            }
//            else if (seats.charAt(index) == 'B') {
//                countAll++;
//                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams =
//                        new LinearLayout.LayoutParams(size, seatHeight+seatGapingH+seatGapingH);
//                layoutParams.setMargins(0, 0, 0, 0);
//                view.setLayoutParams(layoutParams);
////                view.setBackgroundColor(getResources().getColor(R.color.veryLiightGray));
//                view.setText("");
//                view.setTag(RESET_SEATS);
//                layout.addView(view);
//                view.setOnClickListener(this);
//            }
//            else if (seats.charAt(index) == '_') {
//                countAll++;
//                TextView view = new TextView(this);
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatWidth, seatHeight);
////                layoutParams.weight = 1;
//                layoutParams.setMargins(seatGapingH, seatGapingV, seatGapingH, seatGapingV);
//                view.setLayoutParams(layoutParams);
//                view.setBackgroundColor(Color.TRANSPARENT);
//                view.setText("");
//                layout.addView(view);
//            }
//        }
//
//        Thread getStatusesThread = new Thread(new getStatusesThread());
//        getStatusesThread.start();
//        Thread a = new Thread(new adsThread());
//        a.start();
        Thread setup = new Thread(new setupThread());
        setup.start();
    }

    class setupThread implements Runnable {

        @Override
        public void run() {
            while (BEIT_KNESET_NUMBER==0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            final Semaphore mutex = new Semaphore(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSeats();
                    mutex.release();
                }
            });
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    boolean replaceFragment(){
        Fragment myFragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, myFragment);
        fragmentTransaction.addToBackStack(myFragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        return true;
    }

    public void setSeats(){
        if (BEIT_KNESET_NUMBER == 1){
            BEIT_KNESET_NAME = BEIT_KNESET_NAME1;
            seats = seats1;
        } else if (BEIT_KNESET_NUMBER==2){
            BEIT_KNESET_NAME = BEIT_KNESET_NAME2;
            seats = seats2;
        } else if(BEIT_KNESET_NUMBER==3){
            BEIT_KNESET_NAME = BEIT_KNESET_NAME3;
            seats = seats3;
        }

        int seatHeight = (Resources.getSystem().getDisplayMetrics().heightPixels)/22;
        int seatWidth = (Resources.getSystem().getDisplayMetrics().widthPixels)/30;
        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels));
        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels/24));

//        layout = findViewById(R.id.layoutSeat);

        seats = "/" + seats;
        float additionByScreen = getResources().getDimension(R.dimen.text_size);

        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(6 * seatGapingH, 8 * seatGapingV, 6 * seatGapingH, 8 * seatGapingV);
        layout.addView(layoutSeat);
        layout.setBackgroundResource(R.drawable.b7);

        LinearLayout layout = null;
        count=0;
        countAll=0;
        countX=0;
        int size = (seatWidth+seatGapingH+seatGapingH)*7;

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        for (int index = 0; index < seats.length(); index++) {
            if (seats.charAt(index) == '/') {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);
            }
            else if (seats.charAt(index) == 'U') {
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
                view.setBackgroundResource(R.drawable.ic_seats_light_red1);
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
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    view.setAutoSizeTextTypeUniformWithConfiguration(3,12,1,TypedValue.COMPLEX_UNIT_DIP);
//                } else{
//                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 3, 12, 1, TypedValue.COMPLEX_UNIT_DIP);
//                }
////                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, view.getTextSize()-5);
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, seatWidth/13 + additionByScreen); //seatWidth/13+4
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }
            else if (seats.charAt(index) == 'A') {
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
                view.setBackgroundResource(R.drawable.ic_seats_light_green);
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
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, seatWidth/13 + additionByScreen); //seatWidth/13+4
                view.setTextColor(Color.GRAY);
                view.setTag(STATUS_MISSING);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }
            else if (seats.charAt(index) == 'X') {
                countAll++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(size, seatHeight+seatGapingH+seatGapingH);
                layoutParams.setMargins(0, 0, 0, 0);
                view.setLayoutParams(layoutParams);
//                view.setBackgroundColor(getResources().getColor(R.color.veryLiightGray));
                view.setId(myResources[countX]);
                view.setText("");
                view.setTag(UPDATE_SEATS);
                view.setTextColor(Color.GRAY);
                view.setOnClickListener(this);
                layout.addView(view);
                countX++;
            }
            else if (seats.charAt(index) == 'B') {
                countAll++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(size, seatHeight+seatGapingH+seatGapingH);
                layoutParams.setMargins(0, 0, 0, 0);
                view.setLayoutParams(layoutParams);
//                view.setBackgroundColor(getResources().getColor(R.color.veryLiightGray));
                view.setText("");
                view.setTag(RESET_SEATS);
                layout.addView(view);
                view.setOnClickListener(this);
            }
            else if (seats.charAt(index) == '_') {
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

        Thread getStatusesThread = new Thread(new getStatusesThread());
        getStatusesThread.start();
        Thread a = new Thread(new adsThread());
        a.start();
    }

    public static void setMissing(int id){
        TextView view = layout.findViewById(id);
        view.setBackgroundResource(R.drawable.ic_seats_light_green);
        view.setTextColor(Color.GRAY);
        view.setTag(1);
    }

    public static void setPresent(int id){
        TextView view = layout.findViewById(id);
        view.setBackgroundResource(R.drawable.ic_seats_light_red1);
        view.setTextColor(Color.WHITE);
        view.setTag(2);
    }

//    public static void smsSendMessage(String phoneNumber, String msg) {
//        // Phone number of user.
//        String destinationAddress = phoneNumber; //Phone number from DataBase
//        // Text to send to user.
//        String smsMessage = msg;
//        // Set the service center address if needed, otherwise null.
//        String scAddress = null;
//        // Set pending intents to broadcast
//        // when message sent and when delivered, or set to null.
//        PendingIntent sentIntent = null, deliveryIntent = null;
//        // Use SmsManager.
//        SmsManager smsManager = SmsManager.getDefault();
//        ArrayList<String> parts = smsManager.divideMessage(smsMessage);
//        smsManager.sendMultipartTextMessage(destinationAddress, null, parts, null, null);
////        smsManager.sendTextMessage
////                (destinationAddress, scAddress, smsMessage,
////                        sentIntent, deliveryIntent);
//        Log.d("smsSender", "smsSendMessage: Sent message");
//    }
//
////    public class smsReceiver extends BroadcastReceiver {
////        private final String TAG =
////                smsReceiver.class.getSimpleName();
////        public static final String pdu_type = "pdus";
////
////        @TargetApi(Build.VERSION_CODES.M)
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            // Get the SMS message.
////            Bundle bundle = intent.getExtras();
////            SmsMessage[] msgs;
////            String strMessage = "";
////            String format = bundle.getString("format");
////            // Retrieve the SMS message received.
////            Object[] pdus = (Object[]) bundle.get(pdu_type);
////            if (pdus != null) {
////                // Check the Android version.
////                boolean isVersionM =
////                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
////                // Fill the msgs array.
////                msgs = new SmsMessage[pdus.length];
////                for (int i = 0; i < msgs.length; i++) {
////                    // Check Android version and use appropriate createFromPdu.
////                    if (isVersionM) {
////                        // If Android version M or newer:
////                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
////                    } else {
////                        // If Android version L or older:
////                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
////                    }
////                    // Build the message to show.
////                    strMessage += "SMS from " + msgs[i].getOriginatingAddress();
////                    strMessage += " :" + msgs[i].getMessageBody() + "\n";
////                    // Log and display the SMS message.
////                    Log.d(TAG, "onReceive: " + strMessage);
////                    Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
////                }
////            }
////        }
////    }
//
////    public class smsReceiver extends BroadcastReceiver {
////        private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
////        private static final String TAG = "SmsBroadcastReceiver";
////        String msg, phoneNo = "";
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            //retrieves the general action to be performed and display on log
////            Log.i(TAG, "Intent Received: " +intent.getAction());
////            if (intent.getAction()==SMS_RECEIVED)
////            {
////                //retrieves a map of extended data from the intent
////                Bundle dataBundle = intent.getExtras();
////                if (dataBundle!=null)
////                {
////                    //creating PDU(Protocol Data Unit) object which is a protocol for transferring message
////                    Object[] mypdu = (Object[])dataBundle.get("pdus");
////                    final SmsMessage[] message = new SmsMessage[mypdu.length];
////
////                    for (int i = 0; i<mypdu.length; i++)
////                    {
////                        //for build versions >= API Level 23
////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
////                        {
////                            String format = dataBundle.getString("format");
////                            //From PDU we get all object and SmsMessage Object using following line of code
////                            message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
////                        }
////                        else
////                        {
////                            //<API level 23
////                            message[i] = SmsMessage.createFromPdu((byte[])mypdu[i]);
////                        }
////                        msg = message[i].getMessageBody();
////                        phoneNo = message[i].getOriginatingAddress();
////                    }
////                    Toast.makeText(context, "Message: " +msg +"\nNumber: " +phoneNo, Toast.LENGTH_LONG).show();
////                }
////            }
////        }
////    }
//
//
//    private void checkForSmsPermission() {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.SEND_SMS) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.SEND_SMS},
//                    MY_PERMISSIONS_REQUEST_SEND_SMS);
//        }
//        //check if the permission is not granted
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)
//        {
//            //if the permission is not been granted then check if the user has denied the permission
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS))
//            {
//                //Do nothing as user has denied
//                Toast.makeText(this, "no permission", Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
//            }
//            else
//            {
//                //a pop up will appear asking for required permission i.e Allow or Deny
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
//    {
//        //will check the requestCode
//        switch(requestCode)
//        {
//            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
//            {
//                //check whether the length of grantResults is greater than 0 and is equal to PERMISSION_GRANTED
//                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
//                {
//                    //Now broadcastreceiver will work in background
//                    Toast.makeText(this, "Thank you for permitting!", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(this, "Well I can't do anything until you permit me", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }

    @Override
    public void onClick(View view) {
//        int[] backgrounds = {R.drawable.b1,R.drawable.b2,R.drawable.b3,R.drawable.b4,R.drawable.b5,R.drawable.b6,R.drawable.b7,R.drawable.b8,R.drawable.b9,R.drawable.b10, R.drawable.b11, R.drawable.b12, R.drawable.b13};
//        backgroundCount++;
//        if (backgroundCount>backgrounds.length-1){backgroundCount=0;}
//        ConstraintLayout layout =(ConstraintLayout) findViewById(R.id.main_layout);
//        layout.setBackgroundResource(backgrounds[backgroundCount]);
//        Toast.makeText(this, dbHandler.getNameById(view.getId()), Toast.LENGTH_SHORT).show();
//        if ((int) view.getTag() == RESET_SEATS) {
//            checkDBUpdate=1;
//            Toast.makeText(this, "Checking for updates in DB", Toast.LENGTH_SHORT).show();
//        }
//        TextView tv1 = (TextView) view;
//        if ((int) view.getTag() == STATUS_PRESENT) {
//            Toast.makeText(this, dbHandler.getNameById(view.getId()), Toast.LENGTH_SHORT).show();
//            view.setTag(STATUS_MISSING);
//            view.setBackgroundResource(R.drawable.ic_seats_light_green);
//            tv1.setTextColor(Color.GRAY);
//        } else if ((int) view.getTag() == UPDATE_SEATS) {
//            Thread getStatusesThread = new Thread(new getStatusesThread());
//            getStatusesThread.start();
//        } else if((int) view.getTag() == RESET_SEATS) {
//            String name;
//            Toast.makeText(this, "Reset Statuses", Toast.LENGTH_SHORT).show();
//            for (int i = 1; i <= count; i++) {
//                name = dbHandler.getNameById(i);
//                if (!name.equals("פנוי")) {
//                    dbHandler.UpdateUserStatus("red", i);
//                    MainActivity.setPresent(i);
//                }
//            }
//        } else if (!tv1.getText().equals("פנוי")){
//            Toast.makeText(this, dbHandler.getNameById(view.getId()), Toast.LENGTH_SHORT).show();
//            view.setTag(STATUS_PRESENT);
//            view.setBackgroundResource(R.drawable.ic_seats_light_red);
//            tv1.setTextColor(Color.WHITE);
//        }
    }

    class getStatusesThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    if (checkDBUpdateFirst==1){
                        checkDBUpdateFirst = 0;
                        Thread c = new Thread(new updateDbThread());
                        c.start();
                        c.join();
                    }
                    Thread.sleep(1000);
                    socket = new Socket(MainActivity.IP, MainActivity.PORT);
                    dos = new DataOutputStream(socket.getOutputStream());
                    String message;
                    message = "0 get statuses " + (MainActivity.BEIT_KNESET_NUMBER-1);
                    String receivedMsg = "";
                    dos.writeUTF(message);
                    dos.flush();
                    dos = new DataOutputStream(socket.getOutputStream());
                    byte[] bufferSize = new byte[3];
                    is = socket.getInputStream();
                    is.read(bufferSize);
                    receivedMsg = new String(bufferSize, "UTF-8");
                    Log.d("getStatuses", receivedMsg);
                    byte[] buffer = new byte[Integer.parseInt(receivedMsg)];
                    message = "received num";
                    dos.writeUTF(message);
                    dos.flush();
                    is = socket.getInputStream();
                    is.read(buffer);
                    receivedMsg = new String(buffer, "UTF-8");
                    if (receivedMsg != null) {
                        if (!receivedMsg.equals(statuses)) {
                            statuses = receivedMsg;
                            final Semaphore mutex = new Semaphore(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String name;
                                    for (int i = 1; i <= count; i++) {
                                        name = dbHandler.getNameById(i);
                                        if (!name.equals("פנוי")) {
                                            dbHandler.UpdateUserStatus("red", i);
                                            MainActivity.setPresent(i);
                                        }
                                    }
                                    mutex.release();
                                }
                            });
                            try {
                                mutex.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String[] statuses = receivedMsg.split(" ");
                            Log.d("getStatuses", "Received statuses");
                            for (int i = 0; i < statuses.length; i++) {
                                if (statuses[i] != "" && statuses[i] != null) {
                                    dbHandler.UpdateUserStatus("green", Integer.parseInt(statuses[i]));
                                    currentStatus = Integer.parseInt(statuses[i]);
//                            setMissing(Integer.parseInt(statuses[i]));
                                    final Semaphore mutex1 = new Semaphore(0);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setMissing(currentStatus);
                                            mutex1.release();
                                        }
                                    });
                                    try {
                                        mutex1.acquire();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
//                                MainActivity.setMissing(Integer.parseInt(statuses[i]));
                                }
                            }
                        }
                    } else {
                        Log.d("getStatuses", "Error Receiving statuses");
                    }
                    socket.close();
                    Thread b = new Thread(new setTextThread());
                    b.start();
                    b.join();
                    if (checkDBUpdate==1){
                        checkDBUpdate = 0;
                        Thread c = new Thread(new updateDbThread());
                        c.start();
                        c.join();
                    }
                    if (getInMemories==5){
                        getInMemories = 0;
                        Thread d = new Thread(new updateMemoriesThread());
                        d.start();
                        d.join();
                    }
                    getInMemories++;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(14000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertToDB(){
        BufferedReader reader;
        String[] splitedLine;
        String name,phoneNumber,seatId;
        dbHandler.DeleteAll();
        try{
            final InputStream file = getAssets().open("UsersBK.txt");
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

    public void insertToDBUpdated(String[] users){
        String name,phoneNumber,seatId;
        String[] splitedLine;
        dbHandler.DeleteAll();
        for (int i=0; i<users.length; i++){
            if (i>0){
                if (users[i].equals(users[0])){
                    break;
                }
            }
            splitedLine = users[i].split(" "); //\\\s+
            if (splitedLine.length == 3){
                name = splitedLine[1];
                if (name.equals("פנוי")){
//                    setMissing(Integer.parseInt(seatId));
                    phoneNumber = "0000000000";
                } else {
                    phoneNumber = "+" + splitedLine[2];
                }
                seatId = splitedLine[0];
                dbHandler.insertUserDetails(name,phoneNumber,seatId, "red");
                setNewUsers(Integer.parseInt(seatId), name);
            } else {
//                Log.d("Check", "insertToDBUpdated: " + splitedLine[0] + " " + splitedLine[1] + " "  + splitedLine[2]);
                name = splitedLine[1] + " " + splitedLine[2];
                if (name.equals("פנוי")){
//                    setMissing(Integer.parseInt(seatId));
                    phoneNumber = "0000000000";
                } else {
                    phoneNumber = "+" + splitedLine[3];
                }
                seatId = splitedLine[0];
                dbHandler.insertUserDetails(name,phoneNumber,seatId, "red");
                setNewUsers(Integer.parseInt(seatId), name);
            }
        }
    }

    public void setNewUsers(int id, String name){
        TextView view = layout.findViewById(id);
        view.setText(dbHandler.getNameById(id));
        if (name.equals("פנוי")){
            view.setBackgroundResource(R.drawable.ic_seats_light_green);
            view.setTextColor(Color.GRAY);
            view.setTag(1);
        } else {
            view.setBackgroundResource(R.drawable.ic_seats_light_red1);
            view.setTextColor(Color.WHITE);
            view.setTag(2);
        }
    }

    public void setText(int turn) {
        String[] thisTurn = noChangeText;
        if (successfulConnection==0){
            Toast.makeText(this, "Could not connect to the server\nPlease check your internet connection and try again.", Toast.LENGTH_LONG).show();
        }
        if (turn==1 && successfulConnection==1){
            thisTurn = toWrite;
        } else if (turn==2 && successfulConnection==1) {
            thisTurn = toWrite2;
        } else{
            thisTurn = noChangeText;
        }
        Log.d("MainActivity", "setName: countX: " + countX);
        TextView view = layout.findViewById(myResources[0]);
        view.setText(BEIT_KNESET_NAME);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(null, Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setAutoSizeTextTypeUniformWithConfiguration(8, 14, 1, TypedValue.COMPLEX_UNIT_DIP);
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 8, 14, 1, TypedValue.COMPLEX_UNIT_DIP);
        }
        for (int i=1;i<countX;i++) {
            view = layout.findViewById(myResources[i]);
            view.setText(thisTurn[i]);
            view.setGravity(Gravity.CENTER);
            if (i<=2){
                view.setTypeface(null, Typeface.BOLD);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                view.setAutoSizeTextTypeUniformWithConfiguration(8, 14, 1, TypedValue.COMPLEX_UNIT_DIP);
            } else {
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 8, 14, 1, TypedValue.COMPLEX_UNIT_DIP);
            }
        }
    }

    int turn=0;
    String[] messages;
    class setTextThread implements Runnable {

        @Override
        public void run() {
//            while (true){
            if(turn==2) {
                if (toWrite2[3].equals("")) {
                    turn = 0;
                }
            }
            if(turn==0){
                try {
    //                    socket = new Socket(IP, PORT);
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(IP, PORT), 5000);
                    socket.setSoTimeout(5000);
                    if (socket == null) {
    //                        Toast.makeText(getBaseContext(), "Could not connect to the server\nPlease check your internet connection and try again.", Toast.LENGTH_LONG).show();
                        successfulConnection = 0;
                        return;
                    }
                    successfulConnection = 1;
                    dos = new DataOutputStream(socket.getOutputStream());
                    String message;
//                    if (toWrite[2].equals("")) {
//                        message = "get messages new " + (MainActivity.BEIT_KNESET_NUMBER-1);
//                    } else {
//                        message = "get messages " + (MainActivity.BEIT_KNESET_NUMBER-1);
//                    }
                    message = "0 get messages " + (MainActivity.BEIT_KNESET_NUMBER-1);
                    String receivedMsg = "";
                    dos.writeUTF(message);
                    dos.flush();
                    dos = new DataOutputStream(socket.getOutputStream());
                    byte[] bufferSize = new byte[3];
                    is = socket.getInputStream();
                    is.read(bufferSize);
                    receivedMsg = new String(bufferSize, "UTF-8");
                    Log.d("getMessages", "Received messages1: " + receivedMsg);
                    if (!receivedMsg.equals("000")) {
                        byte[] buffer = new byte[Integer.parseInt(receivedMsg)];
                        message = "received num";
                        dos.writeUTF(message);
                        dos.flush();
                        is = socket.getInputStream();
                        is.read(buffer);
                        receivedMsg = new String(buffer, "UTF-8");
                        if (receivedMsg != null) {
                            messages = receivedMsg.split("\n");
                            Log.d("getMessages", "Received messages2: " + receivedMsg);
//                            toWrite = defaultWrite;
//                            toWrite2 = defaultWrite;
                            toWrite[3] = "";
                            toWrite[4] = "";
                            toWrite[5] = "";
                            toWrite[6] = "";
                            toWrite[7] = "";
                            toWrite2[3] = "";
                            toWrite2[4] = "";
                            toWrite2[5] = "";
                            toWrite2[6] = "";
                            toWrite2[7] = "";
                            final Semaphore mutex = new Semaphore(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int z = 1; z < messages.length; z++) {
                                        if (z == 1) {
                                            toWrite[z] = "שבת פרשת: " + messages[z - 1];
                                            noChangeText[z] = toWrite[z];
                                        } else if (z > 7) {
                                            if (z == 8) {
                                                toWrite2[1] = "שבת פרשת: " + messages[0];
                                            }
                                            toWrite2[z - 5] = messages[z - 1];
                                        } else {
                                            toWrite[z] = messages[z - 1];
                                        }
                                        if (messages[z].equals(messages[0])) {
                                            break;
                                        }
                                    }
                                    mutex.release();
                                }
                            });
                            try {
                                mutex.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("getMessages", "Error Receiving messages");
                        }
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final Semaphore mutex = new Semaphore(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setText(turn);
                    mutex.release();
                }
            });
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            turn++;
            if (turn==3){turn=0;}
//                try {
//                    Thread.sleep(60000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
        }
    }
//    }

    String[] memories;
    class updateMemoriesThread implements Runnable {

        @Override
        public void run() {
                try {
                    //                    socket = new Socket(IP, PORT);
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(IP, PORT), 5000);
                    socket.setSoTimeout(5000);
                    if (socket == null) {
                        //                        Toast.makeText(getBaseContext(), "Could not connect to the server\nPlease check your internet connection and try again.", Toast.LENGTH_LONG).show();
                        successfulConnection = 0;
                        return;
                    }
                    successfulConnection = 1;
                    dos = new DataOutputStream(socket.getOutputStream());
                    String message;
                    message = "0 get memory " + (MainActivity.BEIT_KNESET_NUMBER-1);
                    String receivedMsg = "";
                    dos.writeUTF(message);
                    dos.flush();
                    dos = new DataOutputStream(socket.getOutputStream());
                    byte[] bufferSize = new byte[3];
                    is = socket.getInputStream();
                    is.read(bufferSize);
                    receivedMsg = new String(bufferSize, "UTF-8");
                    Log.d("getMemories", "Received memory1: " + receivedMsg);
                    if (!receivedMsg.equals("000")) {
                        byte[] buffer = new byte[Integer.parseInt(receivedMsg)];
                        message = "received num";
                        dos.writeUTF(message);
                        dos.flush();
                        is = socket.getInputStream();
                        is.read(buffer);
                        receivedMsg = new String(buffer, "UTF-8");
                        if (receivedMsg != null) {
                            memories = receivedMsg.split("\n");
                            Log.d("getMemories", "Received memory2: " + receivedMsg);
                            inMemoryNames[0] = "";
                            inMemoryNames[1] = "";
                            inMemoryNames[2] = "";
                            inMemoryNames[3] = "";
                            inMemoryNames[4] = "";
                            inMemoryNames[5] = "";
                            inMemoryNames[6] = "";
                            inMemoryNames[7] = "";
                            inMemoryNames[8] = "";
                            for (int z = 0; z < memories.length; z++) {
                                inMemoryNames[z] = memories[z];
                            }
                        } else {
                            Log.d("getMemories", "Error Receiving messages");
                        }
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    class updateDbThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(IP,PORT),5000);
                socket.setSoTimeout(5000);
                if (socket == null) {
                    successfulConnection = 0;
                    return;
                }
                successfulConnection=1;
                dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "0 get updates " + (MainActivity.BEIT_KNESET_NUMBER-1);
                String receivedMsg = "";
                dos.writeUTF(message);
                dos.flush();
                dos = new DataOutputStream(socket.getOutputStream());
                is = socket.getInputStream();
                byte[] buffer, bufferLen;
                String receivedMsgLong = "";
                int messagelen=0;
                while (true) {
                    bufferLen = new byte[1];
                    is.read(bufferLen);
                    receivedMsg = new String(bufferLen, "UTF-8");
                    Log.d("getUpdates", "Received 1: " + receivedMsg);
                    if (receivedMsg.equals("0")) {
                        dos.writeUTF("received");
                        dos.flush();
                        break;
                    }
                    bufferLen = new byte[Integer.parseInt(receivedMsg)];
                    dos.writeUTF("received");
                    dos.flush();
                    is.read(bufferLen);
                    receivedMsg = new String(bufferLen, "UTF-8");
                    Log.d("getUpdates",  "Received 2: " + receivedMsg);
                    messagelen = Integer.parseInt(receivedMsg)/2;
                    buffer = new byte[Integer.parseInt(receivedMsg)];
                    dos.writeUTF("received");
                    dos.flush();
                    is.read(buffer);
                    receivedMsg = new String(buffer, "UTF-8");
                    receivedMsgLong += receivedMsg.substring(0,receivedMsg.indexOf('|'));
//                    Log.i("Receiving users", Integer.toString(receivedMsg.substring(0,receivedMsg.indexOf('|')).length()));
//                    Log.i("Receiving users", receivedMsg.substring(0,receivedMsg.indexOf('|')));
                    dos.writeUTF("received");
                    dos.flush();
                }
                if (receivedMsg != null) {
                    messages = receivedMsgLong.split("\n");
                    Log.d("getUpdates", "Received updates: " + messages.length);
                    final Semaphore mutex = new Semaphore(0);
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                insertToDBUpdated(messages);
//                                try {
//                                    for (int z = 1; z < messages.length-1; z++) {
//                                        if (messages[z].equals(messages[0])){
//                                            break;
//                                        } else {
//                                            pw.append(messages[z - 1]);
//                                        }
//                                    }
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                }
                                mutex.release();
                            }
                    });
                    try {
                        mutex.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("getUpdates", "Error Receiving updates");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    if (!receivedMsg.equals("0000")) {
//        byte[] buffer = new byte[Integer.parseInt(receivedMsg)];
//        message = "received updates";
//        dos.writeUTF(message);
//        dos.flush();
//        is = socket.getInputStream();
//        is.read(buffer);
//        receivedMsg = new String(buffer, "UTF-8");
//        if (receivedMsg != null) {
//            messages = receivedMsg.split("\n");
//            Log.d("getUpdates", "Received updates: " + receivedMsg);
//            final Semaphore mutex = new Semaphore(0);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
////                                    new PrintWriter("Users1.txt").close();
//                        FileOutputStream otpFile = new FileOutputStream("Users1.txt", true);
//                        PrintWriter pw = new PrintWriter(otpFile);
//                        pw.write("");
//                        for (int z = 1; z < messages.length-1; z++) {
//                            if (messages[z].equals(messages[0])){
//                                break;
//                            } else {
//                                pw.append(messages[z - 1]);
//                            }
//                        }
//                        insertToDB();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    mutex.release();
//                }
//            });
//            try {
//                mutex.acquire();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.d("getUpdates", "Error Receiving updates");
//        }
//    }

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
//        ImageView ad1 = findViewById(R.id.ad1);
//        ImageView ad2 = findViewById(R.id.ad2);
//        ImageView ad3 = findViewById(R.id.ad3);
//        ad1.setImageResource(ads[0]);
//        ad2.setImageResource(ads[1]);
//        ad3.setImageResource(ads[2]);
//        ad1.setBackground(ContextCompat.getDrawable(this, R.drawable.ad1));
//        ad2.setBackground(ContextCompat.getDrawable(this, R.drawable.ad2));
//        ad3.setBackground(ContextCompat.getDrawable(this, R.drawable.advertise_here));
    }

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    int x=0;

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
//        int[] ads = {R.drawable.candle_test1,R.drawable.candle_test2,R.drawable.candle_test3,R.drawable.candle_test4,R.drawable.candle_test5,R.drawable.candle_test6,R.drawable.advertise_here1};
//        ImageView ad1 = findViewById(R.id.ad1);
//        ImageView ad2 = findViewById(R.id.ad2);
//        ImageView ad3 = findViewById(R.id.ad3);
//        @Override
//        public void run() {
//            while (true){
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ad1.setImageResource(ads[x]);
//                        ad2.setImageResource(ads[x+1]);
//                        ad3.setImageResource(ads[x+2]);
////                        ad1.setImageResource(ads[6]);
////                        ad2.setImageResource(ads[6]);
////                        ad3.setImageResource(ads[6]);
//                    }
//                });
//                x=x+3;
//                if (x==6){x=0;}
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        TextView candle1 = findViewById(R.id.candleName1);
        TextView candle2 = findViewById(R.id.candleName2);
        TextView candle3 = findViewById(R.id.candleName3);
        GifImageView gif1 = findViewById(R.id.ad1);
        GifImageView gif2 = findViewById(R.id.ad2);
        GifImageView gif3 = findViewById(R.id.ad3);
        int stopNum = 9;
        @Override
        public void run() {
            while (true){
                if (inMemoryNames[3]==""){stopNum=3;}
                else if(inMemoryNames[6]==""){stopNum=6;}
                else {stopNum=9;}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        candle1.setText(inMemoryNames[x]);
                        candle2.setText(inMemoryNames[x+1]);
                        candle3.setText(inMemoryNames[x+2]);
                    }
                });
                x=x+3;
                if (x==stopNum) {x=0;}
                try {
                    gif2.setImageResource(R.drawable.candle);
                    Thread.sleep(325);
                    gif1.setImageResource(R.drawable.candle);
                    Thread.sleep(275);
                    gif3.setImageResource(R.drawable.candle);
                    Thread.sleep(9400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
