package com.example.tenanteye;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChatDatabaseHelper extends SQLiteOpenHelper {
    protected static final String DATABASE_NAME = "Messages.db";
    protected static final int VERSION_NUMBER = 2;
    protected static final String KEY_ID = "_id";
    protected static final String KEY_MESSAGE = "message";
    protected static final String TABLE_NAME = "Chats";
    private static final String TAG = "ChatDatabaseHelper";
    private static ChatDatabaseHelper chatDatabaseHelper;

    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);

    }

    public static synchronized ChatDatabaseHelper getInstance(Context context) {
        if (chatDatabaseHelper == null) {
            chatDatabaseHelper = new ChatDatabaseHelper(context.getApplicationContext());
        }

        return chatDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: Calling onCreate");

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_MESSAGE + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: Calling onUpgrade, oldVersion = " + oldVersion + " newVersion = " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
