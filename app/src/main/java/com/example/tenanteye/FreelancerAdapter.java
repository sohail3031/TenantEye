package com.example.tenanteye;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FreelancerAdapter extends ArrayAdapter<User> {
    private Context context;
    private int resource;
    private ArrayList<User> freelancerArrayList;

    public FreelancerAdapter(Context context, int resource, ArrayList<User> freelancerArrayList) {
        super(context, resource, freelancerArrayList);

        this.context = context;
        this.resource = resource;
        this.freelancerArrayList = freelancerArrayList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User freelancerUser = freelancerArrayList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
        }

        ImageView image = convertView.findViewById(R.id.freelancer_info_card_view_image_view);
        TextView name = convertView.findViewById(R.id.freelancer_info_card_view_name_text_view);
        TextView location = convertView.findViewById(R.id.freelancer_info_card_view_place_text_view);

        Picasso.get().load(freelancerUser.getProfilePicture()).fit().centerCrop().into(image);

        name.setText(freelancerUser.getFirstName() + " " + freelancerUser.getLastName());
        location.setText(freelancerUser.getCity() + ", " + freelancerUser.getState() + ", " + freelancerUser.getCountry());

        return convertView;
    }
}
