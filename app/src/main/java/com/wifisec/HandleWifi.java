package com.wifisec;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HandleWifi extends AsyncTask<Void, Void, Void> {

    private Context context;
    public MyAdapter adapter;
    WifiManager wifimanager;
    WifiScanReceiver wifireceiver;
    List<ScanResult> wifiScanList;
    double location_x;
    double location_y;
    String type;

    public HandleWifi(Context context, String type)
    {
        Log.w("TEST_SEC_WIFI", "HandleWifi constructeur HandleWifi"+type);
        this.type = type;
        this.context = context;
        this.adapter = new MyAdapter(context, type);
        this.wifimanager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.wifireceiver = new WifiScanReceiver();

        IntentFilter filter1 = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter1.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.context.registerReceiver(wifireceiver, filter1);
        IntentFilter filter2 = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.context.registerReceiver(wifireceiver, filter2);
        IntentFilter filter3 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter3.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.context.registerReceiver(wifireceiver, filter3);

        if(this.type == "scan")
            getWifis();
        else if(this.type == "delete_vulnerables")
            deleteVulnerablesWifis();
        else if(this.type == "show_vulnerables")
            getVulnerablesWifis();

        this.adapter.callbackwifi = this;
    }

    public void deleteVulnerablesWifis()
    {
        adapter.wifis.clear();
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this.context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME_WIFI, null, null);
        adapter.notifyDataSetChanged();
    }

    public boolean PasswordsWifis(String ssid, String bssid)
    {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this.context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteDatabase db2 = mDbHelper.getWritableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE
        };

        String sortOrder = FeedReaderContract.FeedEntry._ID + " DESC";

        Cursor c = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME_PASSWORDS,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );


        while (c.moveToNext()) {
            long itemId = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String password = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORDS_TITLE));

            if(this.TestPasswordsWifis(this.context, bssid, ssid, password)) {

                Log.w("TEST_SEC_WIFI", "add entry TestPasswordsWifis");
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_TITLE, ssid);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_BSSID, bssid);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_LASTSCAN, ssid);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_SECURITY, 1);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_ROBUSTNESS, 0);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_PASSWORD, password);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_ROBUSTNESS, 0);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_X, this.location_x);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_Y, this.location_y);
                long newRowId = db2.insert(FeedReaderContract.FeedEntry.TABLE_NAME_WIFI, null, values);

                return true;
            }
        }

        return false;
    }

    void getVulnerablesWifis() {

        Log.w("TEST_SEC_WIFI","getVulnerablesWifis");
        adapter.wifis.clear();
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this.context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_TITLE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_BSSID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_SECURITY,
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_PASSWORD,
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_X,
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_Y
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry._ID + " DESC";

        Cursor c = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME_WIFI,  // The table to query
                projection,                               // The columns to return
                FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_ROBUSTNESS + " = 0",  // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while (c.moveToNext()) {
            long itemId = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String title = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_TITLE));
            String bssid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_BSSID));
            int security = c.getInt(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_SECURITY));
            String password = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_PASSWORD));
            double coordinates_x = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_X));
            double coordinates_y = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_COORDINATES_Y));

            adapter.wifis.add(new WifiSec(title, bssid, security, password, coordinates_x, coordinates_y));
            Log.w("TEST_SEC_WIFI", "getVulnerablesWifis add = "+title);
        }

        adapter.notifyDataSetChanged();
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void onReceive(Context c, Intent intent) {

            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected()) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {

                        List<WifiConfiguration> item = wifimanager.getConfiguredNetworks();
                        int i = item.size();
                        Iterator<WifiConfiguration> iter =  item.iterator();
                        WifiConfiguration config = item.get(0);

                        Log.w("TEST_SEC_WIFI","est connecté");
                        Log.w("TEST_SEC_WIFI", "SSID" + config.SSID);
                        Log.w("TEST_SEC_WIFI", "PASSWORD" + config.preSharedKey);



                    }
                }
            } else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                        !networkInfo.isConnected()) {
                    // Wifi is disconnected
                }
            }
            else if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                wifiScanList = wifimanager.getScanResults();

                for (int i = 0; i < wifiScanList.size(); i++) {
                    if (!(((wifiScanList.get(i)).SSID).isEmpty())) {

                        Log.w("TEST_SEC_WIFI", "scan wifi = " + ((wifiScanList.get(i)).SSID));

                        if(check_security(wifiScanList.get(i).capabilities) == 1)
                            adapter.wifis.add(new WifiSec(((wifiScanList.get(i)).SSID),
                                ((wifiScanList.get(i)).BSSID),
                                    check_security(wifiScanList.get(i).capabilities),
                                    "", location_x, location_y
                        ));
                    }
                }

                    adapter.notifyDataSetChanged();

            }
        }
    }

    public boolean isConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if(networkInfo.isConnected())
                return true;
        }

        return false;
    }

    public class Foo implements Runnable {

        public Context tmpcontext;
        public String bssid;
        public String ssid;
        public String password;

        private volatile boolean value = false;

        public boolean getValue() {
            return value;
        }
        @Override
        public void run() {
            try {

                if (!wifimanager.isWifiEnabled()) {
                    wifimanager.setWifiEnabled(true);
                }

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + ssid + "\"";
                conf.preSharedKey = "\"" + password + "\"";

                int ret = wifimanager.addNetwork(conf);
                boolean returne = wifimanager.enableNetwork(ret, true);
                wifimanager.saveConfiguration();
                wifimanager.reconnect();

                for(int i = 0; i < 7; i ++) {
                    Log.w("TEST_SEC_WIFI", "isConnected i = " + i);
                    if (isConnected(tmpcontext)) {
                        value = true;
                        break;
                    }

                    Thread.sleep(1000);
                }

            } catch (Exception e) {
            }
        }
    };

    public boolean TestPasswordsWifis(final Context tmpcontext, final String bssid, final String ssid, final String password) {

        this.clearWifi();
        Log.w("TEST_SEC_WIFI", "1 PasswordsWifis ssid = " + ssid);

        Foo foo = new Foo();

        foo.tmpcontext = tmpcontext;
        foo.bssid = bssid;
        foo.ssid = ssid;
        foo.password = password;

        Thread t = new Thread(foo);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.w("TEST_SEC_WIFI", "2 PasswordsWifis foo.getValue() = " + foo.getValue());
        return foo.getValue();
    }

    private int check_security(String capabilities)
    {
        if (capabilities.toLowerCase().contains("WEP".toLowerCase())
                || (capabilities.toLowerCase().contains("WPA2".toLowerCase()))
                || (capabilities.toLowerCase().contains("WPA".toLowerCase())))
            return 1;

        return 0;
    }

    public boolean clearWifi() {
        if (!disconnectAP()) {
            return false;
        }
        // Disable Wifi
        if (!this.wifimanager.setWifiEnabled(false)) {
            return false;
        }
        // Wait for the actions to be completed
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {}
        return true;
    }

    public boolean disconnectAP() {
        if (this.wifimanager.isWifiEnabled()) {
            //remove the current network Id
            WifiInfo curWifi = this.wifimanager.getConnectionInfo();
            if (curWifi == null) {
                return false;
            }
            int curNetworkId = curWifi.getNetworkId();
            this.wifimanager.removeNetwork(curNetworkId);
            this.wifimanager.saveConfiguration();

            // remove other saved networks
            List<WifiConfiguration> netConfList = this.wifimanager.getConfiguredNetworks();
            if (netConfList != null) {
                for (int i = 0; i < netConfList.size(); i++) {
                    WifiConfiguration conf = new WifiConfiguration();
                    conf = netConfList.get(i);
                    this.wifimanager.removeNetwork(conf.networkId);
                }
            }
        }
        this.wifimanager.saveConfiguration();
        return true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        this.location_x = lastKnownLocation.getLatitude();
        this.location_y = lastKnownLocation.getLongitude();

        this.wifimanager.startScan();
        return null;
    }

    void getWifis() {
        adapter.wifis.clear();
        this.execute();
        Log.w("TEST_SEC_WIFI", "requetewifi getWifis ");
    }

    protected void onPostExecute()
    {

    }
}
