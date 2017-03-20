package com.youben.cryptili;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    TextView isEmpty;
    ArrayList<String> msg;
    ArrayList<String> date;
    ArrayList<String> type;
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle(R.string.toolBar_title_history);

        listView= (ListView) findViewById(R.id.history_list);
        isEmpty= (TextView) findViewById(R.id.isempty);

        msg=new ArrayList<String>();
        date=new ArrayList<String>();
        type=new ArrayList<String>();

        new Thread(new onCreateList(0)).start();


    }

    class onCreateList implements Runnable {

        final static int DELETED=-1;

        int deleted;

        public onCreateList(int d){
            this.deleted=d;
        }

        @Override
        public void run() {
            msg.clear();
            date.clear();
            type.clear();
            dbManager=new DbManager(HistoryActivity.this);


            if(deleted==DELETED){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setVisibility(View.GONE);
                        isEmpty.setVisibility(View.VISIBLE);
                    }
                });
                return;

            }else if(!dbManager.isEmpty()){
                msg=dbManager.getMessages();
                date=dbManager.getDates();
                type=dbManager.getTypes();
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setVisibility(View.GONE);
                        isEmpty.setVisibility(View.VISIBLE);
                    }
                });
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    HistoryAdapter adapter=new HistoryAdapter(HistoryActivity.this,R.layout.history_component_list,msg,date,type);

                    listView.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteHistory:
                if(isEmpty.getVisibility()==View.VISIBLE){
                    Toast.makeText(this, R.string.history_toast_nomsg,Toast.LENGTH_SHORT).show();
                }
                else{

                    AlertDialog.Builder builder=new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.history_dialog_title_delete))
                            .setMessage(getString(R.string.history_dialog_msg_delete))
                            .setNegativeButton(getString(R.string.history_dialog_noButton_delete),null)
                            .setPositiveButton(getString(R.string.history_dialog_yesButton_delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dbManager=new DbManager(HistoryActivity.this);
                                            dbManager.delete();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(HistoryActivity.this, R.string.history_toast_deleted,Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                    new Thread(new onCreateList(onCreateList.DELETED)).start();
                                }
                            });
                    builder.create().show();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    class HistoryAdapter extends ArrayAdapter<String>{

        //Context context;
        ArrayList<String> dateArray,msgArray,typeArray;

        public HistoryAdapter(Context c,int Rlayout,ArrayList<String> message,ArrayList<String> date,ArrayList<String> type){
            super(c,Rlayout,R.id.history_message,message);
            this.dateArray=date;
            this.msgArray=message;
            this.typeArray=type;
            //this.context=c;

        }

        class Holder{

            TextView msg,date;

            public Holder(View v){
                this.msg= (TextView) v.findViewById(R.id.history_message);
                this.date= (TextView) v.findViewById(R.id.history_date);
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listComponent=convertView;
            Holder holder=null;
            boolean sended=typeArray.get(position).equals("C");

            if(listComponent==null) {

                int rid=(sended)?R.drawable.history_background_component_list_sended:R.drawable.history_background_component_list_received;

                LayoutInflater inflater = getLayoutInflater();//can replace with : (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listComponent = inflater.inflate(R.layout.history_component_list, parent, false);
                listComponent.findViewById(R.id.single_message_history).setBackgroundResource(rid);
                holder =new Holder(listComponent);
                listComponent.setTag(holder);
            }
            else{
                holder= (Holder) listComponent.getTag();
            }

            holder.date.setText(((sended)?getString(R.string.history_msg_sended):getString(R.string.history_msg_received))+" at "+dateArray.get(position));
            holder.msg.setText(msgArray.get(position));

            return listComponent;
        }
    }
}
