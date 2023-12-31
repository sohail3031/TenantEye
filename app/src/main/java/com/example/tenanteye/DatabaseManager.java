package com.example.tenanteye;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private final Context context;
    private ChatDatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public void open() throws SQLException {
        databaseHelper = new ChatDatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();

    }

    public void close() {
        databaseHelper.close();
    }

    public Cursor fetch() {
        String[] columns = new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE};
        Cursor cursor = sqLiteDatabase.query(ChatDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void insert(String message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChatDatabaseHelper.KEY_MESSAGE, message);
        sqLiteDatabase.insert(ChatDatabaseHelper.TABLE_NAME, null, contentValues);
    }
}
