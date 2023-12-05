package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FreelancerUploadEvidenceActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final ArrayList<Uri> uris = new ArrayList<>();
    private final ArrayList<String> savedImages = new ArrayList<>();
    private ImageView backImageView;
    private AppCompatButton selectImagesButton, cancelButton, saveButton;
    private GridView gridView;
    private Post post;
    private FreelancerCameraEvidenceActivity.ImageAdapter mAdapter;
    private ArrayList<Bitmap> imageList;
    private ImageAdapter imageAdapter;
    private String imageEncoded;
    private List<String> imagesEncodedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_upload_evidence);

        initializeAllVariables();
        getDataFromPreviousActivity();

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        gridView.setAdapter(imageAdapter);

        backImageView.setOnClickListener(view -> showAlertMessage());
        selectImagesButton.setOnClickListener(view -> openFileChooser());
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
            selectImagesButton.setClickable(false);

            saveImagesToDatabase();
        });
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
                    Toast.makeText(FreelancerUploadEvidenceActivity.this, "Please Wait!", Toast.LENGTH_SHORT).show();

                    storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                        savedImages.add(task.getResult().toString());
                    });

                    new Handler().postDelayed(this::saveDataToDatabase, 500);
                }).addOnFailureListener(e -> {
                    Toast.makeText(FreelancerUploadEvidenceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    cancelButton.setClickable(true);
                    saveButton.setClickable(true);
                    selectImagesButton.setClickable(true);
                });
            }
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

                        for (int i = 0; i < savedImages.size(); i++) {
                            Log.i("TAG", "saveDataToDatabase: " + savedImages.get(i));
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(Objects.requireNonNull(dataSnapshot.getKey())).child("evidences").child("evidence " + i).setValue(savedImages.get(i));
                        }
                    }
                }

                Toast.makeText(FreelancerUploadEvidenceActivity.this, "Task Completed!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(FreelancerUploadEvidenceActivity.this, FreelancerTaskActivity.class));
                finish();
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

    private boolean isMultipleSelectionAllowed() {
        return true;
    }

    public void getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        uris.add(Uri.parse(path));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processSelectedImages(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processSelectedImages(Intent data) throws IOException {
        if (data != null && data.getClipData() != null) {
            ClipData mClipData = data.getClipData();

            for (int i = 0; i < mClipData.getItemCount(); i++) {
                ClipData.Item item = mClipData.getItemAt(i);
                Uri uri = item.getUri();
                uris.add(uri);

                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageList.add(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            imageAdapter.notifyDataSetChanged();
        } else {
            assert data != null;
            Uri imageUri = data.getData();

            uris.add(imageUri);

            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            imageList.add(imageBitmap);
            imageAdapter.notifyDataSetChanged();
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();

        intent.setType("image/*");

        if (isMultipleSelectionAllowed()) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);

//        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
    }

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, FreelancerTaskActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.freelancer_upload_evidence_back_arrow_image);
        selectImagesButton = findViewById(R.id.freelancer_upload_evidence_post_open_camera_button);
        cancelButton = findViewById(R.id.freelancer_upload_evidence_post_cancel_button);
        saveButton = findViewById(R.id.freelancer_upload_evidence_post_save_button);
        gridView = findViewById(R.id.freelancer_upload_evidence_post_grid_view);
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