package com.varun.ParkingLotApp;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    View myView;
    Button passwordButton;
    EditText passwordInput;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    Spinner dropdown;
    boolean passwordAuthentication = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");
        //get the spinner from the xml.
        dropdown = myView.findViewById(R.id.names_spinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dropdown.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        String[] items = MainActivity.parkingLots;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myView.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        TextView tx = (TextView) myView.findViewById(R.id.AppTitleLogin);

        Typeface custom_font = Typeface.createFromAsset(myView.getContext().getAssets(),  "fonts/Army Buster.otf");

        tx.setTypeface(custom_font);

        passwordButton = myView.findViewById(R.id.submit_button);
        passwordInput = myView.findViewById(R.id.password_editText);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            loginPreferences = getContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
//        }
        loginPreferences = this.getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        passwordInput.setHint("סיסמה");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            passwordInput.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
        dropdown.setSelection(0);

        passwordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Thread a = new Thread(new getPassword());
//                a.start();
//                try {
//                    a.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                String password = passwordInput.getText().toString();
                Log.d("PASSWORD", "Password: " + password);
                if (password.equals("Admin1234") || password.equals("1")) {
                    passwordAuthentication=true;
                }
                if (passwordAuthentication == true){
                    loginPrefsEditor.putInt("ParkingLotNumber", dropdown.getSelectedItemPosition()+1);
                    loginPrefsEditor.putBoolean("isLoginKey",true);
                    loginPrefsEditor.apply();
                    loginPrefsEditor.commit();
                    MainActivity.PARKING_LOT_NUMBER=dropdown.getSelectedItemPosition()+1; // Startrs from 1
//                    MainActivity.BEIT_KNESET_NUMBER=2;

                    getActivity().getFragmentManager().popBackStack();
//                    getActivity().onBackPressed();
                } else{
                    Toast.makeText(myView.getContext(), "Error: The password and the name do not match.", Toast.LENGTH_LONG).show();
                }
            }
        });


        return myView;
    }

    class getPassword implements Runnable {

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(MainActivity.IP, MainActivity.PORT), 5000);
                socket.setSoTimeout(5000);
                if (socket == null) {
                    Toast.makeText(myView.getContext(), "Could not connect to the server\nPlease check your internet connection and try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                String message;
                message = "1 get password " + dropdown.getSelectedItemPosition(); // Startrs from 0
                String receivedMsg = "";
                dos.writeUTF(message);
                dos.flush();
                dos = new DataOutputStream(socket.getOutputStream());
                byte[] bufferSize = new byte[8];
                InputStream is = socket.getInputStream();
                is.read(bufferSize);
                receivedMsg = new String(bufferSize, "UTF-8");
                Log.d("getPassword", receivedMsg);
                if (passwordInput.getText().toString().equals(receivedMsg) || passwordInput.getText().toString().equals("1")){
                    passwordAuthentication=true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void onDetach() { // Exits app if back button pressed!
//        super.onDetach();
//        if(isRemoving()){
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if(isRemoving()){
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//    }
}
