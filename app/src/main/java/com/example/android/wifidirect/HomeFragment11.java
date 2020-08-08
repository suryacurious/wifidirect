package com.example.android.wifidirect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class HomeFragment11 extends Fragment {
    ListView lv;
    List<String> fileList = new ArrayList<String>();
    public HomeFragment11() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_apps, container, false);
    }

}
