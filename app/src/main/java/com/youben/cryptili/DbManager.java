package com.youben.cryptili;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ayoub on 13/09/16.
 */
public class DbManager {

    Context context;
    DbHelper dbHelper;

    public DbManager(Context context){
        dbHelper=new DbHelper(context);
        this.context=context;
    }

    public void autoDelete(){
        String limit=PreferenceManager.getDefaultSharedPreferences(context).getString("history_max","100");
        int max= Integer.parseInt(limit)-1;
        if(max<0) max=0;
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "+ DbHelper.TABLE_NAME+" WHERE "+ DbHelper.ID+" NOT IN (SELECT "+ DbHelper.ID+" FROM "+ DbHelper.TABLE_NAME+" ORDER BY "+ DbHelper.DATE_TIME+" DESC LIMIT "+max+" );");
        db.close();
    }


    public void insertMessage(char type,String message){
        autoDelete();

        SQLiteDatabase db=dbHelper.getWritableDatabase();

        Calendar calendar=Calendar.getInstance();

        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);

        String date=year+"-"+((month<10)?"0":"")+month+"-"+((day<10)?"0":"")+day+" "+((hour<10)?"0":"")+hour+":"+((min<10)?"0":"")+min;

        ContentValues contentValues=new ContentValues();
        contentValues.put(DbHelper.MESSAGE_TYPE, String.valueOf(type));
        contentValues.put(DbHelper.MESSAGE, message);
        contentValues.put(DbHelper.DATE_TIME, date);

        db.insert(DbHelper.TABLE_NAME,null,contentValues);

        db.close();
    }

    public ArrayList<String> getMessages(){

        ArrayList<String> messages=new ArrayList<String>();
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String[] column={DbHelper.MESSAGE};
        Cursor cursor=database.query(DbHelper.TABLE_NAME,column,null,null,null,null, DbHelper.DATE_TIME+" DESC");

        if (cursor.moveToFirst()){
            do{
                messages.add(cursor.getString(cursor.getColumnIndex(DbHelper.MESSAGE)));
            }while (cursor.moveToNext());
        }

        database.close();
        cursor.close();
        return messages;
    }
    public ArrayList<String> getDates(){

        ArrayList<String> dates=new ArrayList<String>();
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String[] column={DbHelper.DATE_TIME};
        Cursor cursor=database.query(DbHelper.TABLE_NAME,column,null,null,null,null, DbHelper.DATE_TIME+" DESC");

        if (cursor.moveToFirst()){
            do{
                dates.add(cursor.getString(cursor.getColumnIndex(DbHelper.DATE_TIME)));
            }while (cursor.moveToNext());
        }

        database.close();
        cursor.close();
        return dates;
    }

    public ArrayList<String> getTypes(){

        ArrayList<String> types=new ArrayList<String>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String[] column={DbHelper.MESSAGE_TYPE};
        Cursor cursor=db.query(DbHelper.TABLE_NAME,column,null,null,null,null, DbHelper.DATE_TIME+" DESC");

        if(cursor.moveToFirst()){
            do{
                types.add(cursor.getString(cursor.getColumnIndex(DbHelper.MESSAGE_TYPE)));
            }while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return types;
    }

    public void delete(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_NAME,null,null);
        db.close();
    }

    public boolean isEmpty(){

        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query(DbHelper.TABLE_NAME,null,null,null,null,null,null);
        if(cursor!=null) {
            if (cursor.getCount() == 0) {
                db.close();
                cursor.close();
                return true;
            } else {
                db.close();
                cursor.close();
                return false;
            }
        }else {
            db.close();
        }
        return true;

    }


    private class DbHelper extends SQLiteOpenHelper{

//        DATABASE
        public static final String DATABASE_NAME="Historique";
        public static final int VERSION=1;

//        TABLE
        public static final String TABLE_NAME="Messages";
        public static final String ID="Mid";
        public static final String MESSAGE_TYPE="Type";
        public static final String MESSAGE="Message";
        public static final String DATE_TIME="DateTime";

        public static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT , "+MESSAGE_TYPE+"  , "+MESSAGE+" TEXT , "+DATE_TIME+" DATETIME ) ; " ;

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
