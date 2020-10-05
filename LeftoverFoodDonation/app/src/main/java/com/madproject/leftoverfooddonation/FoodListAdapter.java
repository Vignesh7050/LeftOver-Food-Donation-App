package com.madproject.leftoverfooddonation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.madproject.leftoverfooddonation.utility.CustomToast;
import static com.madproject.leftoverfooddonation.UserActivity.phoneNo;
import static com.madproject.leftoverfooddonation.utility.CheckConnection.*;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FoodData> foodData;
    private int flag;
    private String phone;
    private ProgressDialog progressDialog;

    public FoodListAdapter(Context context, ArrayList<FoodData> foodData, String phone, int flag) {
        this.context = context;
        this.foodData = foodData;
        this.flag = flag;
        this.phone = phone;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_food_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.foodName.setText(foodData.get(position).getFoodName());
        holder.foodQuantity.setText(foodData.get(position).getFoodQuantity());
        holder.contactNumber.setText(foodData.get(position).getContactNumber());
        holder.imageView.setImageResource(R.drawable.your_donations);
        holder.cardView.setTag(foodData.get(position).getFoodKey());
        if(flag==0) {
            holder.cardView.setTag(foodData.get(position).getFoodKey());
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFood(v.getContext(),holder.foodName.getText().toString(), holder.foodQuantity.getText().toString(), phone, holder.cardView.getTag().toString());
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return foodData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView foodName,foodQuantity,contactNumber;
        private ImageView imageView,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_food_list);
            foodName = itemView.findViewById(R.id.tvFoodName);
            foodQuantity = itemView.findViewById(R.id.tvFoodQuantity);
            contactNumber = itemView.findViewById(R.id.tvContact);
            imageView = itemView.findViewById(R.id.imageFood);
            delete = itemView.findViewById(R.id.delete);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FoodDetailsActivity.class);
                    intent.putExtra("donor", phone);
                    intent.putExtra("food_key", cardView.getTag().toString());
                    intent.putExtra("flag",flag);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private void deleteFood(final Context context, final String food_name, final String food_quantity, final String donor_phone, final String food_key) {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(context);
        a_builder.setMessage("Are you sure you want to delete "+food_name+" (+"+food_quantity+") ?") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if(context instanceof YourFoodDonationActivity) {
                            AsyncTask<Void, Void, Void> deleteFood = new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected void onPreExecute() {
                                    progressDialog = new ProgressDialog(context);
                                    progressDialog.show();
                                    (progressDialog.getWindow().getAttributes()).dimAmount = 0.5f;
                                    progressDialog.setContentView(R.layout.progress_dialog);
                                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                }

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/leftover-food-donation/FoodDetails/" + phoneNo + "/" + food_key + "/");
                                    if (!isOnline(context)) {
                                        dialog.cancel();
                                        return null;
                                    }
                                    final CountDownLatch counter = new CountDownLatch(1);
                                    ref.setValue(null, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            counter.countDown();
                                        }
                                    });

                                    try {
                                        counter.await();
                                    } catch (InterruptedException e) {
                                    }
                                    progressDialog.dismiss();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    else {
                                        new CustomToast(Toast.makeText(context, "Food deleted successfully.", Toast.LENGTH_SHORT)).show();
                                        ((YourFoodDonationActivity) context).fetchFoodData();
                                    }
                                }
                            };
                            deleteFood.execute();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Delete food?");
        alert.show();
    }
}
