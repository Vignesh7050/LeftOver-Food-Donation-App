package com.madproject.leftoverfooddonation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.madproject.leftoverfooddonation.utility.CustomToast;
import static com.madproject.leftoverfooddonation.utility.CheckConnection.*;
import static com.madproject.leftoverfooddonation.utility.SHA256.generatePasswordHash;

public class LoginActivity extends AppCompatActivity {
    private EditText phone,password;
    private TextView registerlink;
    private Button login;
    private String phoneno, pass;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private static ProgressDialog progressDialog;
    private static AsyncTask<Void,Void,Void> loginTask = null;

    //@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //avoiding moving layouts up when soft keyboard is shown

        getViewComponents();
        setUpListeners();

        database = FirebaseDatabase.getInstance();
        ref= database.getReference("/leftover-food-donation/Users");


    }

    private void getViewComponents(){
        phone=findViewById(R.id.etLoginPhone);
        password = findViewById(R.id.etLoginPassword);
        login = findViewById(R.id.btLogin);
        registerlink = findViewById(R.id.tvRegisterLink);
    }

    public void setUpListeners(){
        registerlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i2);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneno = phone.getText().toString().trim();
                pass = password.getText().toString().trim();

                if (phoneno.length() == 0 && pass.length() == 0) {

                    phone.setError("Please enter the phone number!!!");
                    password.setError("Please enter the password!!!");
                }

                else if (phoneno.isEmpty()) {
                    phone.setError("Please enter the phone number!!!");
                }

                else if (pass.isEmpty()) {
                    password.setError("Please enter the password!!!");
                }

                else {
                    if (!checkConnection(LoginActivity.this)) return;
                    loginTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {

                            progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.show();
                            (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                            progressDialog.setContentView(R.layout.progress_dialog);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    try {
                                        loginTask.cancel(true);
                                    }
                                    catch (NullPointerException e) {}
                                }
                            });
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (isOnline(LoginActivity.this)) {
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(isCancelled()) return;  //If progress bar closed
                                        if (dataSnapshot.hasChild(phoneno)) {
                                            if(generatePasswordHash(pass).equals(dataSnapshot.child(phoneno).child("password").getValue().toString()))
                                            {
                                                if(isCancelled()) return;
                                                Intent i = new Intent(LoginActivity.this, UserActivity.class);
                                                i.putExtra("name",dataSnapshot.child(phoneno).child("name").getValue().toString());
                                                i.putExtra("phoneno", phoneno);
                                                startActivity(i);
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                new CustomToast(Toast.makeText(getApplicationContext(), "Invalid user credentials", Toast.LENGTH_SHORT)).show();
                                            }
                                        } else {
                                            progressDialog.dismiss();
                                            new CustomToast(Toast.makeText(getApplicationContext(), "It seams you don't have an account.", Toast.LENGTH_SHORT)).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            else {
                                progressDialog.dismiss();
                                new CustomToast(Toast.makeText(LoginActivity.this, "Network error! Please try again.", Toast.LENGTH_SHORT)).show();
                            }
                            return null;
                        }
                    };
                    loginTask.execute();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("Are you sure you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Exit?");
        alert.show();
    }
}
