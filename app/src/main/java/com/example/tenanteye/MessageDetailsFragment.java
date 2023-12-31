package com.example.tenanteye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class MessageDetailsFragment extends Fragment {
    int position = 0;
    ChatActivity chatWindow;

    public MessageDetailsFragment(ChatActivity chatWindow) {
        this.chatWindow = chatWindow;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_details, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        TextView idTextView = view.findViewById(R.id.idTextView);
        Button deleteMessage = view.findViewById(R.id.deleteButton);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            long messageId = bundle.getLong("message_id", 0);
            String message = bundle.getString("message", "");
            position = bundle.getInt("position", 0);

            idTextView.setText(String.valueOf(messageId));
            messageTextView.setText(message);
        }

        deleteMessage.setOnClickListener(v -> {
            if (chatWindow == null) {
                Intent intent = new Intent();
                long messageId = Long.parseLong(idTextView.getText().toString());

                intent.putExtra("message_id", messageId);
                intent.putExtra("position", position);
                requireActivity().setResult(Activity.RESULT_OK, intent);
                requireActivity().finish();
            } else {
                long messageId = Long.parseLong(idTextView.getText().toString());

//                chatWindow.deleteMessage(messageId, position);
            }
        });
    }
}