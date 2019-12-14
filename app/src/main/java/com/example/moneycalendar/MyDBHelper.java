package com.example.moneycalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.moneycalendar.MoneyActivity.KEY_DATE2;
import static com.example.moneycalendar.MoneyActivity.KEY_ID;
import static com.example.moneycalendar.MoneyActivity.KEY_CONTEXT;
import static com.example.moneycalendar.MoneyActivity.KEY_DATE;
import static com.example.moneycalendar.MoneyActivity.KEY_PRICE;
import static com.example.moneycalendar.MoneyActivity.TABLE_NAME;

class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context) {

        super(context, "My_Account_Data.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        // AUTOINCREMENT 속성 사용 시 PRIMARY KEY로 지정한다.
        String query = String.format("CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "%s TEXT, "
                + "%s INTEGER, "
                + "%s TEXT, "
                + "%s TEXT);", TABLE_NAME, KEY_ID, KEY_CONTEXT, KEY_PRICE, KEY_DATE, KEY_DATE2);
        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(query);
        onCreate(db);
    }
}
