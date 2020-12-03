package br.com.goiaba.treconsulta.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDao {
    private SQLiteOpenHelper openHelper;
    protected SQLiteDatabase database;

    protected BaseDao(Context context) {
        this.openHelper = new DBOpenHelper(context);
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }
}