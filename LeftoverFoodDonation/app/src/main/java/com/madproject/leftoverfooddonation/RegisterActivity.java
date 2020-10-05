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
import androidx.annotation.Nullable;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText email, phone, username, pass, repass;
    private Button reset, register;
    TextView backtologin;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static AsyncTask<Void, Void, Void> registerTask = null;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //avoiding moving layouts up when soft keyboard is shown

        getComponentView();

        database = FirebaseDatabase.getInstance();
        backtologin = findViewById(R.id.tvBackToLogin);

        setUpListeners();

    }

    private void getComponentView(){
        username = findViewById(R.id.etRegisterUsername);
        email = findViewById(R.id.etRegisterEmail);
        phone = findViewById(R.id.etRegisterPhone);
        pass = findViewById(R.id.etRegisterPassword);
        repass = findViewById(R.id.etRegisterRepassword);
        register = findViewById(R.id.btRegister);
        reset = findViewById(R.id.btReset);
    }

    private void setUpListeners(){
        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uname = username.getText().toString();
                final String emailid = email.getText().toString();
                final String phoneno = phone.getText().toString();
                final String passwd = pass.getText().toString();
                final String repasswd = repass.getText().toString();

                boolean valid = true;

                if (uname.isEmpty()) {
                    username.setError("Please enter username"); valid = false;
                }

                if (emailid.isEmpty()) {
                    email.setError("Please enter email"); valid = false;
                }

                if (phoneno.isEmpty()) {
                    phone.setError("Please enter phone number"); valid = false;
                }

                if (phoneno.length() != 10) {
                    phone.setError("Enter the valid phone number!"); valid = false;
                }

                if (passwd.isEmpty()) {
                    pass.setError("Please enter password"); valid = false;
                }

                else if (passwd.length() < 6) {
                    pass.setError("Password must contain atleast 6 character!"); valid = false;
                }

                else if (!(passwd.equals(repasswd))) {
                    pass.setError("Password is not matching!"); valid = false;
                }

                if(valid) {
                    if (!checkConnection(RegisterActivity.this)) return;
                    registerTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            progressDialog = new ProgressDialog(RegisterActivity.this);
                            progressDialog.show();
                            progressDialog.setCancelable(true);
                            (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                            progressDialog.setContentView(R.layout.progress_dialog);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    try {
                                        registerTask.cancel(true);
                                    }
                                    catch (NullPointerException e) {}
                                }
                            });
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (isOnline(RegisterActivity.this)) {
                                ref = database.getReference("/leftover-food-donation/Users");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(phoneno)) {
                                            if(isCancelled()) return;
                                            progressDialog.dismiss();
                                            new CustomToast(Toast.makeText(getApplicationContext(), "Account with same phone number already exist!", Toast.LENGTH_SHORT)).show();
                                        }
                                        else {
                                            if(isCancelled()) return;
                                            ref.child(phoneno).setValue(new UserData(uname, phoneno, emailid, generatePasswordHash(passwd)), new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if(databaseError==null) {
                                                        new CustomToast(Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT)).show();
                                                        resetAll();
                                                    }
                                                    else
                                                        new CustomToast(Toast.makeText(getApplicationContext(), "Unknown error occured. Please try again!", Toast.LENGTH_SHORT)).show();
                                                }
                                            });
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                new CustomToast(Toast.makeText(RegisterActivity.this, "Network lost! Please try again.", Toast.LENGTH_SHORT)).show();
                            }
                            return null;
                        }
                    };
                    registerTask.execute();
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
        username.getText().clear();
        email.getText().clear();
        phone.getText().clear();
        pass.getText().clear();
        repass.getText().clear();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("Are you sure you want to exit?") .setCancelable(false)
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
