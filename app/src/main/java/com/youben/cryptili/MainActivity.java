package com.youben.cryptili;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /*
    what sould i do ?
        -home design
        -logo app
        -logo youbendev
    */
    SharedPreferences sharedPreferences;
    TextView homemsg;
    ImageView imageView;
    LinearLayout startLayout;
    RelativeLayout secondLayout,relativeLayout;

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        startLayout= (LinearLayout) findViewById(R.id.startlayout);
        secondLayout= (RelativeLayout) findViewById(R.id.secondlayout);
        relativeLayout= (RelativeLayout) findViewById(R.id.relativelayout);
        imageView= (ImageView) findViewById(R.id.imageview);
        homemsg= (TextView) findViewById(R.id.homemsg);

        if(sharedPreferences.getBoolean("animation",true)){

            homemsg.setVisibility(TextView.INVISIBLE);
            startAnimation();

        }else{

//            startLayout.setVisibility(LinearLayout.INVISIBLE);
            secondLayout.setVisibility(RelativeLayout.VISIBLE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        switch (id){
            case R.id.settings:

                Intent intent=new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
                break;

            case R.id.reset:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle(getString(R.string.dialog_title_reset));
                builder.setMessage(getString(R.string.dialog_msg_reset));
                builder.setNegativeButton(getString(R.string.dialog_noButton_reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton(getString(R.string.dialog_yesButton_reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sharedPreferences.edit().clear().apply();
                        DbManager dbManager=new DbManager(MainActivity.this);
                        dbManager.delete();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;

            case R.id.invite_friends:
                Intent inviteInt=new Intent(Intent.ACTION_SEND);
                inviteInt.setType("text/plain");
                inviteInt.putExtra(Intent.EXTRA_TEXT,getString(R.string.invitation_msg)+getPackageName());
                startActivity(inviteInt);
                break;

            case R.id.about:
                Intent aintent=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(aintent);
                break;

            case R.id.how_to:
                Intent hintent=new Intent(MainActivity.this,HowToActivity.class);
                startActivity(hintent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAnimation(){

        Handler handler=new Handler();
        /***
         * This is the start animation
         */
        handler.post(new Runnable() {
            @Override
            public void run() {
                Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.logo_start_animation);
                imageView.startAnimation(animation);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                homemsg.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.alphaprogressif));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homemsg.setVisibility(TextView.VISIBLE);
                    }
                },4000);
            }
        },2000);

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //startLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.alphadegressif));
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //startLayout.setVisibility(LinearLayout.INVISIBLE);
//                    }
//                },4000);
//            }
//        },6000);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                secondLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.alphaprogressif));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        secondLayout.setVisibility(RelativeLayout.VISIBLE);
                    }
                },4000);

            }
        },4000);
    }

    public void openHistory(View view) {
        Intent intent=new Intent(this,HistoryActivity.class);
        startActivity(intent);

    }

    public void goToSecretActivity(View view) {
        Intent intent=new Intent(this,SecretActivity.class);
        startActivity(intent);
    }

}
