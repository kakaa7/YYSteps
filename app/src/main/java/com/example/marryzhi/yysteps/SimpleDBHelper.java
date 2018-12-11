package com.example.marryzhi.yysteps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimpleDBHelper extends SQLiteOpenHelper {

        private static final String DBName = "YYSteps.db";
        private static final String DATA = "notes";

        private static final String CREATE_DATA_TABLE
                = "create table " + DATA + "(id integer primary key autoincrement, num text, week text, date text)";

        private static final String UPDATE_DATA_TABLE
                = "alter table " + DATA + " add height integer";

        public SimpleDBHelper(Context context, int version) {
            super(context, DBName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATA_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


            sqLiteDatabase.execSQL(UPDATE_DATA_TABLE);

            throw new IllegalStateException("unknown oldVersion " + i);
        }

}
