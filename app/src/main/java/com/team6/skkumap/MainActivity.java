package com.team6.skkumap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText etPhoneNum;
    private EditText etCode;

    private FirebaseAuth mAuth;

    public String phoneNum = null;
    public String userInputCode = null;
    private String validCode = null;
    PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etCode = (EditText) findViewById(R.id.etCode);

        Button btnEnterPhoneNum = (Button) findViewById(R.id.btnEnterPhoneNum);
        btnEnterPhoneNum.setOnClickListener(v -> {
            phoneNum = "+82" + etPhoneNum.getText().toString().substring(1);
            Toast.makeText(MainActivity.this, phoneNum, Toast.LENGTH_SHORT).show();
            startVerification();
        });

        Button btnEnterCode = (Button) findViewById(R.id.btnEnterCode);
        btnEnterCode.setOnClickListener(v -> {
            userInputCode = etCode.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(validCode, userInputCode);
            signInWithPhoneAuthCredential(credential);
            Toast.makeText(MainActivity.this, userInputCode, Toast.LENGTH_SHORT).show();
        });

    }

    private void startVerification() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("kr");
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        validCode = verificationId;
                        resendToken = forceResendingToken;
                        Toast.makeText(MainActivity.this, "SMS was sent", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(MainActivity.this, "SMS sending succeed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, "SMS sending failed", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Yes!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "No!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}