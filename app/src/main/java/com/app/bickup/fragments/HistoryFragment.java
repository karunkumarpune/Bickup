package com.app.bickup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.bickup.DeliveryActivity;
import com.app.bickup.R;
import com.app.bickup.adapter.BookingsAdapter;


public class HistoryFragment extends Fragment {
    DeliveryActivity activity;
    ListView listView;
    private String[] array={"Single Bed","Refrigerator","Single Bed"};



    public HistoryFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history, container, false);
        listView=(ListView)view.findViewById(R.id.delivery_history);
        listView.setAdapter(new BookingsAdapter(activity,array,0));
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=(DeliveryActivity)context;

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
