package com.madproject.leftoverfooddonation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    CardView c1,c2,c3,c4;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        c1 = root.findViewById(R.id.cardViewfindfood);
        c2 = root.findViewById(R.id.cardViewdonatefood);
        c3 = root.findViewById(R.id.cardviewFoodYouDonated);
        c4 = root.findViewById(R.id.cardviewPDWF);

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity().getApplication(), FindFoodDonorActivity.class);
                startActivity(i);
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity().getApplication(), DonateFoodActivity.class);
                startActivity(i);
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity().getApplication(), YourFoodDonationActivity.class);
                startActivity(i);
            }
        });

        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity().getApplication(), DontWasteFoodActivity.class);
                startActivity(i);
            }
        });
        return root;
    }
}
