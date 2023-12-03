package com.example.tenanteye.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class SignUpOneActivity extends AppCompatActivity {
    private static final String NAME_RE = "^[a-zA-Z]+";
    private static final String EMAIL_RE = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final String[] isoCountryCode = Locale.getISOCountries();
    private Map<String, String> countryMapSorted;
    private EditText firstNameField, lastNameField, emailAddressField, countryField, stateField, cityField;
    private String firstName, lastName, emailAddress, selectedCountryCode, selectedCountry, selectedStateCode, selectedState, selectedCity;
    private ImageView backImageView;
    private AppCompatButton nextButton;
    private ArrayList<String> countries;
    private Dialog dialog;
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> statesKeys = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> citiesKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_one);

        initializeALlVariables();

        backImageView.setOnClickListener(view -> {
            showAlertMessage();
        });

        nextButton.setOnClickListener(view -> {
            getData();

            if (validateFirstName() && validateLastName() && validateEmailAddress() && validateCountry() && validateState() && validateCity()) {
                Intent intent = new Intent(this, SignUpTwoActivity.class);

                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("emailAddress", emailAddress);
                intent.putExtra("country", selectedCountry);
                intent.putExtra("state", selectedState);
                intent.putExtra("city", selectedCity);

                startActivity(intent);
                finish();
            }
        });

        countryField.setOnClickListener(view -> addCountriesToSpinner());
        stateField.setOnClickListener(view -> addStatesToSpinner());
        cityField.setOnClickListener(view -> addCitiesToSpinner());
    }

    private boolean validateCountry() {
        if ("".equals(selectedCountry)) {
            countryField.setError("Country is required!");

            return false;
        }

        return true;
    }

    private boolean validateState() {
        if ("".equals(selectedState)) {
            stateField.setError("State is required!");

            return false;
        }

        return true;
    }

    private boolean validateCity() {
        if ("".equals(selectedCity)) {
            cityField.setError("City is required!");

            return false;
        }

        return true;
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

    private void addCitiesToSpinner() {
        selectedCountry = countryField.getText().toString();
        selectedState = stateField.getText().toString();

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
                    cityField.setText(adapter.getItem(i));
                    dialog.dismiss();
                });
            } else {
                showSomethingWentWrongError();
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

    private void addStatesToSpinner() {
        selectedCountry = countryField.getText().toString();

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

                    stateField.setText(adapter.getItem(i));
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

    private void addCountriesToSpinner() {
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

                    countryField.setText(adapter.getItem(i));
                    dialog.dismiss();
                });
            } else {
                showSomethingWentWrongError();
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

    private void initializeALlVariables() {
        backImageView = findViewById(R.id.sign_up_one_back_image_view);
        nextButton = findViewById(R.id.sign_up_one_next_button_view);
        firstNameField = findViewById(R.id.sign_up_one_first_name_field);
        lastNameField = findViewById(R.id.sign_up_one_last_name_field);
        emailAddressField = findViewById(R.id.sign_up_one_email_field);
        countryField = findViewById(R.id.sign_up_one_country_field);
        stateField = findViewById(R.id.sign_up_one_state_field);
        cityField = findViewById(R.id.sign_up_one_city_field);
    }

    private void showAlertMessage() {
        if (!"".equals(firstName) || !"".equals(lastName) || !"".equals(emailAddress)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setTitle(R.string.sign_up_alert_title)
                    .setMessage(R.string.sign_up_alert_message)
                    .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                        startActivity(new Intent(this, LoginActivity.class));
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

    private void getData() {
        firstName = firstNameField.getText().toString().trim();
        lastName = lastNameField.getText().toString().trim();
        emailAddress = emailAddressField.getText().toString().trim();
        selectedCountry = countryField.getText().toString().trim();
        selectedState = stateField.getText().toString().trim();
        selectedCity = cityField.getText().toString().trim();
    }

    private boolean validateFirstName() {
        if ("".equals(firstName)) {
            firstNameField.setError(getString(R.string.first_name_required));

            return false;
        } else if (!firstName.matches(NAME_RE)) {
            firstNameField.setError(getString(R.string.first_name_invalid));

            return false;
        }

        firstNameField.setError(null);

        return true;
    }

    private boolean validateLastName() {
        if ("".equals(lastName)) {
            lastNameField.setError(getString(R.string.last_name_required));

            return false;
        } else if (!lastName.matches(NAME_RE)) {
            lastNameField.setError(getString(R.string.last_name_invalid));

            return false;
        }

        lastNameField.setError(null);

        return true;
    }

    private boolean validateEmailAddress() {
        if ("".equals(emailAddress)) {
            emailAddressField.setError(getString(R.string.email_address_required));

            return false;
        } else if (!emailAddress.matches(EMAIL_RE)) {
            emailAddressField.setError(getString(R.string.email_address_invalid));

            return false;
        }

        emailAddressField.setError(null);

        return true;
    }
}