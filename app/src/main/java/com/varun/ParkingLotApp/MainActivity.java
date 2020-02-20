package com.varun.ParkingLotApp;

import android.Manifest;
import android.animation.Animator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
//import com.getbase.floatingactionbutton.FloatingActionButton;
//import com.getbase.floatingactionbutton.FloatingActionsMenu;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static ViewGroup layout;

    public String spots =
            "______________/"
                    + "___X____/"
                    + "AAAAAAAAAAAAAA/"
                    + "AAAAAAAAAAAAAA/"
                    + "AAAAAAAAAAAAAA/";

    public String spots1 =
            "_/"
                    + "X/"
                    + "A/"
                    + "A/"
                    + "A/";


    public String spots2 =
            "X/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/"
                    + "AAAAAAAAAA/";

    public String spots3 =
                    "______________/"
                    + "___X____/"
                    + "______A_______/"
                    + "______A_______/"
                    + "______A_______/";

    List<TextView> spotViewList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    int spotSize = 35;
    int spotGapingH = 1;
    int spotGapingV = 0;
    static int PARKING_LOT_NUMBER = 0;
    String PARKING_LOT_NAME = "";
    static String[] parkingLots = {"חניון נייק", "חניון גוונים", "חניון גוגל"};
    DbHandler dbHandler;
//        static public final String IP = "192.168.43.43"; // Phone Router
//    static public final String IP = "10.100.102.212"; // Home Router
    static public final String IP = "myseatingapp.ddns.net"; // Home Router
    static public final int PORT = 443;
    public Socket socket;
    public DataOutputStream dos;
    InputStream is;

    int STATUS_MISSING = 1;
    int STATUS_PRESENT = 2;
    int UPDATE_spotS = 4;
    int RESET_spotS = 5;
    String selectedIds = "";
    boolean isFABOpen = false;
    FloatingActionButton fab, fab1, fab2, fab3;
    LinearLayout fabLayout1, fabLayout2, fabLayout3;
    View fabBGLayout;

    int[] myResources = {R.id.NameText, R.id.ShabbatParasha, R.id.HeadLineText, R.id.FirstText, R.id.SecondText, R.id.ThirdText, R.id.FourthText, R.id.FifthText};
    static int count, countAll, countX, first1, first2, first3;
    static int backgroundCount = 0, successfulConnection = 0;
    static int currentStatus = 0, checkDBUpdate = 0, checkDBUpdateFirst = 1, currParkingId,statusListType=0;
    String statuses = "";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);
        }
//        checkForSmsPermission();
//        String data = dbHelper.getData();
//        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        dbHandler = new DbHandler(this);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        dbHandler.onUpgrade(db,2,3);
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
//        String spotIdTest = dbHandler.searchByPN("0549766185");
//        String spotIdTest = dbHandler.spotIdFromPN("+972549766158");
//        dbHandler.UpdateUserStatus("red", 210);
        String data;
