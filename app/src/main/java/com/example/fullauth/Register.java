package com.example.fullauth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends Fragment {
    public static String TAG = "TAG";
    EditText personFullName, personEmailAddress, personPassword, personConfPassword, phoneCountryCode, phoneNumber;
    Button registerAccountBtn;
    Boolean isDataValid = false;
    FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        fAuth = FirebaseAuth.getInstance();

        personFullName = v.findViewById(R.id.registerFullName);
        personEmailAddress = v.findViewById(R.id.registerEmail);
        personPassword = v.findViewById(R.id.regsiterPass);
        personConfPassword = v.findViewById(R.id.retypePass);
        phoneCountryCode = v.findViewById(R.id.countryCode);
        phoneNumber = v.findViewById(R.id.registerPhoneNumber);
        registerAccountBtn = v.findViewById(R.id.registerBtn);

        //validate data
        validateData(personFullName);
        validateData(personEmailAddress);
        validateData(personPassword);
        validateData(personConfPassword);
        validateData(phoneCountryCode);
        validateData(phoneNumber);

        if (personPassword.getText().toString().equals(personConfPassword.getText().toString())) {
            this.isDataValid = false;
            personConfPassword.setError("Passwords do not match");
        } else {
            this.isDataValid = true;
        }

        if (this.isDataValid) {
            //proceed with registration
            fAuth
                    .createUserWithEmailAndPassword(personEmailAddress.getText().toString(), personPassword.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(getActivity(), "User Account Created!!", Toast.LENGTH_SHORT).show();
                    //send user to verify phone number
                    Intent phone = new Intent(getActivity(), VerifyPhone.class);
                    phone.putExtra("phone", "+"+phoneCountryCode.getText().toString()+phoneNumber.getText().toString());
                    startActivity(phone);
                    Log.d(TAG, "onSuccess: "+"+"+phoneCountryCode.getText().toString()+phoneNumber.getText().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error!! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return v;
    }

    public void validateData(EditText field) {
        if (field.getText().toString().isEmpty()) {
            this.isDataValid = false;
            field.setError("Required");
        } else {
            this.isDataValid = true;
        }
    }
}