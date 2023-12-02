package com.example.tenanteye.tenanthomepages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TenantTaskFragment extends Fragment {
    private ArrayList<Post> userPosts = new ArrayList<>();
    private String emailAddress;
    private View tenantTaskFragment;
    private EditText searchTextView;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tenantTaskFragment = inflater.inflate(R.layout.fragment_tenant_task, container, false);

//        SharedPreferences loginSharedPreference = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
//        emailAddress = loginSharedPreference.getString("emailAddress", "");
//
//        if (emailAddress.equals("")) {
//            Toast.makeText(requireActivity(), "Please login in again!", Toast.LENGTH_LONG).show();
//            startActivity(new Intent(requireActivity(), LoginActivity.class));
//            requireActivity().finish();
//        }
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        initializeAllVariable();
//        getAppPostsFromUser();
//
        return tenantTaskFragment;
    }

    private void checkSize() {
        Log.i("TAG", "onCreateView: " + userPosts.size());
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("userPosts", userPosts);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            userPosts = (ArrayList<Post>) savedInstanceState.getSerializable("userPosts");
        }
    }

    private void getAppPostsFromUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post> arrayList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String[] spiltEmailAddress = dataSnapshot.getKey().split("-");

                    if (emailAddress.equals(spiltEmailAddress[0] + "." + spiltEmailAddress[1])) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = new Post();

                            post.setAddress(dataSnapshot1.child("address").getValue().toString());
                            post.setCity(dataSnapshot1.child("city").getValue().toString());
                            post.setCountry(dataSnapshot1.child("country").getValue().toString());
                            post.setDescription(dataSnapshot1.child("description").getValue().toString());
                            post.setEndDate(dataSnapshot1.child("endDate").getValue().toString());
                            post.setEndTime(dataSnapshot1.child("endTime").getValue().toString());
                            post.setLink(dataSnapshot1.child("link").getValue().toString());
                            post.setStartDate(dataSnapshot1.child("startDate").getValue().toString());
                            post.setStartTime(dataSnapshot1.child("startTime").getValue().toString());
                            post.setState(dataSnapshot1.child("state").getValue().toString());
                            post.setStatus(dataSnapshot1.child("status").getValue().toString());
                            post.setTimeStamp(dataSnapshot1.child("timeStamp").getValue().toString());
                            post.setTitle(dataSnapshot1.child("title").getValue().toString());
                            post.setZipCode(dataSnapshot1.child("zipCode").getValue().toString());

                            Log.i("TAG", "onDataChange: " + dataSnapshot1.child("address").getValue());

                            arrayList.add(post);

                            Log.i("TAG", "onDataChange: " + userPosts.get(0));
                        }

                        if (arrayList.size() > 0) {
                            userPosts = arrayList;
                        }

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkSize();
    }

    private void initializeAllVariable() {
        searchTextView = tenantTaskFragment.findViewById(R.id.tenant_task_search);
        listView = tenantTaskFragment.findViewById(R.id.tenant_task_list_view);
    }
}