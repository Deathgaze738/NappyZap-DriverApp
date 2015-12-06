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

public class mainActivity extends AppCompatActivity implements OnMapReadyCallback{

    //hardcoded driver ID
    private int driverID = 1;

    private Fragment curFragment;
    private GoogleMap mMap;
    private String[] mNav;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNav = getResources().getStringArray(R.array.mNavArray);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        curFragment = new NavFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, curFragment)
                .commit();

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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(curFragment);
        ft.commit();
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
        setTitle(mNav[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    public void logout()
    {
        Intent intent = new Intent(this, LoginPlaceholder.class);
        startActivity(intent);
    }
    public void mapSetUp()
    {

    }
    @Override
    public void onMapReady(GoogleMap map){

    }
}

