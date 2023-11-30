package com.example.tenanteye.tenanthomepages;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;
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

public class TenantCreateFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private final Calendar calendar = Calendar.getInstance();
    private final Post post = new Post();
    String[] isoCountryCode = Locale.getISOCountries();
    private EditText countrySpinner, stateSpinner, citySpinner, endTime, endDate, title, description, address, zipCode, startDate, startTime, link;
    private AppCompatButton createPostButton;
    private View tenantCreateFragment;
    private Dialog dialog;
    private ArrayList<String> countries;
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> statesKeys = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> citiesKeys = new ArrayList<>();
    private Map<String, String> countryMapSorted;
    private String selectedCountry, selectedCountryCode, selectedState, selectedStateCode, selectedCity, selectedTitle, selectedDescription, selectedAddress, selectedZipCode, selectedStartDate, selectedStartTime, selectedEndDate, selectedEndTime, selectedLink, emailAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tenantCreateFragment = inflater.inflate(R.layout.fragment_tenant_create, container, false);
        SharedPreferences loginSharedPreference = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        emailAddress = loginSharedPreference.getString("emailAddress", "");

        if (emailAddress.equals("")) {
            Toast.makeText(requireActivity(), "Please login in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initializeAllVariable();
        addListeners();

        return tenantCreateFragment;
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title.getText().toString());
        outState.putString("description", description.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("country", selectedCountry);
        outState.putString("state", selectedState);
        outState.putString("city", selectedCity);
        outState.putString("zipCode", zipCode.getText().toString());
        outState.putString("startDate", startDate.getText().toString());
        outState.putString("startTime", startTime.getText().toString());
        outState.putString("endDate", endDate.getText().toString());
        outState.putString("endTime", endTime.getText().toString());
        outState.putString("link", link.getText().toString());
        outState.putStringArrayList("countries", countries);
        outState.putStringArrayList("states", states);
        outState.putStringArrayList("statesKeys", statesKeys);
        outState.putStringArrayList("cities", cities);
        outState.putStringArrayList("citiesKeys", citiesKeys);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            title.setText(savedInstanceState.getString("title"));
            description.setText(savedInstanceState.getString("description"));
            address.setText(savedInstanceState.getString("address"));
            countrySpinner.setText(savedInstanceState.getString("country"));
            stateSpinner.setText(savedInstanceState.getString("state"));
            citySpinner.setText(savedInstanceState.getString("city"));
            zipCode.setText(savedInstanceState.getString("zipCode"));
            startDate.setText(savedInstanceState.getString("startDate"));
            startTime.setText(savedInstanceState.getString("startTime"));
            endDate.setText(savedInstanceState.getString("endDate"));
            endTime.setText(savedInstanceState.getString("endTime"));
            link.setText(savedInstanceState.getString("link"));
            countries = savedInstanceState.getStringArrayList("countries");
            states = savedInstanceState.getStringArrayList("states");
            statesKeys = savedInstanceState.getStringArrayList("statesKeys");
            cities = savedInstanceState.getStringArrayList("cities");
            citiesKeys = savedInstanceState.getStringArrayList("citiesKeys");
        }
    }

    private void showDefaultStateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder
                .setTitle("Invalid input!")
                .setMessage("Please select a state")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void showDefaultCountryError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder
                .setTitle("Invalid input!")
                .setMessage("Please select a country")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void showSomethingWentWrongError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder
                .setTitle("Something went wrong")
                .setMessage("Please try again")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void addCountriesToSpinner() {
        getCountries();

        if (countries.size() > 0) {
            dialog = new Dialog(requireActivity());

            dialog.setContentView(R.layout.countries_dropdown_items);
            Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            EditText editText = dialog.findViewById(R.id.countries_dropdown_items_edit_text);
            ListView listView = dialog.findViewById(R.id.countries_dropdown_items_list_view);

            getCountries();

            if (countries.size() > 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, countries);

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

    private void addStatesToSpinner() throws IOException {
        selectedCountry = countrySpinner.getText().toString();

        if (selectedCountry.equals(getString(R.string.tenant_select_country)) || selectedCountry == null || "".equals(selectedCountry)) {
            showDefaultCountryError();
        } else {
            getStatesFromAPI();

            if (states.size() > 0) {
                dialog = new Dialog(requireActivity());

                dialog.setContentView(R.layout.states_dropdown_items);
                Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.states_dropdown_items_edit_text);
                ListView listView = dialog.findViewById(R.id.states_dropdown_items_list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, states);

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
                        ;
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void addCitiesToSpinner() {
        selectedCountry = countrySpinner.getText().toString();
        selectedState = stateSpinner.getText().toString();

        if (selectedCountry.equalsIgnoreCase(getString(R.string.tenant_select_country)) || selectedCountry == null || "".equals(selectedCountry)) {
            showDefaultCountryError();
        } else if (selectedState.equalsIgnoreCase(getString(R.string.tenant_select_state)) || selectedState == null || "".equals(selectedState)) {
            showDefaultStateError();
        } else {
            getCitiesFromAPI();

            if (cities.size() > 0) {
                dialog = new Dialog(requireActivity());

                dialog.setContentView(R.layout.cities_dropdown_items);
                Objects.requireNonNull(dialog.getWindow()).setLayout(1000, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.cities_dropdown_items_edit_text);
                ListView listView = dialog.findViewById(R.id.cities_dropdown_items_list_view);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, cities);

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

    private void showEndTimeDialog() {
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, i, i1) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);

            endTime.setText(simpleDateFormat.format(calendar.getTime()));
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showEndDateDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (datePicker, i, i1, i2) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            endDate.setText(simpleDateFormat.format(calendar.getTime()));
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showStartDateDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (datePicker, i, i1, i2) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            startDate.setText(simpleDateFormat.format(calendar.getTime()));
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showStartTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, i, i1) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);

            startTime.setText(simpleDateFormat.format(calendar.getTime()));
        }, hour, minute, true);

        timePickerDialog.show();
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
    }

    private void createPost() {
        if (validateTitle() && validateDescription() && validateAddress() && validateCountry() && validateState() && validateCity() && validateZipCode() && validateStateDate() && validateState() && validateEndDate() && validateEndTime() && validateLink()) {
            storePostDataInPostObject();

            String[] splitEmailAddress = emailAddress.split("\\.");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(splitEmailAddress[0] + "-" + splitEmailAddress[1]);
            databaseReference.push().setValue(post);

            Toast.makeText(requireActivity(), "Post Created!", Toast.LENGTH_LONG).show();

            replaceFragment();
        }
    }

    private void replaceFragment() {
        TenantTaskFragment tenantTaskFragment = new TenantTaskFragment();
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.tenant_home_frame_layout, tenantTaskFragment);
        fragmentTransaction.commit();
    }

    private void addListeners() {
        countrySpinner.setOnClickListener(view -> addCountriesToSpinner());
        stateSpinner.setOnClickListener(view -> {
            try {
                addStatesToSpinner();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        citySpinner.setOnClickListener(view -> addCitiesToSpinner());
        endTime.setOnClickListener(view -> showEndTimeDialog());
        endDate.setOnClickListener(view -> showEndDateDialog());
        startDate.setOnClickListener(view -> showStartDateDialog());
        startTime.setOnClickListener(view -> showStartTimeDialog());
        createPostButton.setOnClickListener(view -> createPost());
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

    private void initializeAllVariable() {
        countrySpinner = tenantCreateFragment.findViewById(R.id.tenant_create_countries_field);
        stateSpinner = tenantCreateFragment.findViewById(R.id.tenant_create_states_field);
        citySpinner = tenantCreateFragment.findViewById(R.id.tenant_create_cities_field);
        title = tenantCreateFragment.findViewById(R.id.tenant_create_title_field);
        description = tenantCreateFragment.findViewById(R.id.tenant_create_description_field);
        address = tenantCreateFragment.findViewById(R.id.tenant_create_address_field);
        zipCode = tenantCreateFragment.findViewById(R.id.tenant_create_zip_code_field);
        startDate = tenantCreateFragment.findViewById(R.id.tenant_create_start_date_field);
        startTime = tenantCreateFragment.findViewById(R.id.tenant_create_start_time_field);
        endDate = tenantCreateFragment.findViewById(R.id.tenant_create_end_date_field);
        endTime = tenantCreateFragment.findViewById(R.id.tenant_create_end_time_field);
        link = tenantCreateFragment.findViewById(R.id.tenant_create_link_field);
        createPostButton = tenantCreateFragment.findViewById(R.id.tenant_create_create_post_button);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
    }
}