//        Toast.makeText(this, "data: " +data, Toast.LENGTH_LONG).show();
        dbHandler.DeleteAll();
        insertToDB();
        data = dbHandler.getData();
        if (data.equals("")) {
            insertToDB();
        }
        layout = findViewById(R.id.layoutSpot);
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Toast.makeText(this, "saved: " + prefs.getInt("ParkingLotNumber", 0), Toast.LENGTH_LONG).show();
        if (prefs.getBoolean("isLoginKey", false) && prefs.getInt("ParkingLotNumber", 0)!=0) {
            PARKING_LOT_NUMBER = prefs.getInt("ParkingLotNumber", 0);
        } else {
            replaceFragment();
        }

        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fabBGLayout = findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListType=2; // day
                Thread download = new Thread(new getStatusList());
                download.start();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListType=1; // week
                Thread download = new Thread(new getStatusList());
                download.start();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListType=0; // month
                Thread download = new Thread(new getStatusList());
                download.start();
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        Thread setup = new Thread(new setupThread());
        setup.start();
    }

    class setupThread implements Runnable {

        @Override
        public void run() {
            while (PARKING_LOT_NUMBER == 0) {
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
                    setspots();
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

    boolean replaceFragment() {
        Fragment myFragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, myFragment);
        fragmentTransaction.addToBackStack(myFragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        return true;
    }

    public void setspots() {
//        if (PARKING_LOT_NUMBER == 1){
//            PARKING_LOT_NAME = PARKING_LOT_NAME1;
//            spots = spots1;
//        } else if (PARKING_LOT_NUMBER==2){
//            PARKING_LOT_NAME = PARKING_LOT_NAME2;
//            spots = spots2;
//        } else if(PARKING_LOT_NUMBER==3){
//            PARKING_LOT_NAME = PARKING_LOT_NAME3;
//            spots = spots3;
//        }
        PARKING_LOT_NAME = parkingLots[PARKING_LOT_NUMBER - 1];
        spots = spots2; // Do like the parking_lot_name...

        int spotHeight = (Resources.getSystem().getDisplayMetrics().heightPixels) / 10;
        int spotWidth = (Resources.getSystem().getDisplayMetrics().widthPixels) / 11+5;
        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels));
        Log.e("Screen size", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels / 24));

//        layout = findViewById(R.id.layoutspot);

        spots = "/" + spots;
        float additionByScreen = getResources().getDimension(R.dimen.text_size);

        LinearLayout layoutspot = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutspot.setOrientation(LinearLayout.VERTICAL);
        layoutspot.setLayoutParams(params);
//        layoutspot.setPadding(6 * spotGapingH, 8 * spotGapingV, 6 * spotGapingH, 8 * spotGapingV);
        layout.addView(layoutspot);
//        layout.setBackgroundResource(R.drawable.b7);

        LinearLayout layout = null;
        count = 0;
        countAll = 0;
        countX = 0;
        int size = (spotWidth + spotGapingH + spotGapingH) * 10;

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

//        Log.d("TEST", "setspots: " + dbHandler.getData());
        for (int index = 0; index < spots.length(); index++) {
            if (spots.charAt(index) == '/') {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutspot.addView(layout);
            } else if (spots.charAt(index) == 'U') {
                count++;
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(spotSize, spotSize);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(spotWidth, spotHeight);
//                layoutParams.weight = 1;
                layoutParams.setMargins(spotGapingH, spotGapingV, spotGapingH, spotGapingV);
                view.setLayoutParams(layoutParams);
                view.setTypeface(null, Typeface.BOLD);
//                view.setPadding(0, 0, 0, 2 * spotGapingV);
                view.setPadding(0, 0, 0, 0);
                view.setId(count);
                view.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                view.setBackgroundResource(R.drawable.ic_car_red);
//                view.setBackgroundColor(Color.RED);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_PRESENT);
//                view.setText(count + "");
//                view.setText("מלכה");
                view.setText(dbHandler.nameFromId(count));
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, spotWidth / 13 + additionByScreen); //spotWidth/13+4
                layout.addView(view);
                spotViewList.add(view);
                view.setOnClickListener(this);
            } else if (spots.charAt(index) == 'A') {
                count++;
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(spotSize, spotSize);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(spotWidth, spotHeight);
//                layoutParams.weight = 1;
                layoutParams.setMargins(spotGapingH, spotGapingV, spotGapingH, spotGapingV);
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 12, 0, 2 * spotGapingV);
                view.setId(count);
                view.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                view.setBackgroundResource(R.drawable.ic_car_green_1);
//                view.setBackgroundColor(Color.GREEN);
//                view.setText(count + "");
//                view.setText(dbHandler.getNameById(count));
                view.setText(dbHandler.nameFromId(count));
//                view.setText("13A");
//                view.setAutoSizeTextTypeUniformWithConfiguration(3,10,1,TypedValue.COMPLEX_UNIT_DIP);
//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view, 3, 5, 1, TypedValue.COMPLEX_UNIT_DIP);
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, spotWidth / 13 + additionByScreen); //spotWidth/13+4
                view.setTextColor(Color.BLACK);
                view.setTag(STATUS_MISSING);
                layout.addView(view);
                spotViewList.add(view);
                view.setOnClickListener(this);
            } else if (spots.charAt(index) == 'X') {
                countAll++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(size, spotHeight + spotGapingH + spotGapingH);
                layoutParams.setMargins(0, 0, 0, 0);
                view.setLayoutParams(layoutParams);
//                view.setBackgroundColor(getResources().getColor(R.color.veryLiightGray));
                view.setId(R.id.NameText);
                view.setText(PARKING_LOT_NAME);
                view.setTextColor(Color.BLACK);
                view.setGravity(Gravity.CENTER);
                view.setTypeface(null, Typeface.BOLD);
                view.setOnClickListener(this);
                layout.addView(view);
                countX++;
            } else if (spots.charAt(index) == 'B') {
                countAll++;
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(size, spotHeight + spotGapingH + spotGapingH);
                layoutParams.setMargins(0, 0, 0, 0);
                view.setLayoutParams(layoutParams);
//                view.setBackgroundColor(getResources().getColor(R.color.veryLiightGray));
                view.setText("");
                layout.addView(view);
                view.setOnClickListener(this);
            } else if (spots.charAt(index) == '_') {
                countAll++;
                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(spotSize, spotSize);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(spotWidth, spotHeight);
//                layoutParams.weight = 1;
                layoutParams.setMargins(spotGapingH, spotGapingV, spotGapingH, spotGapingV);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setText("");
                layout.addView(view);
            }
        }

        Thread getStatusesThread = new Thread(new getStatusesThread());
        getStatusesThread.start();
    }

    public static void setMissing(int id) {
        TextView view = layout.findViewById(id);
        view.setBackgroundResource(R.drawable.ic_car_green_1);
        view.setTextColor(Color.BLACK);
        view.setTag(1);
    }

    public static void setPresent(int id) {
        TextView view = layout.findViewById(id);
        view.setBackgroundResource(R.drawable.ic_car_red);
        view.setTextColor(Color.BLACK);
        view.setTag(2);
    }


    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onClick(View view) {
        currParkingId = view.getId();
        Log.d("ONCLICK: ", "currId: " + currParkingId);
        Thread getUserThread = new Thread(new getUserThread());
        getUserThread.start();
//        int[] backgrounds = {R.drawable.b1,R.drawable.b2,R.drawable.b3,R.drawable.b4,R.drawable.b5,R.drawable.b6,R.drawable.b7,R.drawable.b8,R.drawable.b9,R.drawable.b10, R.drawable.b11, R.drawable.b12, R.drawable.b13};
//        backgroundCount++;
//        if (backgroundCount>backgrounds.length-1){backgroundCount=0;}
//        ConstraintLayout layout =(ConstraintLayout) findViewById(R.id.main_layout);
//        layout.setBackgroundResource(backgrounds[backgroundCount]);
//        Toast.makeText(this, dbHandler.getNameById(view.getId()), Toast.LENGTH_SHORT).show();
//        if ((int) view.getTag() == RESET_spotS) {
//            checkDBUpdate=1;
//            Toast.makeText(this, "Checking for updates in DB", Toast.LENGTH_SHORT).show();
//        }
//        TextView tv1 = (TextView) view;
//        if ((int) view.getTag() == STATUS_PRESENT) {
//            Toast.makeText(this, dbHandler.getNameById(view.getId()), Toast.LENGTH_SHORT).show();
//            view.setTag(STATUS_MISSING);
//            view.setBackgroundResource(R.drawable.ic_spots_light_green);
//            tv1.setTextColor(Color.GRAY);
//        } else if ((int) view.getTag() == UPDATE_spotS) {
//            Thread getStatusesThread = new Thread(new getStatusesThread());
//            getStatusesThread.start();
//        } else if((int) view.getTag() == RESET_spotS) {
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
//            view.setBackgroundResource(R.drawable.ic_spots_light_red);
//            tv1.setTextColor(Color.WHITE);
//        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                }
/*                if (fab.getRotation() != -180) {
                    fab.setRotation(-180);
                }*/
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    class getUserThread implements Runnable {

        @Override
        public void run() {
            try {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(IP, PORT), 5000);
                }   catch (SocketTimeoutException e){
//                    Snackbar.make(findViewById(R.id.main_layout), "Failed to connect to the internet, please try again later!", Snackbar.LENGTH_LONG).show();
                    successfulConnection = 0;
                    return;
                }
                successfulConnection = 1;
                dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "1 get user " + Integer.toString(currParkingId) + " " + (MainActivity.PARKING_LOT_NUMBER - 1);
                String receivedMsg = "";
                dos.writeUTF(message);
                dos.flush();
                dos = new DataOutputStream(socket.getOutputStream());
                is = socket.getInputStream();
                byte[] buffer, bufferLen;
                String receivedMsgLong = "";
                int messagelen = 0;
                bufferLen = new byte[2];
                is.read(bufferLen);
                receivedMsg = new String(bufferLen, "UTF-8");
                Log.d("getUser", "Received 1: " + receivedMsg);
                if (receivedMsg.equals("00")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Parking spot not taken",Toast.LENGTH_LONG).show();
                        }
                    });
                    dos.writeUTF("received");
                    dos.flush();
                } else {
                    buffer = new byte[Integer.parseInt(receivedMsg.trim())];
                    dos.writeUTF("received");
                    dos.flush();
                    is.read(buffer);
                    receivedMsg = new String(buffer, "UTF-8");
                    dos.writeUTF("received");
                    dos.flush();
                    if (receivedMsg != null) {
                        final Semaphore mutex = new Semaphore(0);
                        final String name = receivedMsg;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, dbHandler.nameFromId(currParkingId)+": " + name,Toast.LENGTH_LONG).show();
                                mutex.release();
                            }
                        });
                        try {
                            mutex.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("getUser", "Error Receiving user");
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class getStatusesThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    if (checkDBUpdateFirst == 1) {
                        checkDBUpdateFirst = 0;
                        Thread c = new Thread(new updateDbThread());
                        c.start();
                        c.join();
                    }
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(IP, PORT), 5000);
                    }   catch (SocketTimeoutException e){
//                        Snackbar.make(findViewById(R.id.fab), "Failed to connect to the internet, please try again later!", Snackbar.LENGTH_LONG).show();
                        successfulConnection = 0;
                        return;
                    }
