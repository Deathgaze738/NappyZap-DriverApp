package com.example.aaron.nappyzap_driver;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class mainActivity extends AppCompatActivity implements OnMapReadyCallback{

    //hardcoded driver ID
    private int driverID = 1;

    private Fragment curFragment;
    private String[] mNav;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    //GPS Manager
    public static GPSChecker gpsChecker;
    public static CurrentPickup currentPickup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsChecker = new GPSChecker(this);
        mNav = getResources().getStringArray(R.array.mNavArray);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        if((new File("cache/currentPickup.data")).exists()){
            currentPickup.load(this);
        }
        else{
            CurrentPickup temp = new CurrentPickup();
            temp.save(this);
        }
        //Sets main fragment
        curFragment = new NavFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, curFragment)
                .commit();
        //Initialises Drawer List
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNav));
        mDrawerList.setItemChecked(0, true);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Log.d("MainActivity", "Item selected in side drawer.");
        if(position==0){
            curFragment = new NavFragment();
        }
        if(position==1) {
            curFragment = new CurrentJobFragment();
        }
        if(position==2){
            curFragment = new JobsListFragment();
        }
        if(position==3){
            logout();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, curFragment)
                .commit();
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        setTitle(mNav[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        Log.d("MainActivity", "Fragment successfully swapped");
    }
    public void logout()
    {
        Log.d("MainActivity", "Logging Out");
        Intent intent = new Intent(this, LoginPlaceholder.class);
        startActivity(intent);
    }
    @Override
    public void onMapReady(GoogleMap map){

    }


    protected void onDestroy(){
        currentPickup.save(this);
        try{
            Fragment fragment = ((Fragment) getFragmentManager().findFragmentById(R.id.map));
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
            Log.d("Fragment", "Successfully destroyed");
        }catch(Exception e){
            Log.d("Fragment", "Not destroyed");
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

