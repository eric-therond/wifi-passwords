package com.wifisec;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    public ArrayList<WifiSec> wifis;
    private Context context;
    public HandleWifi callbackwifi;

    public MyAdapter(Context context, String type) {
            this.context = context;
            this.wifis = new ArrayList<WifiSec>();
        }

    @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.wifi_item, parent, false);
            return new MyAdapter.ViewHolder(context, itemView);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            WifiSec wifi = wifis.get(position);

            if(wifi.wifi_security == 1)
                holder.wifi_icon.setImageResource(R.mipmap.ic_wifi_secure);
            else
                holder.wifi_icon.setImageResource(R.mipmap.ic_wifi_normal);

            holder.wifi_title.setText(Html.fromHtml(wifi.wifi_title));
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        public int getItemCount() {
            if(wifis == null)
                return 0;

            return wifis.size();
        }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView wifi_icon;
        public TextView wifi_title;
        private Context context;

        public ViewHolder(Context context, View itemView)
        {
            super(itemView);
            this.wifi_title = (TextView) itemView.findViewById(R.id.wifi_title);
            this.wifi_icon = (ImageView) itemView.findViewById(R.id.imageView);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String str = "password not found";
            int position = getLayoutPosition();
            WifiSec wifi = wifis.get(position);

            if(callbackwifi != null) {
                Log.w("TEST_SEC_WIFI", "callbackwifi not null");
                if(callbackwifi.type == "show_vulnerables")
                {
                    Log.w("TEST_SEC_WIFI", "callbackwifi show_vulnerables");
                    Toast.makeText(context, Html.fromHtml("ssid : " + wifi.getssid()
                            + "<br>password : " + wifi.getpassword()
                            + "<br>coordinates_x : " + wifi.getcoordinatesx()
                            + "<br>coordinates_y : " + wifi.getcoordinatesy()), Toast.LENGTH_SHORT).show();
                }
                else {
                Log.w("TEST_SEC_WIFI", "callbackwifi scan");
                    if (callbackwifi.PasswordsWifis(wifi.getssid(), wifi.getbssid()))
                        str = "password found";

                    Toast.makeText(context, Html.fromHtml(wifi.getssid() + " : " + str), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
