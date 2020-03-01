package com.varun.ParkingLotApp;


import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class ReportFragment extends Fragment {

    View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_report, container, false);
        getActivity().setTitle("Report");
        String data = getArguments().getString("data");
        String name = getArguments().getString("name");
        TextView tv = myView.findViewById(R.id.dataText);
        tv.setText(data);
        TextView titleTv = myView.findViewById(R.id.titleText);
        titleTv.setText(name);
        ImageButton btn = myView.findViewById(R.id.backButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    boolean done = getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        return myView;
    }

}
