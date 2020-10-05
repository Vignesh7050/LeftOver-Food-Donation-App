package com.madproject.leftoverfooddonation;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static com.madproject.leftoverfooddonation.UserActivity.phoneNo;
import static com.madproject.leftoverfooddonation.utility.CheckConnection.*;

public class YourFoodDonationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView noDataMessage;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static ProgressDialog progressDialog;
    private static AsyncTask<Void,Void,Void> fetchData = null;

    private ArrayList<FoodData> foodData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);

        recyclerView = findViewById(R.id.recycleview);
        noDataMessage = findViewById(R.id.noDataMessage);
        noDataMessage.setText("You dont have any food donations!");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        fetchFoodData();
    }

    public void fetchFoodData() {
        if(!checkConnection(YourFoodDonationActivity.this)) return;
        fetchData = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                foodData.clear();
                progressDialog = new ProgressDialog(YourFoodDonationActivity.this);
                progressDialog.show();
                (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        try {
                            cancel(false);
                        } catch(NullPointerException e) {}
                    }
                });
            }

            @Override
            protected Void doInBackground(Void... voids) {
                ref = database.getReference("/leftover-food-donation/FoodDetails/"+phoneNo+"/");
                final CountDownLatch done = new CountDownLatch(1);
                if(!isOnline(YourFoodDonationActivity.this)) {return null; }
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(fetchData.isCancelled()) return;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            FoodData data = new FoodData(ds.child("foodName").getValue().toString(),ds.child("foodQuantity").getValue().toString(),ds.child("contactNumber").getValue().toString());
                            data.setFoodKey(key);
                            foodData.add(data);
                            if(fetchData.isCancelled()) {foodData.clear(); break;}
                        }
                        done.countDown();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        foodData.clear();
                        progressDialog.dismiss();
                    }
                });
                try {
                    done.await();
                } catch (InterruptedException e) {e.printStackTrace(); }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(progressDialog!=null) progressDialog.dismiss();
                if(foodData.size()!=0) {
                    FoodListAdapter adapter = new FoodListAdapter(YourFoodDonationActivity.this, foodData, phoneNo,0);
                    recyclerView.setAdapter(adapter);
                    noDataMessage.setVisibility(View.INVISIBLE);
                }
                else
                    noDataMessage.setVisibility(View.VISIBLE);
            }
        };
        fetchData.execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchFoodData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.refresh)
            fetchFoodData();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sub_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