//                    socket.setSoTimeout(5000);
//                    if (socket == null) {
//                        Snackbar snackbar = Snackbar
//                                .make(getWindow().getDecorView().getRootView(), "Failed to connect to the internet, please try again!", Snackbar.LENGTH_LONG)
//                                .setAction("RETRY", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Thread a = new Thread(new getStatusList());
//                                        a.start();
//                                        successfulConnection = 0;
//                                        return;
//                                    }
//                                });
//                        snackbar.show();
//                        Toast.makeText(MainActivity.this, "Parking spot not taken",Toast.LENGTH_LONG).show();
//                        successfulConnection = 0;
//                        return;
//                    }
                    successfulConnection=1;
                    dos = new DataOutputStream(socket.getOutputStream());
                    String message;
                    message = "1 get statuses " + (MainActivity.PARKING_LOT_NUMBER - 1);
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
                        if (!receivedMsg.equals(statuses) || 1==1) {
                            statuses = receivedMsg;
                            final Semaphore mutex = new Semaphore(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 1; i <= count; i++) {
                                        dbHandler.UpdateUserStatus("green", i);
                                        MainActivity.setMissing(i);

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
                                    dbHandler.UpdateUserStatus("red", Integer.parseInt(statuses[i]));
                                    currentStatus = Integer.parseInt(statuses[i]);
//                            setMissing(Integer.parseInt(statuses[i]));
                                    final Semaphore mutex1 = new Semaphore(0);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setPresent(currentStatus);
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
                    Thread.sleep(15000);
//                    checkDBUpdate++;
//                    if (checkDBUpdate == 8) {
//                        checkDBUpdate = 0;
//                        Thread c = new Thread(new updateDbThread());
//                        c.start();
//                        c.join();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertToDB() {
        BufferedReader reader;
        String[] splitedLine;
        String name;
        int id;
        dbHandler.DeleteAll();
        try {
            final InputStream file = getAssets().open("UsersPL.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while (line != null) {
                splitedLine = line.split("\\t"); // 1    12A
                id = Integer.parseInt(splitedLine[0].replaceAll("\\uFEFF", ""));
                name = splitedLine[1];
                dbHandler.insertUserDetails(id,name, "green");
                line = reader.readLine();
            }
            Toast.makeText(this, "Inserted new users", Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void insertToDBUpdated(String[] users) {
        String name;
        int spotId;
        String[] splitedLine;
        dbHandler.DeleteAll();
        for (int i = 0; i < users.length; i++) {
            if (i > 0) {
                if (users[i].equals(users[0])) {
                    break;
                }
            }
            splitedLine = users[i].split("\\s+"); //\\\s+
            name = splitedLine[1];
            spotId = i+1;
//            spotId = i;
            dbHandler.insertUserDetails(spotId,name, "green");
            setNewUsers(spotId, name);
        }
    }

    public void setNewUsers(int id, String name) {
        TextView view = layout.findViewById(id);
//        view.setText(dbHandler.getNameById(id));
        view.setText(name);
//        if (name.equals("פנוי")) {
//            view.setBackgroundResource(R.drawable.ic_seats_light_green);
//            view.setTextColor(Color.GRAY);
//            view.setTag(1);
//        }
        view.setBackgroundResource(R.drawable.ic_car_green_1);
        view.setTextColor(Color.GRAY);
        view.setTag(1);
    }

    int turn = 0;
    String[] messages;

    class updateDbThread implements Runnable {

        @Override
        public void run() {
            try {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(IP, PORT), 5000);
                }   catch (SocketTimeoutException e){
                    FloatingActionButton fabAdd = findViewById(R.id.fab);
                    Snackbar.make(fabAdd, "Failed to connect to the internet, please try again later!", Snackbar.LENGTH_LONG).show();
                    successfulConnection = 0;
                    return;
                }
                successfulConnection = 1;
                dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "1 get updates " + (MainActivity.PARKING_LOT_NUMBER - 1);
                String receivedMsg = "";
                dos.writeUTF(message);
                dos.flush();
                dos = new DataOutputStream(socket.getOutputStream());
                is = socket.getInputStream();
                byte[] buffer, bufferLen;
                String receivedMsgLong = "";
                int messagelen = 0;
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
                    Log.d("getUpdates", "Received 2: " + receivedMsg);
                    messagelen = Integer.parseInt(receivedMsg) / 2;
                    buffer = new byte[Integer.parseInt(receivedMsg)];
                    dos.writeUTF("received");
                    dos.flush();
                    is.read(buffer);
                    receivedMsg = new String(buffer, "UTF-8");
                    receivedMsgLong += receivedMsg.substring(0, receivedMsg.indexOf('|'));
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

    class getStatusList implements Runnable {

        @Override
        public void run() {
            try {
                if (statusListType!=0 && statusListType!=1 && statusListType!=2){return;}
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(IP, PORT), 5000);
                }   catch (SocketTimeoutException e){
//                    Snackbar.make(findViewById(R.id.main_layout), "Failed to connect to the internet, please try again later!", Snackbar.LENGTH_LONG).show();
                    successfulConnection = 0;
                    return;
                }
                successfulConnection = 1;
                dos = new DataOutputStream(socket.getOutputStream());
                final String message;
//                String fileName="";
//                String plName = parkingLots[MainActivity.PARKING_LOT_NUMBER - 1];
//                Date currentTime = Calendar.getInstance().getTime();
//                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
//                String formattedDate = df.format(currentTime);
//                if (statusListType==0){fileName="monthly_statuses_" + formattedDate + "_" + plName + ".txt";}
//                else if (statusListType==1){fileName="weekly_statuses_" + plName + "_" + formattedDate + ".txt";}
//                else if (statusListType==2){fileName="daily_statuses_" + plName + "_" + formattedDate + ".txt";}
                message = "1 get lists " + statusListType + " " + (MainActivity.PARKING_LOT_NUMBER - 1);
                String receivedMsg = "";
                dos.writeUTF(message);
                dos.flush();
                dos = new DataOutputStream(socket.getOutputStream());
                is = socket.getInputStream();
                byte[] buffer, bufferLen;
                String receivedMsgLong = "";
                int messagelen = 0;
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
                    Log.d("getUpdates", "Received 2: " + receivedMsg);
                    messagelen = Integer.parseInt(receivedMsg) / 2;
                    buffer = new byte[Integer.parseInt(receivedMsg)];
                    dos.writeUTF("received");
                    dos.flush();
                    is.read(buffer);
                    receivedMsg = new String(buffer, "UTF-8");
                    receivedMsgLong += receivedMsg.substring(0, receivedMsg.indexOf('|'));
//                    Log.i("Receiving statuses", Integer.toString(receivedMsg.substring(0,receivedMsg.indexOf('|')).length()));
//                    Log.i("Receiving statuses", receivedMsg);
                    dos.writeUTF("received");
                    dos.flush();
                }
                if (receivedMsg != null) {
//                    messages = receivedMsgLong.split("\n");
                    final String messagesConnected = receivedMsgLong;
                    Log.d("getUpdates", "Received updates: " + messages.length);
                    final Semaphore mutex = new Semaphore(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            writeToFile(messagesConnected);
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

    private void writeToFile(String data) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//            Toast.makeText(this, "Saved in ", Toast.LENGTH_LONG).show();
//        }
        requestAppPermissions();
        String plName = parkingLots[MainActivity.PARKING_LOT_NUMBER - 1];
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
        String formattedDate = df.format(currentTime);
        String fileName = plName + "_" + formattedDate + ".txt";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, "Status Files");
        if (!file.exists()) {
            Log.d("TEST", "writeToFile: 1");
            file.mkdir();
        }
        if (statusListType==0){file = new File(file,"Monthly Statuses");}
        else if (statusListType==1){file = new File(file,"Weekly Statuses");}
        else if (statusListType==2){file = new File(file,"Daily Statuses");}
        else {Toast.makeText(this, "Error saving the file.", Toast.LENGTH_LONG).show(); return;}
        if (!file.exists()) {
            Log.d("TEST", "writeToFile: 2");
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.write(data);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Statuses saved successfully\n" + file.toString() +"/"+ fileName, Toast.LENGTH_LONG).show();}
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());

        }
    }
}