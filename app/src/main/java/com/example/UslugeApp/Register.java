package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFUllName, mEmail, mPassword, mPasswordRepeat, mPhone, mCity;
    Button mRegisterBtn;
    ImageView goToLogin;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    Spinner mCounty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        mFUllName = findViewById(R.id.profileFullName);
        mEmail = findViewById(R.id.profileEmail);
        mPassword = findViewById(R.id.password);
        mPasswordRepeat = findViewById(R.id.passwordRepeat);
        mPhone = findViewById(R.id.profilePhoneNum);
        mRegisterBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.goToLoginBtn);

        mCounty = findViewById(R.id.profileCounty);
        ArrayAdapter adCategorySpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerCounties,
                R.layout.spinner_text_2
        );
        adCategorySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        mCounty.setAdapter(adCategorySpinnerAdapter);

        mCity = findViewById(R.id.profileCity);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);



        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), SearchAds.class));
            finish();
        }



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordRepeat = mPasswordRepeat.getText().toString().trim();
                final String fullName = mFUllName.getText().toString();
                final String phone = mPhone.getText().toString();
                final String county = String.valueOf(mCounty.getSelectedItemId());
                final String city = mCity.getText().toString();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("E-mail je obavezan");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Lozinka je obavezna");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Lozinka mora imati minimalno 6 znakova");
                    return;
                }

                if (!password.equals(passwordRepeat)){
                    mPasswordRepeat.setError("Lozinke se ne podudaraju");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                // register user

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "Korisnik kreiran.", Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map <String, Object> user = new HashMap<>();
                            user.put("fname", fullName);
                            user.put("email", email);
                            user.put("phone", phone);
                            user.put("county", county);
                            user.put("city", city);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for" + userID);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), SearchAds.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }else {
                            Toast.makeText(Register.this, "Greška "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}
