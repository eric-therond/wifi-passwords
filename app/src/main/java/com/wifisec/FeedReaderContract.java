package com.wifisec;
import android.provider.BaseColumns;

public class FeedReaderContract {
    public FeedReaderContract() {}

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME_WIFI = "wifi_networks";
        public static final String COLUMN_NAME_WIFI_ENTRY_ID = "id";
        public static final String COLUMN_NAME_WIFI_TITLE = "ssid";
        public static final String COLUMN_NAME_WIFI_BSSID = "bssid";
        public static final String COLUMN_NAME_WIFI_LASTSCAN = "last_scan";
        public static final String COLUMN_NAME_WIFI_SECURITY = "security";
        public static final String COLUMN_NAME_WIFI_ROBUSTNESS = "robustness";
        public static final String COLUMN_NAME_WIFI_PASSWORD = "password";
        public static final String COLUMN_NAME_WIFI_COORDINATES_X = "coordinates_x";
        public static final String COLUMN_NAME_WIFI_COORDINATES_Y = "coordinates_y";

        public static final String TABLE_NAME_PASSWORDS = "wifi_passwords";
        public static final String COLUMN_NAME_PASSWORDS_ENTRY_ID = "id";
        public static final String COLUMN_NAME_PASSWORDS_TITLE = "passwords";
    }
}
