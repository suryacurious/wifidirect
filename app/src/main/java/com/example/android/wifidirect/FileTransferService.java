package com.example.android.wifidirect;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    public static final String EXTRAS_FILE_NAME ="";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";


    public FileTransferService(String name)
    {
        super(name);
    }

    public FileTransferService()
    {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */

    @Override
    protected void onHandleIntent(Intent intent) {


//        if (intent.getAction().equals(ACTION_SEND_FILE))
//        {
        Log.e("click", "entered 10 ");

        String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        String filename = intent.getExtras().getString((EXTRAS_FILE_NAME));
        Log.e("Host InetAddress",host);
        Socket socket = new Socket();
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

        try {
            Log.e("click", "entered 11 ");
            Log.e(WiFiDirectActivity.TAG, "Opening client socket");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
            Log.e("Client's InetAddress","Host --- "+host);
            Log.e("Client's InetAddress","InetAddress --- "+socket.getInetAddress());

            Log.e(WiFiDirectActivity.TAG, "IS Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();
            ContentResolver cr = getApplicationContext().getContentResolver();
            InputStream is = null;
            try {
                Log.e("click", "entered 12 ");
                is = cr.openInputStream(Uri.parse(fileUri));
            } catch (FileNotFoundException e) {
                Log.e(WiFiDirectActivity.TAG, e.toString());
            }
            assert is != null;
            DeviceDetailFragment.copyFile(is, (FileOutputStream) stream);
            Log.e(WiFiDirectActivity.TAG, "Client: Data written");
//                Toast.makeText(getApplicationContext(), "Client: Data written " , Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }

//        }
    }



}

