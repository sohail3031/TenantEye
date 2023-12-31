package com.example.tenanteye;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatAdapter extends ArrayAdapter<User> {
    private final ArrayList<User> userArrayList;
    private final Context context;
    private final int resource;

    public UserChatAdapter(@NonNull Context context, int resource, ArrayList<User> userArrayList) {
        super(context, resource, userArrayList);

        this.context = context;
        this.resource = resource;
        this.userArrayList = userArrayList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = userArrayList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
        }

        CircleImageView circleImageView = convertView.findViewById(R.id.circleImageView);
        TextView name = convertView.findViewById(R.id.name);
        TextView email = convertView.findViewById(R.id.email);

        Picasso.get().load(user.getProfilePicture()).fit().centerCrop().into(circleImageView);

        name.setText(user.getFirstName() + " " + user.getLastName());
        email.setText(user.getEmailAddress());

        return convertView;
    }
}
