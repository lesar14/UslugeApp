package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.Objects;


public class Profile extends AppCompatActivity {

    TextView fullName, email, phoneNum, county, city;
    String countyId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button resetPasswordLocal, changeProfileBtn;
    ImageView profileImage;
    FirebaseUser user;
    StorageReference storageReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        fullName = findViewById(R.id.profileFullName);
        email = findViewById(R.id.profileEmail);
        phoneNum = findViewById(R.id.profilePhoneNum);
        county = findViewById(R.id.profileCounty);
        city = findViewById(R.id.profileCity);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);

        storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference profileRef = storageReference.child("users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "profile.jpeg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        resetPasswordLocal = findViewById(R.id.resetPasswordLocalBtn);
        profileImage = findViewById(R.id.profileImageView);
        changeProfileBtn = findViewById(R.id.saveProfileBtn);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), SearchAds.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_my_ads:
                        startActivity(new Intent(getApplicationContext(),MyAds.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_ordered_ads:
                        startActivity(new Intent(getApplicationContext(), AdsOrdered.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.nav_ad_to_do:
                        startActivity(new Intent(getApplicationContext(), AdsToDo.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                fullName.setText(documentSnapshot != null ? documentSnapshot.getString("fname") : null);
                email.setText(documentSnapshot != null ? documentSnapshot.getString("email") : null);
                phoneNum.setText(documentSnapshot != null ? documentSnapshot.getString("phone"): null);
                city.setText(documentSnapshot != null ? documentSnapshot.getString("city"): null);
                countyId = documentSnapshot != null ? documentSnapshot.getString("county") : null;


               DocumentReference documentReference1 = fStore.collection("counties").document(String.valueOf(countyId));
                documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        county.setText(snapshot.getString("County"));
                    }
                }
            });
            }
        });


        resetPasswordLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText newPass = new EditText(v.getContext());
                newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                MaterialAlertDialogBuilder resetPassword = new MaterialAlertDialogBuilder(Profile.this);
                resetPassword.setTitle("Resetiranje lozinke?");
                resetPassword.setMessage("Unesite novu lozinku.");
                resetPassword.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));
                resetPassword.setView(newPass);

                resetPassword.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the mail and send reset link
                        String newPassword = newPass.getText().toString();

                        if (newPassword.length() < 6) {
                            Toast.makeText(Profile.this, "Lozinka mora imati minimalno 6 znakova.", Toast.LENGTH_SHORT).show();
                        }
                        else {

                           user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(Profile.this, "Lozinka je uspješno promijenjena", Toast.LENGTH_SHORT).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(Profile.this, "Neuspješna promjena lozinke" + e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });
                        }
                    }
                });

                resetPassword.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });
                resetPassword.show();
            }
        });

        changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ProfileEdit.class);
                i.putExtra("fullName", fullName.getText().toString());
                i.putExtra("email", email.getText().toString());
                i.putExtra("phone", phoneNum.getText().toString());
                i.putExtra("city", city.getText().toString());
                i.putExtra("county", countyId);

                startActivity(i);
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        intent.putExtra("finish", true); // if you are checking for this in your other Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void onBackPressed() {
        Intent myIntent = new Intent(Profile.this, SearchAds.class);
        Profile.this.startActivity(myIntent);
        }
}

