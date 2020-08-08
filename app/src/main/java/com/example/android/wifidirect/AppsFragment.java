package com.example.android.wifidirect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AppsFragment extends Fragment {


    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        adapter = new AppsAdapter(getContext(), new ApkInfoExtractor(getContext()).GetAllInstalledApkInfo());

        recyclerView.setAdapter(adapter);
        return view;
    }
}
