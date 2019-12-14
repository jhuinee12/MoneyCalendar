package com.example.moneycalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    CalendarView calendar;
    Button bt_add, bt_money;
    EditText ed_title;
    static TextView tv_date, tx_total;
    ListView ls_todo;
    private String date=getToday_yM(), date2=getToday_d();

    final static String KEY_ID = "_id";
    final static String KEY_TITLE = "title";
    final static String TABLE_NAME = "MyScheduleList";
    final static String KEY_DATE = "date";
    final static String KEY_DATE2 = "date2";
    //    public static String View_DATE = getToday_date();
    public static String View_DATE = getToday_yM();
    public static String View_DATE2 = getToday_d();

    TodoDBHelper todoHelper;
    MyDBHelper myDBHelper;
    SQLiteDatabase db, db2;
    Cursor cursor;
    TodoCursorAdaptor todoAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        setTitle("Checklist Book");

        calendar = (CalendarView) findViewById(R.id.calendarView);
        ed_title = (EditText) findViewById(R.id.edt_Title);
        tv_date = (TextView) findViewById(R.id.txt_Date);
        tx_total = (TextView) findViewById(R.id.txt_total);
        bt_add = (Button) findViewById(R.id.btn_Add);
        bt_money = (Button) findViewById(R.id.btn_Money);
        ls_todo = (ListView) findViewById(R.id.lst_todo);

        //데이터베이스 생성
        todoHelper = new TodoDBHelper(this);
        myDBHelper = new MyDBHelper(this);
        db = todoHelper.getWritableDatabase();
        db2 = myDBHelper.getWritableDatabase();
        todoAdaptor = new TodoCursorAdaptor( this, cursor );




        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("WrongConstant")
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //선택 날짜 표시

                date = year + "/" + (month+1);
                date2 = String.valueOf(dayOfMonth);
                Log.d(TAG, "onSelectDayChange: date : "+date+"/"+date2);

                tv_date.setVisibility(View.VISIBLE);
                tv_date.setText(date+"/"+date2);
                ed_title.setVisibility(View.VISIBLE); // EditText 보이기
                bt_add.setVisibility(View.VISIBLE); // 추가버튼 보이기
                ls_todo.setVisibility(View.VISIBLE); // 리스트뷰 보이기

                //선택한 달의 토탈 금액
                String queryPriceTotal = String.format(" SELECT SUM(price) FROM %s WHERE date = '%s'", MoneyActivity.TABLE_NAME, date);
                MoneyActivity.cursor = db2.rawQuery(queryPriceTotal, null);
                MoneyActivity.cursor.moveToNext();
                MoneyActivity.total = String.valueOf(MoneyActivity.cursor.getInt(0));
                Log.d(MoneyActivity.TABLE_NAME, "total : " + MoneyActivity.total);
                tx_total.setText(MoneyActivity.total);

                ed_title.setText(""); //EditText에 공백값 넣기

                //커서 어댑터 생성
                String querySelectAll = String.format( "SELECT * FROM '%s' WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, date, date2);
                Log.d(TAG, querySelectAll);
                cursor = db.rawQuery( querySelectAll, null );
                todoAdaptor.changeCursor(cursor);
                todoAdaptor.notifyDataSetChanged();

                //리스트뷰 어댑터 설정
                ls_todo.setAdapter( todoAdaptor );

                // 추가 버튼 클릭
                bt_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = ed_title.getText().toString();
                        String today_Date = getToday_yM()+"/"+getToday_d();
                        Log.d(TABLE_NAME, "값 확인" + title +", " + today_Date);

                        String query = String.format(
                                "INSERT INTO %s VALUES ( null, '%s', '%s' , '%s');", TABLE_NAME, title, date, date2);
                        db.execSQL( query );

                        String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, date, date2);
                        Log.d(TAG, querySelectAll);
                        cursor = db.rawQuery( querySelectAll, null);
                        todoAdaptor.changeCursor(cursor);
                        todoAdaptor.notifyDataSetChanged();

                        ed_title.setText("");

                        // 추가 버튼 누른 후 키보드 안보이게 하기
                        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                        imm.hideSoftInputFromWindow( ed_title.getWindowToken(), 0 );
                    }
                });
            }
        });

        // 가계부 버튼 클릭
        bt_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MoneyActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("date2", date2);
                startActivity(intent); // 가계부 창 띄우기
            }
        });

        // 리스트뷰 삭제
        ls_todo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String query = String.format("DELETE FROM %s WHERE %s=%s",TABLE_NAME,KEY_ID,id);
                        db.execSQL( query );
                        // 바뀐 리스트뷰 띄우기
                        String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s' AND date2 = '%s'", TABLE_NAME, date, date2);
                        Log.d(TAG, querySelectAll);
                        cursor = db.rawQuery( querySelectAll, null);
                        todoAdaptor.changeCursor(cursor);
                        todoAdaptor.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소",null);
                builder.show();

                return true;
            }
        });
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
