package com.example.tenanteye.freelancerhomepages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FreelancerAvailablePosts extends ArrayAdapter<Post> {
    private ArrayList<Post> postArrayList;
    private Context context;
    private int resource;

    public FreelancerAvailablePosts(@NonNull Context context, int resource, ArrayList<Post> postArrayList) {
        super(context, resource, postArrayList);

        this.context = context;
        this.resource = resource;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Post post = postArrayList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
        }

        TextView title = convertView.findViewById(R.id.freelancer_available_post_title_text);
        TextView description = convertView.findViewById(R.id.freelancer_available_post_description_text);
        TextView address = convertView.findViewById(R.id.freelancer_available_post_address_text);
        TextView postedBy = convertView.findViewById(R.id.freelancer_available_post_posted_by_text);

        title.setText(post.getTitle());
        description.setText(post.getDescription());
        address.setText(post.getAddress());
        postedBy.setText(post.getAddress());

        return convertView;
    }
}
