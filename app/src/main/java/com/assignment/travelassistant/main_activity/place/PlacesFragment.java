package com.assignment.travelassistant.main_activity.place;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.travelassistant.R;
import com.assignment.travelassistant.main_activity.diary.DiariesAdapter;
import com.assignment.travelassistant.model.Diary;
import com.assignment.travelassistant.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlacesFragment extends Fragment {
    RecyclerView rclPlaces;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    ArrayList<Place> mList = new ArrayList<>();
    PlacesAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        rclPlaces = view.findViewById(R.id.rcl_places);

        adapter = new PlacesAdapter(getContext(), mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rclPlaces.setLayoutManager(layoutManager);
        rclPlaces.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef = firebaseDatabase.getReference("Places");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Place> temp = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Place p = dataSnapshot1.getValue(Place.class);
                    temp.add(p);
                }
                adapter.clearAll();
                adapter.addAll(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
