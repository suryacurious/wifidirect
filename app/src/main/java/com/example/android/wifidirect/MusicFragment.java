package com.example.android.wifidirect;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.android.wifidirect.WiFiDirectActivity.TAG;

public class MusicFragment extends Fragment{


    Context context;
    String[] ListElements = new String[] { };
    String[] ListElementsloc = new String[] { };
    ListView listView;
    List<String> ListElementsArrayList , ListElementslocArrayList ;
    ArrayAdapter<String> adapter ;
    ContentResolver contentResolver;
    Cursor cursor;
    Uri uri;
    Button button;
    public static ArrayList<String> selectedItems , selectedItemsLoc;
    int size=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_music, container, false);
        context = getContext();
//        listView = (ListView) view.findViewById(R.id.listView1);
        ListView chl = (ListView) view.findViewById(R.id.checkable_list);
        Button bt = (Button) view.findViewById(R.id.btshow);
        //set multiple selection mode
        chl.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));
        ListElementslocArrayList = new ArrayList<>(Arrays.asList(ListElementsloc));
//        adapter = new ArrayAdapter<String>
//                (context, android.R.layout.simple_list_item_1, ListElementsArrayList);

        //supply data itmes to ListView
        ArrayAdapter<String> aa = new ArrayAdapter<String>(context, R.layout.checkable_list_layout,
                R.id.txt_title, ListElementsArrayList);
        selectedItems = new ArrayList<String>();
        selectedItemsLoc = new ArrayList<String>();
        GetAllMediaMp3Files();

//        listView.setAdapter(adapter);
        chl.setAdapter(aa);

        chl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem); //remove deselected item from the list of selected items
                    selectedItemsLoc.remove(ListElementslocArrayList.get(position).toString());
                }
                else {
                    selectedItems.add(selectedItem); //add selected item to the list of selected items
                    selectedItemsLoc.add(ListElementslocArrayList.get(position).toString());
                }

            }

        });

        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("click","entered 1");
                showSelectedItems(v);
                Log.e("click","entered 2 ");
                Intent musicfilesintent = new Intent(getActivity().getBaseContext(), WiFiDirectActivity.class);
                getActivity().startActivity(musicfilesintent);



//                Bundle musicselectsbundle = new Bundle();
//                musicselectsbundle.putSerializable("musicselects", selectedItemsLoc);
//                Intent musicfilesintent = new Intent(getActivity().getBaseContext(), MainActivity.class);
//                musicfilesintent.putExtra("musicselects", musicselectsbundle);
//                getActivity().startActivity(musicfilesintent);


            }
        });



        // ListView on item selected listener.
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                // TODO Auto-generated method stub
//                // Showing ListView Item Click Value using Toast.
//
//                Toast.makeText(context,parent.getAdapter().getItem(position).toString(),Toast.LENGTH_LONG).show();
//                Toast.makeText(context,ListElementslocArrayList.get(position).toString(),Toast.LENGTH_LONG).show();
//
//            }
//        });

        return view;
    }

//    public void startTransferfiles(ArrayList<String> Array) {
//        try {
//            Log.e("click", "entered 3 ");
//            WifiP2pInfo info = null;
//            size = (Array == null) ? 0 : Array.size();
//            Log.e("click", "entered 4 ");
//            Log.e(WiFiDirectActivity.TAG, "arraysize----------- " + size);
//            if (size > 0) {
//                // Start Transfer files in location
//                // FileTransferService.
//                Log.e("click", "entered 5 ");
//                for (int counter = 0; counter < size; counter++) {
//                    String send_file_loc = Array.get(counter).toString();
//                    Uri uri = Uri.fromFile(new File(send_file_loc));
//                    Log.e(WiFiDirectActivity.TAG, "Intent----------- " + uri);
//                    Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
//                    serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
//                    Log.e("click", "entered 6 ");
//                    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
//                    Log.e("click", "entered 7 ");
//                    Log.e("click", info.groupOwnerAddress.getHostAddress());
//                    serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
//                            DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
//                    serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
//                    Log.e("click", "entered 7 -2 ");
//                    getActivity().startService(serviceIntent);
//                }
//
//
//
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

    public void showSelectedItems(View view){
        String selItems="";
        String selItemsloc="";
        for(String item:selectedItems){
            if(selItems=="")
                selItems=item;
            else
                selItems+="/"+item;
        }
        Toast.makeText(getContext(), selItems, Toast.LENGTH_SHORT).show();

        for(String item:selectedItemsLoc){
            if(selItemsloc=="")
                selItemsloc=item;
            else
                selItemsloc+="/"+item;
        }
        Toast.makeText(getContext(), selItemsloc, Toast.LENGTH_SHORT).show();

    }

    public void GetAllMediaMp3Files(){

        contentResolver = context.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );


        if (cursor == null) {

            Toast.makeText(context,"Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(context,"No Music Found on SD Card.", Toast.LENGTH_LONG);

        }
        else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int loc = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {

                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);

                String SongTitle = cursor.getString(Title);
                String SongLoc = cursor.getString(loc);

                // Adding Media File Names to ListElementsArrayList.
                ListElementsArrayList.add(SongTitle);
                ListElementslocArrayList.add(SongLoc);

            } while (cursor.moveToNext());
        }

    }



}
