package com.example.tenanteye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static final int REQUEST_FOR_ACTIVITY = 13;
    //  Note: Uncomment the commented lines to see debugging messages as toast as mentioned in assignment 2.2 in question 7
    private static final String TAG = "ChatWindowActivity";
    protected EditText editText;
    protected ArrayList<String> chatMessagesArrayList;
    ChatAdapter chatAdapter;
    Cursor cursor;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatMessagesArrayList = new ArrayList<>();

        getDatabaseData();

        ListView listView = findViewById(R.id.chat_window_list_view);
        editText = findViewById(R.id.chat_window_edit_text);
        Button sendButton = findViewById(R.id.chat_window_button);

        ChatAdapter chatAdapter = new ChatAdapter(this, chatMessagesArrayList);
        listView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String message = editText.getText().toString();

            chatMessagesArrayList.add(message);
            databaseManager.insert(message);
            chatAdapter.notifyDataSetChanged();
            editText.setText("");
        });
    }

    @SuppressLint("Range")
    private void getDatabaseData() {
        try {
            databaseManager = new DatabaseManager(this);
            databaseManager.open();

            cursor = databaseManager.fetch();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                chatMessagesArrayList.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
                cursor.moveToNext();
            }

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.i(TAG, "onCreate: Column Name: " + cursor.getColumnName(i));
            }
        } catch (Exception e) {
            Log.d("DB", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    protected class ChatAdapter extends ArrayAdapter<String> {
        Cursor cursor;

        public ChatAdapter(Context context) {
            super(context, 0);
        }

        public ChatAdapter(Context ctx, ArrayList<String> messages) {
            super(ctx, 0, messages);
        }

        @Override
        public int getCount() {
            return chatMessagesArrayList.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return chatMessagesArrayList.get(position);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = ChatActivity.this.getLayoutInflater();
            View resultView;

            if (position % 2 == 0) {
                resultView = inflater.inflate(R.layout.chat_row_outgoing, null);
            } else {
                resultView = inflater.inflate(R.layout.chat_row_incoming, null);
            }

            TextView messageTextView = resultView.findViewById(R.id.message_text);
            messageTextView.setText(getItem(position));

            return resultView;
        }

        public long getItemId(int position) {
            try {
                ChatDatabaseHelper chatDatabaseHelper = ChatDatabaseHelper.getInstance(getContext());
                SQLiteDatabase sqLiteDatabase = chatDatabaseHelper.getWritableDatabase();
                String query = String.format("SELECT * FROM %s", ChatDatabaseHelper.TABLE_NAME);
                cursor = sqLiteDatabase.rawQuery(query, null);

                cursor.moveToPosition(position);

                return cursor.getLong(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_ID));
            } catch (Exception ignored) {
                return 0L;
            }
        }
    }
}