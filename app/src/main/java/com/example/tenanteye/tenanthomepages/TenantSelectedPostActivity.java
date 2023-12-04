package com.example.tenanteye.tenanthomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TenantSelectedPostActivity extends AppCompatActivity {
    private final ArrayList<String> freelancers = new ArrayList<>();
    String[] isoCountryCode = Locale.getISOCountries();
    private Post post = new Post();
    private EditText countrySpinner, stateSpinner, citySpinner, endTime, endDate, title, description, address, zipCode, startDate, startTime, link, freelancerSpinner;
    private AppCompatButton editButton, updateButton, deleteButton;
    private ImageView backImageView;
    private Dialog dialog;
    private ArrayList<String> countries;
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> statesKeys = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> citiesKeys = new ArrayList<>();
    private Map<String, String> countryMapSorted;
    private String selectedCountry, selectedCountryCode, selectedState, selectedStateCode, selectedCity, selectedTitle, selectedDescription, selectedAddress, selectedZipCode, selectedStartDate, selectedStartTime, selectedEndDate, selectedEndTime, selectedLink, emailAddress, timeStamp;
    private boolean isEditButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_selected_post);

        SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        emailAddress = loginSharedPreference.getString("emailAddress", "");

        if (emailAddress.equals("")) {
            Toast.makeText(this, "Please login in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
        assert post != null;
        timeStamp = post.getTimeStamp();

        initializeAllVariables();
        addDataToFields();
        getDataFromAPI();
        findFreelancersForTask();

        new Handler().postDelayed(() -> {
            backImageView.setOnClickListener(view -> {
                if (isEditButtonClicked) {
                    showAlertMessage();
                } else {
                    startActivity(new Intent(this, TenantHomeActivity.class));
                    finish();
                }
            });

            countrySpinner.setOnClickListener(view -> addCountriesToSpinner());
            stateSpinner.setOnClickListener(view -> addStatesToSpinner());
            citySpinner.setOnClickListener(view -> addCitiesToSpinner());
            endTime.setOnClickListener(view -> showEndTimeDialog());
            endDate.setOnClickListener(view -> showEndDateDialog());
            startDate.setOnClickListener(view -> showStartDateDialog());
            startTime.setOnClickListener(view -> showStartTimeDialog());
            editButton.setOnClickListener(view -> enableEditing());
            updateButton.setOnClickListener(view -> updatePost());
            deleteButton.setOnClickListener(view -> showDeleteAlert());
            freelancerSpinner.setOnClickListener(view -> addFreelancersToSpinner());
        }, 500);
    }

    private void addFreelancersToSpinner() {
        if (isEditButtonClicked) {
            if (freelancers.size() == 0) {
                showNoFreelancersAvailableMessage();
            } else {
                if (!freelancers.contains("")) {
                    freelancers.add(0, "");
                }

                dialog = new Dialog(this);

                dialog.setContentView(R.layout.freelancers_dropdown_items);
                Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.freelancers_dropdown_items_edit_text);
                ListView listView = dialog.findViewById(R.id.freelancers_dropdown_items_list_view);

                if (countries.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1, freelancers);

                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            adapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                        freelancerSpinner.setText(adapter.getItem(i));
                        dialog.dismiss();
                    });
                }
            }
        }
    }

    private void findFreelancersForTask() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(dataSnapshot1.child("user").getValue()).toString().equalsIgnoreCase("freelancer") && (Objects.requireNonNull(dataSnapshot1.child("city").getValue()).toString().equalsIgnoreCase(post.getCity()) || Objects.requireNonNull(dataSnapshot1.child("state").getValue()).toString().equalsIgnoreCase(post.getState()))) {
                            freelancers.add(Objects.requireNonNull(dataSnapshot1.child("emailAddress").getValue()).toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showNoFreelancersAvailableMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("No data found!")
                .setMessage("No freelancers available in the this location!")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void showDeleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Alert!")
                .setMessage("Are you sure, you want to delete the post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deletePost();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void deletePost() {
        String[] splitEmailAddress = emailAddress.split("\\.");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (timeStamp.equals(Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString())) {
                        String key = Objects.requireNonNull(dataSnapshot.getKey());

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).removeValue();

                        Toast.makeText(this, "Post Deleted!", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(TenantSelectedPostActivity.this, TenantTaskActivity.class));
                        finish();

                        break;
                    }
                }
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private boolean validateTitle() {
        if ("".equals(title.getText().toString())) {
            title.setError("Title is required!");

            return false;
        }

        return true;
    }

    private boolean validateDescription() {
        if ("".equals(description.getText().toString())) {
            description.setError("Description is required!");

            return false;
        }

        return true;
    }

    private boolean validateAddress() {
        if ("".equals(address.getText().toString())) {
            address.setError("Address is required!");

            return false;
        }

        return true;
    }

    private boolean validateCountry() {
        if ("".equals(countrySpinner.getText().toString())) {
            countrySpinner.setError("Country is required!");

            return false;
        }

        return true;
    }

    private boolean validateState() {
        if ("".equals(stateSpinner.getText().toString())) {
            stateSpinner.setError("State is required!");

            return false;
        }

        return true;
    }

    private boolean validateCity() {
        if ("".equals(citySpinner.getText().toString())) {
            citySpinner.setError("City is required!");

            return false;
        }

        return true;
    }

    private boolean validateZipCode() {
        if ("".equals(zipCode.getText().toString())) {
            zipCode.setError("Zip Code is required!");

            return false;
        } else if (zipCode.getText().toString().length() < 6) {
            zipCode.setError("Zip Code should be of 6 digits long!");

            return false;
        }

        return true;
    }

    private boolean validateStateDate() {
        if ("".equals(startDate.getText().toString())) {
            startDate.setError("Start Date is required!");

            return false;
        }

        return true;
    }

    private boolean validateStartTime() {
        if ("".equals(startTime.getText().toString())) {
            startDate.setError("Start Time is required!");

            return false;
        }

        return true;
    }

    private boolean validateEndDate() {
        if ("".equals(endDate.getText().toString())) {
            endDate.setError("End Date is required!");

            return false;
        }

        return true;
    }

    private boolean validateEndTime() {
        if ("".equals(endTime.getText().toString())) {
            endTime.setError("End Time is required!");

            return false;
        }

        return true;
    }

    private boolean validateLink() {
        if ("".equals(link.getText().toString())) {
            link.setError("Link is required!");

            return false;
        } else if (!URLUtil.isValidUrl(link.getText().toString()) || !Patterns.WEB_URL.matcher(link.getText().toString()).matches()) {
            link.setError("Invalid url provided!");

            return false;
        }

        return true;
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new java.util.Date());
    }

    private void storePostDataInPostObject() {
        post.setTitle(title.getText().toString());
        post.setDescription(description.getText().toString());
        post.setAddress(address.getText().toString());
        post.setCountry(countrySpinner.getText().toString());
        post.setState(stateSpinner.getText().toString());
        post.setCity(citySpinner.getText().toString());
        post.setZipCode(zipCode.getText().toString());
        post.setStartDate(startDate.getText().toString());
        post.setStartTime(startTime.getText().toString());
        post.setEndDate(endDate.getText().toString());
        post.setEndTime(endTime.getText().toString());
        post.setLink(link.getText().toString());
        post.setTimeStamp(getTimeStamp());

        if (freelancerSpinner.getText().toString().length() > 0 && freelancerSpinner != null) {
            post.setStatus("Assigned");
            post.setAssignedTo(freelancerSpinner.getText().toString());
        } else {
            post.setStatus("Active");
            post.setAssignedTo("");
        }

        Log.i("TAG", "storePostDataInPostObject: " + post.getStatus());
    }

    private void updatePost() {
        if (validateTitle() && validateDescription() && validateAddress() && validateCountry() && validateState() && validateCity() && validateZipCode() && validateStateDate() && validateState() && validateEndDate() && validateEndTime() && validateLink()) {
            storePostDataInPostObject();

            String[] splitEmailAddress = emailAddress.split("\\.");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        if (timeStamp.equals(Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString())) {
                            String key = Objects.requireNonNull(dataSnapshot.getKey());

                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("title").setValue(post.getTitle());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("description").setValue(post.getDescription());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("address").setValue(post.getAddress());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("country").setValue(post.getCountry());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("state").setValue(post.getState());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("city").setValue(post.getCity());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("zipCode").setValue(post.getZipCode());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("startDate").setValue(post.getStartDate());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("startTime").setValue(post.getStartTime());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("endDate").setValue(post.getEndDate());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("endTime").setValue(post.getEndTime());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("link").setValue(post.getLink());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("timeStamp").setValue(post.getTimeStamp());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("assignedTo").setValue(post.getAssignedTo());
                            databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("status").setValue(post.getStatus());

                            Toast.makeText(this, "Post Updated!", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(TenantSelectedPostActivity.this, TenantTaskActivity.class));
                            finish();

                            break;
                        }
                    }
                } else {
                    showSomethingWentWrongError();
                }
            });
        }
    }

    private void showEndTimeDialog() {
        if (isEditButtonClicked) {
            Calendar calendar = Calendar.getInstance();
            selectedEndTime = endTime.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

            try {
                calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(selectedEndTime)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, i, i1) -> {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);

                endTime.setText(simpleDateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }
    }

    private void showEndDateDialog() {
        if (isEditButtonClicked) {
            Calendar calendar = Calendar.getInstance();
            selectedEndDate = endDate.getText().toString();
            String[] splitDate = selectedEndDate.split("-");

            calendar.set(Calendar.YEAR, Integer.parseInt(splitDate[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(splitDate[1]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitDate[2]));

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                calendar.set(i, i1, i2);
                endDate.setText(simpleDateFormat.format(calendar.getTime()));
            }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }

    private void showStartDateDialog() {
        if (isEditButtonClicked) {
            Calendar calendar = Calendar.getInstance();
            selectedStartDate = startDate.getText().toString();
            String[] splitDate = selectedStartDate.split("-");

            calendar.set(Calendar.YEAR, Integer.parseInt(splitDate[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(splitDate[1]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitDate[2]));

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                calendar.set(i, i1, i2);
                startDate.setText(simpleDateFormat.format(calendar.getTime()));
            }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }

    private void showStartTimeDialog() {
        if (isEditButtonClicked) {
            Calendar calendar = Calendar.getInstance();
            selectedStartTime = startTime.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

            try {
                calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(selectedStartTime)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, i, i1) -> {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);

                startTime.setText(simpleDateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }
    }

    private void addCountriesToSpinner() {
        if (isEditButtonClicked) {
            getCountries();

            if (countries.size() > 0) {
                dialog = new Dialog(this);

                dialog.setContentView(R.layout.countries_dropdown_items);
                Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.countries_dropdown_items_edit_text);
                ListView listView = dialog.findViewById(R.id.countries_dropdown_items_list_view);

                if (countries.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1, countries);

                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            adapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                        for (Map.Entry<String, String> entry : countryMapSorted.entrySet()) {
                            if (entry.getKey().equals(adapter.getItem(i))) {
                                selectedCountryCode = entry.getValue();

                                getStatesFromAPI();

                                break;
                            }
                        }

                        countrySpinner.setText(adapter.getItem(i));
                        dialog.dismiss();
                    });
                } else {
                    showSomethingWentWrongError();
                }
            }
        }
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

    private void addStatesToSpinner() {
        if (isEditButtonClicked) {
            selectedCountry = countrySpinner.getText().toString();

            if (selectedCountry.equals(getString(R.string.tenant_select_country)) || selectedCountry == null || "".equals(selectedCountry)) {
                showDefaultCountryError();
            } else {
                if (states.size() > 0) {
                    dialog = new Dialog(this);

                    dialog.setContentView(R.layout.states_dropdown_items);
                    Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText editText = dialog.findViewById(R.id.states_dropdown_items_edit_text);
                    ListView listView = dialog.findViewById(R.id.states_dropdown_items_list_view);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1, states);

                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            adapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                        for (String s : states) {
                            if (s.equals(adapter.getItem(i))) {
                                selectedStateCode = statesKeys.get(states.indexOf(s));

                                getCitiesFromAPI();

                                break;
                            }
                        }

                        stateSpinner.setText(adapter.getItem(i));
                        dialog.dismiss();
                    });
                } else {
                    showSomethingWentWrongError();
                }
            }
        }
    }

    private void showDefaultCountryError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Invalid input!")
                .setMessage("Please select a country")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void addCitiesToSpinner() {
        if (isEditButtonClicked) {
            selectedCountry = countrySpinner.getText().toString();
            selectedState = stateSpinner.getText().toString();

            if (selectedCountry.equalsIgnoreCase(getString(R.string.tenant_select_country)) || selectedCountry == null || "".equals(selectedCountry)) {
                showDefaultCountryError();
            } else if (selectedState.equalsIgnoreCase(getString(R.string.tenant_select_state)) || selectedState == null || "".equals(selectedState)) {
                showDefaultStateError();
            } else {
                if (cities.size() > 0) {
                    dialog = new Dialog(this);

                    dialog.setContentView(R.layout.cities_dropdown_items);
                    Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText editText = dialog.findViewById(R.id.cities_dropdown_items_edit_text);
                    ListView listView = dialog.findViewById(R.id.cities_dropdown_items_list_view);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1, cities);

                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            adapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                        citySpinner.setText(adapter.getItem(i));
                        dialog.dismiss();
                    });
                } else {
                    showSomethingWentWrongError();
                }
            }
        }
    }

    private void showDefaultStateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Invalid input!")
                .setMessage("Please select a state")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void getDataFromAPI() {
        selectedCountry = countrySpinner.getText().toString();

        getCountries();

        for (Map.Entry<String, String> entry : countryMapSorted.entrySet()) {
            if (entry.getKey().equals(selectedCountry)) {
                selectedCountryCode = entry.getValue();

                getStatesFromAPI();

                break;
            }
        }

        selectedState = stateSpinner.getText().toString();

        new Handler().postDelayed(() -> {
            for (String s : states) {
                if (s.equals(selectedState)) {
                    selectedStateCode = statesKeys.get(states.indexOf(s));

                    getCitiesFromAPI();

                    break;
                }
            }
        }, 2000);

        new Handler().postDelayed(() -> {
            selectedCity = citySpinner.getText().toString();
        }, 2000);
    }

    private void getCitiesFromAPI() {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList1 = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://referential.p.rapidapi.com/v1/city?fields=iso_a2%2Cstate_code%2Cstate_hasc%2Ctimezone%2Ctimezone_offset&iso_a2=" + selectedCountryCode.toLowerCase() + "&lang=en&state_code=" + selectedStateCode.toLowerCase() + "&limit=250")
                .get()
                .addHeader("X-RapidAPI-Key", "6cd6ef093emsh3cdc130f43151f3p1aecabjsna38014f87b82")
                .addHeader("X-RapidAPI-Host", "referential.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("TAG", "getStatesFromAPI: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    try {
                        JSONArray jsonArray = new JSONArray(response.peekBody(Long.MAX_VALUE).string());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String temp = jsonObject.getString("value");
                            String temp1 = jsonObject.getString("key");

                            arrayList.add(temp);
                            arrayList1.add(temp1);
                        }

                        if (arrayList.size() > 0) {
                            cities = arrayList;
                            citiesKeys = arrayList1;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.i("TAG", "onResponse: " + response.peekBody(Long.MAX_VALUE).string());
//                    Toast.makeText(TenantSelectedPostActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getStatesFromAPI() {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList1 = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://referential.p.rapidapi.com/v1/state?fields=iso_a2&iso_a2=" + selectedCountryCode.toLowerCase() + "&lang=en&limit=250")
                .get()
                .addHeader("X-RapidAPI-Key", "6cd6ef093emsh3cdc130f43151f3p1aecabjsna38014f87b82")
                .addHeader("X-RapidAPI-Host", "referential.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("TAG", "getStatesFromAPI: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    try {
                        JSONArray jsonArray = new JSONArray(response.peekBody(Long.MAX_VALUE).string());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String temp = jsonObject.getString("value");
                            String temp1 = jsonObject.getString("key");

                            arrayList.add(temp);
                            arrayList1.add(temp1);
                        }

                        if (arrayList.size() > 0) {
                            states = arrayList;
                            statesKeys = arrayList1;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void getCountries() {
        Locale[] locales = Locale.getAvailableLocales();
        Map<String, String> countryMap = new HashMap<>();
        countries = new ArrayList<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();

            if (country.trim().length() > 0 && !countries.contains(country) && !"world".equalsIgnoreCase(country)) {
                countries.add(country);
            }
        }

        for (String code : isoCountryCode) {
            Locale locale = new Locale("", code);
            String name = locale.getDisplayCountry();
            countryMap.put(name, code);
        }

        countryMapSorted = countryMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        Collections.sort(countries);
    }

    private void addDataToFields() {
        title.setText(post.getTitle());
        description.setText(post.getDescription());
        address.setText(post.getAddress());
        countrySpinner.setText(post.getCountry());
        stateSpinner.setText(post.getState());
        citySpinner.setText(post.getCity());
        zipCode.setText(post.getZipCode());
        startDate.setText(post.getStartDate());
        startTime.setText(post.getStartTime());
        endDate.setText(post.getEndDate());
        endTime.setText(post.getEndTime());
        link.setText(post.getLink());
        freelancerSpinner.setText(post.getAssignedTo());
    }

    private void enableEditing() {
        if (!isEditButtonClicked) {
            isEditButtonClicked = true;

            title.setClickable(true);
            title.setCursorVisible(true);
            title.setFocusable(true);
            title.setFocusableInTouchMode(true);

            description.setClickable(true);
            description.setCursorVisible(true);
            description.setFocusable(true);
            description.setFocusableInTouchMode(true);

            address.setClickable(true);
            address.setCursorVisible(true);
            address.setFocusable(true);
            address.setFocusableInTouchMode(true);

            countrySpinner.setClickable(true);
            countrySpinner.setCursorVisible(true);
            countrySpinner.setFocusable(true);
            countrySpinner.setFocusableInTouchMode(true);

            stateSpinner.setClickable(true);
            stateSpinner.setCursorVisible(true);
            stateSpinner.setFocusable(true);
            stateSpinner.setFocusableInTouchMode(true);

            citySpinner.setClickable(true);
            citySpinner.setCursorVisible(true);
            citySpinner.setFocusable(true);
            citySpinner.setFocusableInTouchMode(true);

            zipCode.setClickable(true);
            zipCode.setCursorVisible(true);
            zipCode.setFocusable(true);
            zipCode.setFocusableInTouchMode(true);

            startDate.setClickable(true);
            startDate.setCursorVisible(true);
            startDate.setFocusable(true);
            startDate.setFocusableInTouchMode(true);

            startTime.setClickable(true);
            startTime.setCursorVisible(true);
            startTime.setFocusable(true);
            startTime.setFocusableInTouchMode(true);

            endDate.setClickable(true);
            endDate.setCursorVisible(true);
            endDate.setFocusable(true);
            endDate.setFocusableInTouchMode(true);

            endTime.setClickable(true);
            endTime.setCursorVisible(true);
            endTime.setFocusable(true);
            endTime.setFocusableInTouchMode(true);

            link.setClickable(true);
            link.setCursorVisible(true);
            link.setFocusable(true);
            link.setFocusableInTouchMode(true);

            Toast.makeText(this, "You can edit the post!", Toast.LENGTH_LONG).show();
        }
    }

    private void showAlertMessage() {
        if (!emailAddress.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setTitle(R.string.sign_up_alert_title)
                    .setMessage(R.string.sign_up_alert_message)
                    .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                        startActivity(new Intent(this, TenantTaskActivity.class));
                        finish();
                    })
                    .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                        dialog.dismiss();
                    });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        } else {
            finish();
        }
    }

    private void initializeAllVariables() {
        countrySpinner = findViewById(R.id.tenant_selected_post_countries_field);
        stateSpinner = findViewById(R.id.tenant_selected_post_states_field);
        citySpinner = findViewById(R.id.tenant_selected_post_cities_field);
        title = findViewById(R.id.tenant_selected_post_title_field);
        description = findViewById(R.id.tenant_selected_post_description_field);
        address = findViewById(R.id.tenant_selected_post_address_field);
        zipCode = findViewById(R.id.tenant_selected_post_zip_code_field);
        startDate = findViewById(R.id.tenant_selected_post_start_date_field);
        startTime = findViewById(R.id.tenant_selected_post_start_time_field);
        endDate = findViewById(R.id.tenant_selected_post_end_date_field);
        endTime = findViewById(R.id.tenant_selected_post_end_time_field);
        link = findViewById(R.id.tenant_selected_post_link_field);
        editButton = findViewById(R.id.tenant_selected_post_edit_button);
        updateButton = findViewById(R.id.tenant_selected_post_update_button);
        deleteButton = findViewById(R.id.tenant_selected_post_delete_button);
        backImageView = findViewById(R.id.tenant_selected_post_back_arrow_image);
        freelancerSpinner = findViewById(R.id.tenant_selected_post_freelancers_field);
    }
}