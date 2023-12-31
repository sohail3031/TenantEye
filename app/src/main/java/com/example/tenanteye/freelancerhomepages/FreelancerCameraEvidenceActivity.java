package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FreelancerCameraEvidenceActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final ArrayList<Uri> uris = new ArrayList<>();
    private ImageView backImageView;
    private AppCompatButton openCameraButton, cancelButton, saveButton;
    private GridView gridView;
    private Post post;
    private ImageAdapter mAdapter;
    private ArrayList<Bitmap> imageList;
    private ImageAdapter imageAdapter;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_camera_evidence);

        initializeAllVariables();
        getDataFromPreviousActivity();

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        gridView.setAdapter(imageAdapter);

        openCameraButton.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        backImageView.setOnClickListener(view -> showAlertMessage());
        cancelButton.setOnClickListener(view -> {
            imageList.clear();
            uris.clear();
            imageAdapter.notifyDataSetChanged();
            gridView.setAdapter(null);
            gridView.setAdapter(imageAdapter);
        });
        saveButton.setOnClickListener(view -> {
            cancelButton.setClickable(false);
            saveButton.setClickable(false);
            openCameraButton.setClickable(false);

            saveImagesToDatabase();
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveImagesToDatabase() {
        String[] assignedToEmailAddress = post.getAssignedTo().split("\\.");
        String[] assignedByEmailAddress = post.getAssignedBy().split("\\.");
        String timeStamp = post.getUniqueIdentifier();
        String uniqueKey = assignedToEmailAddress[0] + "-" + assignedToEmailAddress[1] + "->" + assignedByEmailAddress[0] + "-" + assignedByEmailAddress[1] + "->" + timeStamp;

        for (int i = 0; i < imageList.size(); i++) {
            if (uris.get(i) != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(uniqueKey + "/" + System.currentTimeMillis() + ".png");

                storageReference.putFile(uris.get(i)).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(FreelancerCameraEvidenceActivity.this, "Please Wait!", Toast.LENGTH_SHORT).show();
                    saveDataToDatabase();
                }).addOnFailureListener(e -> {
                    Toast.makeText(FreelancerCameraEvidenceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    cancelButton.setClickable(true);
                    saveButton.setClickable(true);
                    openCameraButton.setClickable(true);
                });
            }
        }
    }

    public void getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        try {
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            uris.add(Uri.parse(path));
        } catch (Exception e) {
            Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToDatabase() {
        String[] splitEmailAddress = post.getAssignedBy().split("\\.");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (post.getAssignedTo().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("assignedTo").getValue()).toString()) && post.getUniqueIdentifier().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("uniqueIdentifier").getValue()).toString())) {
                        post.setStatus("Completed");

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(Objects.requireNonNull(dataSnapshot.getKey())).child("status").setValue(post.getStatus());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(Objects.requireNonNull(dataSnapshot.getKey())).child("uniqueIdentifier").setValue(post.getUniqueIdentifier());
                    }
                }

                Toast.makeText(FreelancerCameraEvidenceActivity.this, "Task Completed!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(FreelancerCameraEvidenceActivity.this, FreelancerTaskActivity.class));
//                finish();
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private void showSomethingWentWrongError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Something went wrong")
                .setMessage("Please try again")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Toast.makeText(this, "Camera and storage permissions are required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, FreelancerTaskActivity.class));
//                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uris.add(data.getData());

            Log.i("TAG", "onActivityResult: " + data.getData());

            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            imageList.add(imageBitmap);
            imageAdapter.notifyDataSetChanged();
            gridView.setAdapter(imageAdapter);

            assert imageBitmap != null;
            getImageUri(this, imageBitmap);
        } else {
            Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.freelancer_camera_evidence_back_arrow_image);
        openCameraButton = findViewById(R.id.freelancer_camera_evidence_post_open_camera_button);
        cancelButton = findViewById(R.id.freelancer_camera_evidence_post_cancel_button);
        saveButton = findViewById(R.id.freelancer_camera_evidence_post_save_button);
        gridView = findViewById(R.id.freelancer_camera_evidence_post_grid_view);
    }

    public static class ImageAdapter extends BaseAdapter {
        private final Context mContext;
        private final ArrayList<Bitmap> imageList;

        public ImageAdapter(Context c, ArrayList<Bitmap> imageList) {
            mContext = c;
            this.imageList = imageList;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setImageBitmap(imageList.get(position));
            return imageView;
        }
    }
}