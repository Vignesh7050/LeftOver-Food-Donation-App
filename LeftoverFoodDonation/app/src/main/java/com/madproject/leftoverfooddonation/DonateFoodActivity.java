package com.madproject.leftoverfooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madproject.leftoverfooddonation.utility.CustomToast;
import static com.madproject.leftoverfooddonation.UserActivity.phoneNo;
import static com.madproject.leftoverfooddonation.utility.CheckConnection.checkConnection;

public class DonateFoodActivity extends AppCompatActivity {
    private EditText foodName,foodQuantity,description, contactNumber,address;
    private Button donate,reset,choose;
    private ImageView imageView;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_food);

        address = findViewById(R.id.etPlace);
        contactNumber=findViewById(R.id.etContactno);
        foodName=findViewById(R.id.etFoodName);
        foodQuantity=findViewById(R.id.etAmtFood);
        description=findViewById(R.id.etDesc);
        donate=findViewById(R.id.btdntfoodsbmt);
        reset=findViewById(R.id.btdntfoodreset);
        database= FirebaseDatabase.getInstance();
        ref=database.getReference("/leftover-food-donation/FoodDetails/");

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname=foodName.getText().toString().trim();
                String foodQty=foodQuantity.getText().toString().trim();
                String desc=description.getText().toString().trim();
                String cn=contactNumber.getText().toString().trim();
                String addr=address.getText().toString().trim();


                boolean valid = true;
                if(fname.isEmpty())  { foodName.setError("Food name required"); valid = false; }
                if(foodQty.isEmpty()) { foodQuantity.setError("Quantity description is required"); valid = false; }
                if(cn.isEmpty()) { contactNumber.setError("Contact number is required"); valid = false; }
                if(cn.length()!=10) { contactNumber.setError("Enter 10 digit valid number"); valid = false; }
                if(addr.isEmpty()) { address.setError("Address is required"); valid = false; }
                if(valid) {
                    if(!checkConnection(DonateFoodActivity.this)) return;
                    FoodData c=new FoodData(fname, foodQty,desc,cn,addr);
                    progressDialog = new ProgressDialog(DonateFoodActivity.this);
                    progressDialog.show();
                    (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    ref.child(phoneNo).push().setValue(c, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            progressDialog.dismiss();
                            new CustomToast(Toast.makeText(getApplicationContext(),"Food donation successful.",Toast.LENGTH_SHORT)).show();
                            resetAll();
                        }
                    });

                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });
  }

  private void resetAll() {
      foodName.getText().clear();
      foodQuantity.getText().clear();
      description.getText().clear();
      address.getText().clear();
      contactNumber.getText().clear();
  }

    @Override
    public void onBackPressed() {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onBackPressed();
    }
}
