/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.RequiresApi;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.wifidirect.WiFiDirectActivity.TAG;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;

    private View mContentView = null;
    private WifiP2pDevice device;
    static WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    int size = 0;
    ArrayList<String> Files,Files2;
    private String type;
    private String ext;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 0;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                        deleteRememberedWiDi();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //initialized to access file type
//                        intent.setType("image/*");   // setting file type
//                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE); // return selected code
//
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                        startActivity(intent);
                        Log.e("click","entered 3 ");
                        Files = MusicFragment.selectedItemsLoc;
                        Files2 = MusicFragment.selectedItems;
                        startTransferfiles(Files,Files2);

                    }


                });

//        startTransferfiles(MusicFragment.selectedItemsLoc);

        return mContentView;
    }





    public void startTransferfiles(ArrayList<String> Array , ArrayList<String> Array1 ) {
//        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        Log.e("click", "entered 4 ");
        size =  (Array == null) ? 0 : Array.size();
        Log.e(TAG, "arraysize----------- " + size);
        if(size > 0) {
            // Start Transfer files in location

            // FileTransferService.
            Log.e("click", "entered 5 ");
            for (int counter = 0; counter < size; counter++) {
                String send_file_loc = Array.get(counter).toString();
                String send_file_name = Array.get(counter).toString();

                Uri uri = Uri.fromFile(new File(send_file_loc));
                int last = send_file_loc.lastIndexOf('.');
                char[] ch = new char[send_file_loc.length()];

                // Copy character by character into array
                for (int i = 0; i < send_file_loc.length(); i++)
                {
                    ch[i] = send_file_loc.charAt(i);
                }

                for (int i = last; i < send_file_loc.length(); i++)
                {
                    ext = ext+ch[i];
                }
                System.out.println(ext);


                Log.e(TAG, "ftype SharedPreferences ---- " + ext);
                SharedPreferences settings = getActivity().getSharedPreferences("DataType", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ftype", ext);
                editor.commit();

//                statusText.setText("Sending: " + uri);
                Log.e("click", "entered 6 ");
                Log.e(TAG, "Name----------- " + send_file_name);
                Log.e(TAG, "Intent----------- " + uri);

                Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
                serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                Log.e("click", "entered 7 ");
                serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
                serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_NAME, send_file_name.toString());
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        info.groupOwnerAddress.getHostAddress());
                Log.e("click", "entered 8 ");
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                Log.e("click", "entered 9 ");
                getActivity().startService(serviceIntent);
            }
        }
    }


    private void deleteRememberedWiDi()
    {
        final WifiP2pManager wifiP2pManagerObj = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        final Context context = getActivity();
        final WifiP2pManager.Channel channel = wifiP2pManagerObj.initialize(context, context.getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.e("WIFIDIRECT", "Channel disconnected!");
            }
        });

        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(wifiP2pManagerObj, channel, netid, null);
                        Log.e("WIFIDIRECT", "Channel disconnected!" + channel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.e(TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);

        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }




    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        DeviceDetailFragment.info = info;
        Objects.requireNonNull(this.getView()).setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            //  Receiver
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    .execute();


        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.

            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);

            ((TextView) mContentView.findViewById(R.id.status_text)).
                    setText(getResources()
                            .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }


    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;


        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {

            this.context = context;
            this.statusText = (TextView) statusText;

        }

        // find this in receiver

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.e("click", "entered 13 ");
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.e(TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.e(TAG, "Server: connection done");
                InputStream inputstream = client.getInputStream();

                SharedPreferences settings = context.getSharedPreferences("DataType", MODE_PRIVATE);
//                String value = settings.getString("ftype", "");
//                Log.e(TAG,"SharedPreferences --- "+ value);
                String value = new DeviceDetailFragment().ext;
                Log.e(TAG,"Extension --- "+ value);

                final File f = new File
                        (
                                context.getExternalFilesDir("received"),
                                "wifip2pshared-" + System.currentTimeMillis()
                                        +value

                        );

//                final File f = new File(Environment.getExternalStorageDirectory() + "/"
//                        + "Wifidirect" + "/" + fileName);

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.e(TAG, "server: copying files " + f.toString());
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }

        }


        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
//            if (result != null) {
//                statusText.setText("File copied - " + result);
//
//                File recvFile = new File(result);
//                Uri fileUri = FileProvider.getUriForFile(
//                                context,
//                                "com.example.android.wifidirect.fileprovider",
//                                recvFile);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(fileUri, "image/*");
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                context.startActivity(intent);
//            }
            Log.e("on PostExecute Method", "File Copied --- "+ result);

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
//            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, FileOutputStream out) {
        Log.e("click", "entered 14 ");
        byte buf[] = new byte[1024];
        int len;
        try {
            Log.e("Copy File Method", "inputStream ------"+inputStream.toString());
            Log.e("Copy File Method", "FileOutputStream --- "+out.toString());
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        }
        return true;
    }

}
