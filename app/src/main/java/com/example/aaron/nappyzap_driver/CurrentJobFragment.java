package com.example.aaron.nappyzap_driver;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

/**
 * Created by Aaron on 05/12/2015.
 */
public class CurrentJobFragment extends Fragment {

    TextView fullName;
    TextView address;
    TextView details;
    TextView size;
    Button request;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_job_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment
        fullName = (TextView) getView().findViewById(R.id.textFullName);
        address = (TextView) getView().findViewById(R.id.textAddress);
        details = (TextView) getView().findViewById(R.id.textDetails);
        size = (TextView) getView().findViewById(R.id.textSize);
        request = (Button) getView().findViewById(R.id.btnRequest);
        request.setOnClickListener(requestClicked);
        populateView();
    }


    @Override
    public void onDestroyView ()
    {
        try{
            MapFragment fragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.content_frame));
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }catch(Exception e){
        }
        super.onDestroyView();
    }

    private void populateView(){
        //Get Text Views
        fullName.setText(mainActivity.currentPickup.name+", "+mainActivity.currentPickup.phoneNo);
        address.setText(mainActivity.currentPickup.binLocation + ", " + mainActivity.currentPickup.address);
        details.setText(mainActivity.currentPickup.details);
        size.setText(String.valueOf(mainActivity.currentPickup.sizeOfPickup));
        setRequestBtn(request);
    }

    private void setRequestBtn(View view){
        //Request or Complete pickup
        Boolean requestFlag = mainActivity.currentPickup.complete;
        if(requestFlag){
            request.setText("Complete Pickup");
        }
        else if(!requestFlag){
            request.setText("Request New Pickup");
        }
    }

    View.OnClickListener requestClicked = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("RequestClick", "Entered View");
            if(!mainActivity.currentPickup.complete){
                Log.d("RequestClick", "Requesting a new pickup...");
                requestNewPickup();
            }
            else if(mainActivity.currentPickup.complete){
                Log.d("RequestClick", "Completing Current Pickup...");
                completeCurrentPickup();
            }
        }
    };

    private void requestNewPickup(){
        mainActivity.currentPickup = new CurrentPickup(1, this.getActivity());
        mainActivity.currentPickup.save(this.getActivity());
    }

    private void completeCurrentPickup(){

    }
}
