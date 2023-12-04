package com.example.tenanteye.tenanthomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

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

public class TenantEditProfileActivity extends AppCompatActivity {
    private static final String NAME_RE = "^[a-zA-Z]+";
    private final String[] isoCountryCode = Locale.getISOCountries();
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private ImageView backImageView;
    private ArrayList<String> countries;
    private Dialog dialog;
    private Map<String, String> countryMapSorted;
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> citiesKeys = new ArrayList<>();
    private ArrayList<String> statesKeys = new ArrayList<>();
    private EditText firstNameField, lastNameField, countryField, stateField, cityField, phoneNumberField, emailAddressField;
    private RadioGroup genderRadioGroup, userRadioGroup;
    private RadioButton genderRadioButton, userRadioButton, maleRadioButton, femaleRadioButton, otherRadioButton, tenantRadioButton, freelancerRadioButton;
    private DatePicker datePicker;
    private AppCompatButton saveButton;
    private CountryCodePicker countryCodePicker;
    private User userData;
    private String firstName, lastName, selectedCountry, selectedState, selectedCity, phoneNumber, gender, user, dateOfBirth, selectedCountryCode, selectedStateCode, emailAddress, phoneNumberCountry;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_edit_profile);

        initializeAllVariables();
        getDataFromPreviousActivity();
        showDataOnUI();

        countryField.setOnClickListener(view -> addCountriesToSpinner());
        stateField.setOnClickListener(view -> addStatesToSpinner());
        cityField.setOnClickListener(view -> addCitiesToSpinner());

        saveButton.setOnClickListener(view -> {
            if (!isClicked) {
                getData();

                try {
                    if (validateFirstName() && validateLastName() && validateCountry() && validateState() && validateCity() && !"".equals(gender) && !"".equals(user) && validatePhoneNumber()) {
                        saveDataToDatabase();
                    } else {
                        Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberParseException e) {
                    Log.i("TAG", "onCreate: " + e.getMessage());
                }
            }
        });

        genderRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.sign_up_two_gender_male) {
                genderRadioButton = findViewById(R.id.sign_up_two_gender_male);
            } else if (i == R.id.sign_up_two_gender_female) {
                genderRadioButton = findViewById(R.id.sign_up_two_gender_female);
            } else {
                genderRadioButton = findViewById(R.id.sign_up_two_gender_other);
            }

            gender = genderRadioButton.getText().toString();
        });

        userRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.sign_up_two_user_user) {
                userRadioButton = findViewById(R.id.sign_up_two_user_user);
            } else {
                userRadioButton = findViewById(R.id.sign_up_two_user_freelancer);
            }

            user = userRadioButton.getText().toString();
        });

        backImageView.setOnClickListener(view -> showAlertMessage());
    }

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, TenantMoreActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private void saveDataToUserObject() {
        userData.setCity(selectedCity);
        userData.setCountry(selectedCountry);
        userData.setDateOfBirth(dateOfBirth);
        userData.setFirstName(firstName);
        userData.setGender(gender);
        userData.setLastName(lastName);
        userData.setPhoneNumber(phoneNumber);
        userData.setState(selectedState);
        userData.setUser(user);
        userData.setGender(gender);
        userData.setUser(user);
    }

    private void saveDataToDatabase() {
        saveDataToUserObject();

        String[] splitEmailAddress = userData.getEmailAddress().split("\\.");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    Log.i("TAG", "saveDataToDatabase: " + task.getResult().child("emailAddress"));
                    if (userData.getEmailAddress().equals(Objects.requireNonNull(dataSnapshot.child("emailAddress").getValue()).toString())) {
                        String key = Objects.requireNonNull(dataSnapshot.getKey());

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("city").setValue(userData.getCity());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("country").setValue(userData.getCountry());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("dateOfBirth").setValue(userData.getDateOfBirth());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("firstName").setValue(userData.getFirstName());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("gender").setValue(userData.getGender());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("lastName").setValue(userData.getLastName());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("phoneNumber").setValue(userData.getPhoneNumber());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("state").setValue(userData.getState());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("user").setValue(userData.getUser());

                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_LONG).show();

                        isClicked = true;

                        startActivity(new Intent(TenantEditProfileActivity.this, TenantMoreActivity.class));
                        finish();

                        break;
                    }
                }
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private boolean validatePhoneNumber() throws NumberParseException {
        Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, phoneNumberCountry);

        if ("".equals(phoneNumber)) {
            phoneNumberField.setError(getString(R.string.phone_number_required));

            return false;
        } else if (!phoneNumberUtil.isValidNumber(number)) {
            phoneNumberField.setError(getString(R.string.phone_number_invalid));

            return false;
        } else {
            return true;
        }
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

    private void getData() {
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        selectedCountry = countryField.getText().toString();
        selectedState = stateField.getText().toString();
        selectedCity = cityField.getText().toString();
        int month = datePicker.getMonth() + 1;
        dateOfBirth = datePicker.getDayOfMonth() + "-" + month + "-" + datePicker.getYear();

        countryCodePicker.registerCarrierNumberEditText(phoneNumberField);

        phoneNumber = countryCodePicker.getFullNumberWithPlus();
        phoneNumberCountry = countryCodePicker.getSelectedCountryCode();
        emailAddress = emailAddressField.getText().toString();
    }

    private void showDataOnUI() {
        firstNameField.setText(userData.getFirstName());
        lastNameField.setText(userData.getLastName());
        emailAddressField.setText(userData.getEmailAddress());
        countryField.setText(userData.getCountry());
        stateField.setText(userData.getState());
        cityField.setText(userData.getCity());
        phoneNumberField.setText(userData.getPhoneNumber());

        if (userData.getGender().equalsIgnoreCase("male")) {
            maleRadioButton.setChecked(true);
            gender = "Male";
        } else if (userData.getGender().equalsIgnoreCase("female")) {
            femaleRadioButton.setChecked(true);
            gender = "Female";
        } else {
            otherRadioButton.setChecked(true);
            gender = "Other";
        }

        if (userData.getUser().equalsIgnoreCase("tenant")) {
            tenantRadioButton.setChecked(true);
            user = "Tenant";
        } else {
            freelancerRadioButton.setChecked(true);
            user = "Freelancer";
        }

        String[] splitDateOfBirth = userData.getDateOfBirth().split("-");
        datePicker.updateDate(Integer.parseInt(splitDateOfBirth[2]), Integer.parseInt(splitDateOfBirth[1]) - 1, Integer.parseInt(splitDateOfBirth[0]));
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        userData = (User) bundle.getSerializable("user");
    }

    private void initializeAllVariables() {
        firstNameField = findViewById(R.id.tenant_edit_profile_first_name_field);
        lastNameField = findViewById(R.id.tenant_edit_profile_last_name_field);
        countryField = findViewById(R.id.tenant_edit_profile_country_field);
        stateField = findViewById(R.id.tenant_edit_profile_state_field);
        cityField = findViewById(R.id.tenant_edit_profile_city_field);
        genderRadioGroup = findViewById(R.id.tenant_edit_profile_two_gender_radio_group);
        userRadioGroup = findViewById(R.id.tenant_edit_profile_two_user_radio_group);
        datePicker = findViewById(R.id.tenant_edit_profile_two_dob_date_picker_view);
        emailAddressField = findViewById(R.id.tenant_edit_profile_email_field);
        phoneNumberField = findViewById(R.id.tenant_edit_profile_three_number_field);
        maleRadioButton = findViewById(R.id.tenant_edit_profile_two_gender_male);
        femaleRadioButton = findViewById(R.id.tenant_edit_profile_two_gender_female);
        otherRadioButton = findViewById(R.id.tenant_edit_profile_two_gender_other);
        tenantRadioButton = findViewById(R.id.tenant_edit_profile_two_user_user);
        freelancerRadioButton = findViewById(R.id.tenant_edit_profile_two_user_freelancer);
        saveButton = findViewById(R.id.tenant_edit_profile_save_button);
        countryCodePicker = findViewById(R.id.tenant_edit_profile_country_code_picker);
        backImageView = findViewById(R.id.tenant_edit_profile_back_image_view);
    }
}