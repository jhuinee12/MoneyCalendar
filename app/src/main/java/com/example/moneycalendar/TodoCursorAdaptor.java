package com.example.moneycalendar;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

public class TodoCursorAdaptor extends CursorAdapter {
    String TAG = "TodoCursorAdaptor";
    public TodoCursorAdaptor(Context context, Cursor c) {
        super(context, c);
    }

    //리스트뷰에 표시될 뷰 반환
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( R.layout.lst_todo, parent,false );
        return v;

    }

    //뷰의 속성 지정
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView item_Title = (TextView) view.findViewById( R.id.txt_todo );
        String Title = cursor.getString( cursor.getColumnIndex( MainActivity.KEY_TITLE ) );

        Log.d(TAG, Title);

        item_Title.setText( Title );

    }
}
