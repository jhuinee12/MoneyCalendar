package com.example.moneycalendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MoneyActivity extends AppCompatActivity {

    private static final String TAG = "MoneyActivity";
    TextView tv_date;
    TextView tv_txt;
    ListView ls_account;
    Button bt_Input, bt_Output;

    MyDBHelper mHelper;
    SQLiteDatabase db;
    static Cursor cursor;
    MyCursorAdapter myAdapter;

    static String total;

    final static String KEY_ID = "_id";
    final static String KEY_CONTEXT = "context";
    final static String KEY_PRICE = "price";
    final static String TABLE_NAME = "MyAccountList";
    final static String KEY_DATE = "date";
    final static String KEY_DATE2 = "date2";
    //    public static String View_DATE = getToday_date();
    public static String View_DATE;
    public static String View_DATE2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        setTitle("Account Book");

        //데이터베이스 생성
        mHelper = new MyDBHelper(this);
        db = mHelper.getWritableDatabase();

        //레이아웃 변수설정

        tv_date = (TextView) findViewById(R.id.txt_Date);
        tv_txt = (TextView) findViewById(R.id.txt_Total);
        ls_account = (ListView) findViewById( R.id.lst_account);
        bt_Input = (Button) findViewById(R.id.btn_Input);
        bt_Output = (Button) findViewById(R.id.btn_Output);

        //날짜 표시 인텐트 설정
        Intent comingIntent = getIntent();
        Log.d(TAG, "getintent OK");
        String date = comingIntent.getStringExtra("date");
        String date2 = comingIntent.getStringExtra("date2");
        if(!TextUtils.isEmpty(date)){
            View_DATE = date;
            View_DATE2 = date2;
            tv_date.setText(date+"/"+date2);
            Log.d(TABLE_NAME, "string is not empty");
        } else{
            //date = getToday_date();
            tv_date.setText(View_DATE+"/"+View_DATE2);
        }

        // 수입 버튼 클릭
        bt_Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText eContext = (EditText) findViewById(R.id.edt_Context);
                    EditText ePrice = (EditText) findViewById(R.id.edt_Price);

                    String contexts = eContext.getText().toString();
                    int price = Integer.parseInt(ePrice.getText().toString());
                    String today_Date = getToday_yM()+"/"+getToday_d();
                    Log.d(TABLE_NAME, "값 확인" + contexts + ", " + price + ", " + today_Date);

                    String query = String.format("INSERT INTO %s VALUES ( null, '%s', %d, '%s', '%s');", TABLE_NAME, contexts, price, View_DATE, View_DATE2);
                    db.execSQL(query);

                    // 총합 가격 표시
                    String queryPriceSum = String.format(" SELECT SUM(price) FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
                    cursor = db.rawQuery(queryPriceSum, null);
                    cursor.moveToNext();
                    String sum = String.valueOf(cursor.getInt(0));
                    Log.d(TABLE_NAME, "sum : " + sum);
                    tv_txt.setText(sum);




                    // 리스트뷰 표시
                    String querySelectAll = String.format("SELECT * FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
                    cursor = db.rawQuery(querySelectAll, null);
                    myAdapter.changeCursor(cursor);
                    //myAdapter.notifyDataSetChanged();

                    eContext.setText("");
                    ePrice.setText("");

                    String queryPriceTotal = String.format(" SELECT SUM(price) FROM %s WHERE date = '%s'", MoneyActivity.TABLE_NAME, View_DATE);
                    MoneyActivity.cursor = db.rawQuery(queryPriceTotal, null);
                    MoneyActivity.cursor.moveToNext();
                    total = String.valueOf(MoneyActivity.cursor.getInt(0));
                    Log.d(MoneyActivity.TABLE_NAME, "total : " + total);
                    MainActivity.tx_total.setText(total);

                    // 저장 버튼 누른 후 키보드 안보이게 하기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ePrice.getWindowToken(), 0);
                }
                catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "입력된 값이 없습니다", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 지출 버튼 클릭
        bt_Output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    EditText eContext = (EditText) findViewById( R.id.edt_Context );
                    EditText ePrice = (EditText) findViewById( R.id.edt_Price );

                    String contexts = eContext.getText().toString();
                    int price = -Integer.parseInt( ePrice.getText().toString() );
                    String today_Date = getToday_yM()+"/"+getToday_d();
                    Log.d(TABLE_NAME, "값 확인" + contexts +", " + price + ", " + today_Date);

                    String query = String.format(
                            "INSERT INTO %s VALUES ( null, '%s', %d, '%s' , '%s');", TABLE_NAME, contexts, price, View_DATE, View_DATE2);
                    db.execSQL( query );

                    // 총합 가격 표시
                    String queryPriceSum = String.format( " SELECT SUM(price) FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
                    cursor = db.rawQuery( queryPriceSum, null );
                    cursor.moveToNext();
                    String sum = String.valueOf(cursor.getInt(0));
                    Log.d(TABLE_NAME, "sum : " + sum);
                    tv_txt.setText(sum);



                    // 리스트뷰 표시
                    String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
                    Log.d(TAG, querySelectAll);
                    cursor = db.rawQuery( querySelectAll, null );
                    myAdapter.changeCursor( cursor );
                    //myAdapter.notifyDataSetChanged();

                    String queryPriceTotal = String.format(" SELECT SUM(price) FROM %s WHERE date = '%s'", MoneyActivity.TABLE_NAME, View_DATE);
                    MoneyActivity.cursor = db.rawQuery(queryPriceTotal, null);
                    MoneyActivity.cursor.moveToNext();
                    total = String.valueOf(MoneyActivity.cursor.getInt(0));
                    Log.d(MoneyActivity.TABLE_NAME, "total : " + total);
                    MainActivity.tx_total.setText(total);

                    eContext.setText( "" );
                    ePrice.setText( "" );

                    // 저장 버튼 누른 후 키보드 안보이게 하기
                    InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                    imm.hideSoftInputFromWindow( ePrice.getWindowToken(), 0 );
                }
                catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "입력된 값이 없습니다", Toast.LENGTH_LONG).show();
                }

            }
        });

        // 리스트뷰 삭제
        ls_account.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoneyActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String query = String.format("DELETE FROM %s WHERE %s=%s",TABLE_NAME,KEY_ID,id);
                        db.execSQL( query );

                        // 총합 갱신
                        String queryPriceSum = String.format( " SELECT SUM(price) FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
                        cursor = db.rawQuery( queryPriceSum, null );
                        cursor.moveToNext();
                        String sum = String.valueOf(cursor.getInt(0));
                        Log.d(TABLE_NAME, "sum : " + sum);
                        tv_txt.setText(sum);

                        String queryPriceTotal = String.format(" SELECT SUM(price) FROM %s WHERE date = '%s'", TABLE_NAME, View_DATE);
                        cursor = db.rawQuery(queryPriceTotal, null);
                        cursor.moveToNext();
                        total = String.valueOf(cursor.getInt(0));
                        Log.d(TABLE_NAME, "total : " + total);
                        MainActivity.tx_total.setText(total);

                        // 리스트뷰 갱신
                        String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
                        Log.d(TAG, querySelectAll);
                        cursor = db.rawQuery( querySelectAll, null );
                        myAdapter.changeCursor( cursor );

                        myAdapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("취소",null);
                builder.show();

                return true;
            }
        });



        // 총합 가격 표시
        String queryPriceSum = String.format( " SELECT SUM(price) FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
        cursor = db.rawQuery( queryPriceSum, null );
        cursor.moveToNext();
        String sum = String.valueOf(cursor.getInt(0));
        Log.d(TABLE_NAME, "sum : " + sum);
        tv_txt.setText(sum);

        //커서 어댑터 생성
        String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, View_DATE, View_DATE2);
        cursor = db.rawQuery( querySelectAll, null );
        myAdapter = new MyCursorAdapter ( this, cursor );
        myAdapter.changeCursor( cursor );

        //리스트뷰 어댑터 설정
        ls_account.setAdapter( myAdapter );
    }

    // 날짜값 받아오기
//    static public String getToday_date(){
//        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/M/d", Locale.KOREA);
//        Date currentTime = new Date();
//        String Today_day = mSimpleDateFormat.format(currentTime).toString();
//        return Today_day;
//    }

    static public String getToday_yM(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/M", Locale.KOREA);
        Date currentTime = new Date();
        String Today_day = mSimpleDateFormat.format(currentTime).toString();
        return Today_day;
    }
    static public String getToday_d(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("d", Locale.KOREA);
        Date currentTime = new Date();
        String Today_day = mSimpleDateFormat.format(currentTime).toString();
        return Today_day;
    }


    static public String getThis_time(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HHMMSS", Locale.KOREA);
        Date currentTime = new Date();
        String This_time = mSimpleDateFormat.format(currentTime).toString();
        return This_time;
    }

//    static public void reset_table(){
//        String TABLE_NAME = "a_" + getToday_date();
//        String querySelectAll = String.format( "SELECT * FROM %s", TABLE_NAME );
//    }
}
