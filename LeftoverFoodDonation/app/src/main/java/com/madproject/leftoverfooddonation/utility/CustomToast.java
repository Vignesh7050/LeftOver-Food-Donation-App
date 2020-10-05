package com.madproject.leftoverfooddonation.utility;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.madproject.leftoverfooddonation.R;

public class CustomToast {
    private static Toast toast;
    public CustomToast(Toast toast) {
        this.toast = toast;
    }

    public void show() {
        System.out.println(this);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.rounded_corner_background_black);
        ((TextView) view.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
        toast.show();
}
}
