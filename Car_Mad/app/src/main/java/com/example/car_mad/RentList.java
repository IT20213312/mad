package com.example.car_mad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RentList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RentAdapter mAdapter;
    StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ValueEventListener mDBListener;
    private List<RentCarModel> rentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_list);

        mRecyclerView = findViewById(R.id.rv_rentList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.black));


        rentList =new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("rent-car");

//Set the values for every adapter from firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    RentCarModel rent = postSnapshot.getValue(RentCarModel.class);
                    rentList.add(rent);
                }
                mAdapter  = new RentAdapter(RentList.this,rentList);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled( DatabaseError error) {
                Toast.makeText(RentList.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}