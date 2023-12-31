package com.example.tenanteye.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenanteye.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    private final List<MessagesList> messagesLists;
    private final Context context;

    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePicture;
        private TextView name, lastMessage, unSeenMessages;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            profilePicture = itemView.findViewById(R.id.messages_adapter_layout_profile_picture);
//            name = itemView.findViewById(R.id.messages_adapter_layout_name);
//            lastMessage = itemView.findViewById(R.id.messages_adapter_layout_last_message);
//            unSeenMessages = itemView.findViewById(R.id.messages_adapter_layout_unseen_messages);
        }
    }
}
