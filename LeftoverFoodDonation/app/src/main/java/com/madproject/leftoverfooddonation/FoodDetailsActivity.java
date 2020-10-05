package com.madproject.leftoverfooddonation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.madproject.leftoverfooddonation.utility.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

import static com.madproject.leftoverfooddonation.UserActivity.phoneNo;
import static com.madproject.leftoverfooddonation.utility.CheckConnection.*;

public class FoodDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private TextView foodName, foodQuantity, foodDescription, contact, address;
    private ImageView delete, call;
    private static ProgressDialog progressDialog = null;
    private static String donor_phone, food_key;
    private static int flag;
    private Button Orderbutton;
    private static ScrollView parent, child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        donor_phone = getIntent().getStringExtra("donor");
        food_key = getIntent().getStringExtra("food_key");
        flag = getIntent().getIntExtra("flag", 1);

        foodName = findViewById(R.id.food_name);
        foodQuantity = findViewById(R.id.food_quantity);
        foodDescription = findViewById(R.id.food_description);
        contact = findViewById(R.id.contact_number);
        address = findViewById(R.id.food_address);
        delete = findViewById(R.id.food_delete);
        call = findViewById(R.id.call);
        Orderbutton = findViewById(R.id.buttondonate);

        setupScrollViews();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("/leftover-food-donation/FoodDetails/" + donor_phone + "/" + food_key + "/");

        fetchFoodDetails();
    }

    private void fetchFoodDetails() {
        progressDialog = new ProgressDialog(FoodDetailsActivity.this);
        progressDialog.show();
        (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(!checkConnection(FoodDetailsActivity.this)) {progressDialog.dismiss(); return;}
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (progressDialog.isShowing()) {
                    foodName.setText(dataSnapshot.child("foodName").getValue().toString());
                    foodQuantity.setText(dataSnapshot.child("foodQuantity").getValue().toString());
                    foodDescription.setText(dataSnapshot.child("description").getValue().toString());
                    contact.setText(dataSnapshot.child("contactNumber").getValue().toString());
                    address.setText(dataSnapshot.child("address").getValue().toString());

                    if (!phoneNo.equals(donor_phone)) {
                        call.setVisibility(View.VISIBLE);
                        Orderbutton.setVisibility(View.VISIBLE);
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                placeCall(donor_phone);
                            }
                        });
                        Orderbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteOrOrderFood(v.getContext(), foodName.getText().toString(), foodQuantity.getText().toString(), donor_phone, food_key,0);
                            }
                        });
                    }
                    if (flag == 0) {
                        delete.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteOrOrderFood(v.getContext(), foodName.getText().toString(), foodQuantity.getText().toString(), donor_phone, food_key,1);
                            }
                        });
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void placeCall(String number) {
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("tel:"+number));
        startActivity(i);
    }

    private void deleteOrOrderFood(final Context context, final String food_name, final String food_quantity, final String donor_phone, final String food_key,final int i) {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        if(i==1){
            a_builder.setMessage("Are you sure want to delete " + food_name + " (+" + food_quantity + ") ?").setCancelable(false);}
        else{
            a_builder.setMessage("Are you sure want to order " + food_name + " (+" + food_quantity + ") ?").setCancelable(false);
        }
            a_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if(!checkConnection(FoodDetailsActivity.this)) return;
                        AsyncTask<Void,Void, Void> deleteOrOrderFood = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPreExecute() {
                                progressDialog = new ProgressDialog(FoodDetailsActivity.this);
                                progressDialog.show();
                                (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                                progressDialog.setContentView(R.layout.progress_dialog);
                                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                DatabaseReference ref1;
                                if (i==1){
                                    ref1 = FirebaseDatabase.getInstance().getReference("/leftover-food-donation/FoodDetails/"+phoneNo+"/"+food_key+"/");}
                                else{
                                    ref1 = FirebaseDatabase.getInstance().getReference("/leftover-food-donation/FoodDetails/"+donor_phone+"/"+food_key+"/");}
                                if(!isOnline(FoodDetailsActivity.this)) { dialog.cancel(); return null;}
                                final CountDownLatch counter = new CountDownLatch(1);
                                ref1.setValue(null, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        counter.countDown();
                                    }
                                });

                                try {
                                    counter.await();
                                } catch ( InterruptedException e) {}
                                progressDialog.dismiss();
                                finish();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if (i==1)
                                    new CustomToast(Toast.makeText(FoodDetailsActivity.this, "Food deleted successfully.", Toast.LENGTH_SHORT)).show();
                                else
                                    new CustomToast(Toast.makeText(FoodDetailsActivity.this, "Food ordered successfully.", Toast.LENGTH_SHORT)).show();
                            }
                        };
                        deleteOrOrderFood.execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        if (i==1){
            alert.setTitle("Delete food?");}
        else{
            alert.setTitle("Order food?");
        }
        alert.show();
    }
    
    //Enable scrolling for both the inner and outer scroll view
    private void setupScrollViews() {
        parent = findViewById(R.id.parent_scroll_view);
        child = findViewById(R.id.child_scroll_view1);

        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("TAG", "PARENT_TOUCH");
                parent.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("TAG", "CHILD_DESCRIPTION");
                parent.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println(getResources().getResourceEntryName(item.getItemId()));
        if (getResources().getResourceEntryName(item.getItemId()).equals("home")) { //If top back arrow is pressed
           // System.out.println("reached");
            finish();
            return true;
        } else if (item.getItemId() == R.id.refresh)
            fetchFoodDetails();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sub_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}