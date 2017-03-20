package com.youben.cryptili;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HowToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);
        getSupportActionBar().setTitle(R.string.toolBar_title_how2);

    }

    public void onClick(View view) {
        int id=view.getId();
        int mid=0,tid=0;

        switch (id){
            case R.id.how2_1:
                tid=R.string.how2_1;
                mid=R.string.how2_1r;
                break;
            case R.id.how2_2:
                tid=R.string.how2_2;
                mid=R.string.how2_2r;
                break;
            case R.id.how2_3:
                tid=R.string.how2_3;
                mid=R.string.how2_3r;
                break;
            case R.id.how2_4:
                tid=R.string.how2_4;
                mid=R.string.how2_4r;
                break;
            case R.id.how2_5:
                tid=R.string.how2_5;
                mid=R.string.how2_5r;
                break;
            case R.id.how2_6:
                tid=R.string.how2_6;
                mid=R.string.how2_6r;
                break;
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        builder.setTitle(tid);
        builder.setMessage(mid);
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }
}
