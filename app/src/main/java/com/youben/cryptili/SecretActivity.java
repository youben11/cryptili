package com.youben.cryptili;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SecretActivity extends AppCompatActivity {

    EditText et;
    TextView tv;
    SharedPreferences sharedPreferences;
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);

        et= (EditText) findViewById(R.id.text1);
        tv= (TextView) findViewById(R.id.text2);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

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
                Intent intent=new Intent(SecretActivity.this,Settings.class);
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
                        DbManager dbManager=new DbManager(SecretActivity.this);
                        dbManager.delete();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;

            case R.id.invite_friends:
                Intent inviteInt=new Intent(Intent.ACTION_SEND);
                inviteInt.setType("text/plain");
                inviteInt.putExtra(Intent.EXTRA_TEXT,getString(R.string.invitation_msg)+""+getPackageName());
                startActivity(inviteInt);
                break;

            case R.id.about:
                Intent aintent=new Intent(SecretActivity.this,AboutActivity.class);
                startActivity(aintent);
                break;

            case R.id.how_to:
                Intent hintent=new Intent(SecretActivity.this,HowToActivity.class);
                startActivity(hintent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doSomethingToThisText(View view) {
        String msg=et.getText().toString();
        String key=sharedPreferences.getString("key","");

        Thread thread=null;
        if (!key.isEmpty() && !msg.isEmpty()) {

            if (view.getId()==R.id.cryptButton) {

                thread=new Thread(new Crypt(msg,key));
                thread.start();

            }
            else {

                thread=new Thread(new Decrypt(msg,key));
                thread.start();
            }

        }
        else{

            if (!msg.isEmpty()){
                Toast.makeText(this, R.string.secret_toast_enterKey,Toast.LENGTH_LONG).show();
                changeKey(findViewById(R.id.keyButton));
            }
            else Toast.makeText(this,R.string.secret_toast_writeMsg,Toast.LENGTH_LONG).show();

        }

    }

    public void changeKey(View view) {

        final EditText keyText=new EditText(this);
        keyText.setText(sharedPreferences.getString("key",""));

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.secret_dialog_title_key));
        builder.setMessage(getString(R.string.secret_dialog_msg_key));
        builder.setView(keyText,60,30,60,30);
        builder.setPositiveButton(getString(R.string.secret_dialog_saveButton_key), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferences.edit().putString("key",keyText.getText().toString()).apply();
            }
        });
        builder.setNegativeButton(getString(R.string.secret_dialog_cancelButton_key),null);
        builder.create().show();

    }

    class Crypt implements Runnable{
        String cmsg,ckey,cresult;

        Crypt(String m,String k){
            this.cmsg=m;
            this.ckey=k;
        }

        @Override
        public void run() {

            dbManager=new DbManager(SecretActivity.this);

            if(sharedPreferences.getBoolean("history",true)&&!sharedPreferences.getString("history_max","100").equals("0")){

                dbManager.insertMessage('C',cmsg);

            }else{
                dbManager.autoDelete();
            }

            cresult=crypt(cmsg,ckey);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(cresult);
                }
            });

        }
    }

    class Decrypt implements Runnable{

        String dmsg,dkey,dresult;

        Decrypt(String msg,String key){
            this.dmsg=msg;
            this.dkey=key;
        }

        @Override
        public void run() {
            dresult=decrypt(dmsg,dkey);

            if(sharedPreferences.getBoolean("history",true)){

                dbManager=new DbManager(SecretActivity.this);
                dbManager.insertMessage('D',dresult);

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(dresult);
                }
            });
        }
    }

    private String crypt(String msg,String key) {
        String result= "";
        char c;

        for(int i=0,j=0;i<msg.length();i++){

            c=msg.toCharArray()[i];

            if(c>=32 && c<=126){
                c -= 32;
                result += (char) (( c + key.toCharArray()[j] - 32) % 95 +32);
                j = (j + 1) % key.length();
            }else
                result +=(char) c;

        }

        return result;
    }

    private String decrypt(String msg,String key) {
        String result = "";
        char c;

        for (int i = 0, j = 0; i < msg.length(); i++) {

            c = msg.toCharArray()[i];

            if (c >= 32 && c <= 126) {

                c -= 32;
                c = (char)((c + ( 127 - key.toCharArray()[j])) % 95 + 32);
                result += c;
                j = (j + 1) % key.length();
            } else
                result += (char) c;
        }
        return result;
    }

    public void copyText(View view) {
        if(tv.getText().equals("")) return ;

        ClipboardManager clipboard= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipdata=ClipData.newPlainText("applicationSecret",tv.getText().toString());

        clipboard.setPrimaryClip(clipdata);
    }

    public void pasteText(View view) {

        ClipboardManager clipboardManager= (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        if(clipboardManager.hasPrimaryClip()&&clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
            ClipData.Item item=clipboardManager.getPrimaryClip().getItemAt(0);
            et.setText(item.getText());
        }
        else{
            Toast.makeText(this, R.string.secret_toast_cantFindTextInCB,Toast.LENGTH_LONG).show();
        }


    }

    public void returnPrevious(View view) {
        finish();
    }

    public void sendMessage(View view) {
        String msg=tv.getText().toString();

        if(!msg.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(intent);
        }else Toast.makeText(this, R.string.secret_toast_writeMsg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        et.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
    }

}
