package com.wifisec;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements interfaceFragment.OnFragmentInteractionListener {

    private FragmentManager fm;

    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = this.fm.beginTransaction();
        BlankFragment1 fr = BlankFragment1.newInstance("scan");
        fragmentTransaction.replace(R.id.fragment_place, fr);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = this.fm.beginTransaction();

        if (id == R.id.action_show) {
            BlankFragment1 fr1 = BlankFragment1.newInstance("scan");
            fragmentTransaction.replace(R.id.fragment_place, fr1);
        }
        else if(id == R.id.action_vulnerables) {
            BlankFragment1 fr1 = BlankFragment1.newInstance("show_vulnerables");
            fragmentTransaction.replace(R.id.fragment_place, fr1);
        }
        else if(id == R.id.action_delete_vulnerables) {
            BlankFragment1 fr1 = BlankFragment1.newInstance("delete_vulnerables");
            fragmentTransaction.replace(R.id.fragment_place, fr1);
        }
        else if(id == R.id.action_settings) {
            BlankFragment2 fr2 = BlankFragment2.newInstance();
            fragmentTransaction.replace(R.id.fragment_place, fr2);
        }
        else if(id == R.id.action_quit) {
            finish();
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        return super.onOptionsItemSelected(item);
    }
}
