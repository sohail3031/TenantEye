package com.example.tenanteye.freelancerhomepages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;

import java.util.ArrayList;

public class FreelancerPostAdapter extends ArrayAdapter<Post> {
    private final Context context;
    private final int resource;
    private final ArrayList<Post> postArrayList;

    public FreelancerPostAdapter(Context context, int resource, ArrayList<Post> postArrayList) {
        super(context, resource, postArrayList);

        this.context = context;
        this.resource = resource;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        Post post = postArrayList.get(i);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(resource, null);
        }

        TextView titleText = view.findViewById(R.id.freelancer_show_task_info_brief_title_text);
        TextView descriptionText = view.findViewById(R.id.freelancer_show_task_info_brief_description_text);
        TextView addressText = view.findViewById(R.id.freelancer_show_task_info_brief_address_text);
        TextView statusText = view.findViewById(R.id.freelancer_show_task_info_brief_status_text);
        TextView assignedByText = view.findViewById(R.id.freelancer_show_task_info_brief_status_assigned_to_text);

        titleText.setText(post.getTitle().substring(0, Math.min(post.getTitle().length(), 50)));
        descriptionText.setText(post.getDescription().substring(0, Math.min(post.getDescription().length(), 50)));
        addressText.setText(post.getAddress().substring(0, Math.min(post.getAddress().length(), 50)));
        statusText.setText(post.getStatus());

        if (post.getAssignedBy() != null && post.getAssignedBy().length() > 0) {
            assignedByText.setText(post.getAssignedBy());
        } else {
            assignedByText.setText("");
        }

        return view;
    }
}
