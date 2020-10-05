package com.madproject.leftoverfooddonation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.madproject.leftoverfooddonation.utility.CustomToast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CountDownLatch;

import static com.madproject.leftoverfooddonation.utility.CheckConnection.*;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;

    public static String phoneNo;
    private static String userName;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        phoneNo = getIntent().getStringExtra("phoneno");
        userName = getIntent().getStringExtra("name");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        aToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(aToggle);
        aToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        ((TextView) headerLayout.findViewById(R.id.user_name)).setText(userName);
        ((TextView) headerLayout.findViewById(R.id.user_phone)).setText(phoneNo);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment(),"Home");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
                a_builder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                startActivity(new Intent(UserActivity.this, LoginActivity.class));
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
                alert.setTitle("Logout?");
                alert.show();
        }
        if (aToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
            a_builder.setMessage("Are you sure you want to logout and exit?").setCancelable(false)
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_home)
            loadFragment(new HomeFragment(), "Home");
        else if(item.getItemId() == R.id.nav_delete_account)
            deleteAccount();
        else if(item.getItemId() == R.id.nav_about_app)
            loadFragment(new AboutFragment(), "About");
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
        getSupportActionBar().setTitle(title);
    }

    private void deleteAccount() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("Are you sure you want to delete your account?\nThis will remove all the data of your food donations.") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        AsyncTask<Void,Void, Void> deleteAccount = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPreExecute() {
                                progressDialog = new ProgressDialog(UserActivity.this);
                                progressDialog.show();
                                (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                                progressDialog.setContentView(R.layout.progress_dialog);
                                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("/leftover-food-donation/Users/"+phoneNo+"/");
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/leftover-food-donation/FoodDetails/"+phoneNo+"/");
                                if(!isOnline(UserActivity.this)) { dialog.cancel(); return null;}
                                final CountDownLatch counter = new CountDownLatch(2);
                                ref1.setValue(null, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        counter.countDown();
                                    }
                                });
                                ref2.setValue(null, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        counter.countDown();
                                    }
                                });
                                try {
                                    counter.await();
                                } catch ( InterruptedException e) {}
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                else
                                    new CustomToast(Toast.makeText(UserActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT)).show();
                            }
                        };
                        deleteAccount.execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Delete Account?");
        alert.show();
    }

}
