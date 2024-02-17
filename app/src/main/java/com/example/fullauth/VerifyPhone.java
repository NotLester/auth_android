package com.example.fullauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {
    EditText otpNumberOne, otpNumberTwo, otpNumberThree, otpNumberFour, otpNumberFive, otpNumberSix;
    Button verifyPhone, resendOTP;
    Boolean otpValid = true;
    FirebaseAuth fAuth;
    PhoneAuthCredential phoneAuthCredential;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String verificationId;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        Intent data = getIntent();
        phone = data.getStringExtra("phone");

        fAuth = FirebaseAuth.getInstance();

        otpNumberOne = findViewById(R.id.otpNumberOne);
        otpNumberTwo = findViewById(R.id.otpNumberTwo);
        otpNumberThree = findViewById(R.id.otpNumberThree);
        otpNumberFour = findViewById(R.id.otpNumberFour);
        otpNumberFive = findViewById(R.id.otpNumberFive);
        otpNumberSix = findViewById(R.id.otpNumberSix);

        verifyPhone = findViewById(R.id.verifyPhoneBTn);
        resendOTP = findViewById(R.id.resendOTP);

        verifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(otpNumberOne);
                validateData(otpNumberTwo);
                validateData(otpNumberThree);
                validateData(otpNumberFour);
                validateData(otpNumberFive);
                validateData(otpNumberSix);

                if (otpValid) {
                    //send OTP to the user
                    String otp =
                            otpNumberOne.getText().toString() + otpNumberTwo.getText().toString() +
                                    otpNumberThree.getText().toString() + otpNumberFour.getText().toString() +
                                    otpNumberFive.getText().toString() + otpNumberSix.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

                    verifyAuthentication(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
                resendOTP.setVisibility(View.GONE);
            }
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendOTP.setVisibility(View.VISIBLE);
            }
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyAuthentication(phoneAuthCredential);
                resendOTP.setVisibility(View.GONE);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerifyPhone.this, "OTP Verification Failed...", Toast.LENGTH_SHORT).show();
            }
        };

        sendOTP(phone);

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP(phone);
            }
        });
    }

    public void sendOTP(String phoneNumber) {
        PhoneAuthProvider
                .getInstance()
                .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks);
    }

    public void resendOTP(String phoneNumber) {
        PhoneAuthProvider
                .getInstance()
                .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks, token);
    }

    public void validateData(EditText field) {
        if (field.getText().toString().isEmpty()) {
            this.otpValid = false;
            field.setError("Required");
        } else {
            this.otpValid = true;
        }
    }

    public void verifyAuthentication(PhoneAuthCredential credential) {
        fAuth
                .getCurrentUser()
                .linkWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(VerifyPhone.this, "Account created and linked", Toast.LENGTH_SHORT)
                                .show();
                        //move to dashboard
                    }
        });
    }
}