package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileEdit extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText profileFullName, profileEmail, profilePhoneNum, profileCity;
    ImageView profileImage;
    Button saveProfileBtn, goBackBtn;
    Spinner profileCounty;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_profile);

        profileFullName = findViewById(R.id.profileFullName);
        profileEmail = findViewById(R.id.profileEmailAddress);
        profilePhoneNum = findViewById(R.id.profilePhoneNum);
        profileImage = findViewById(R.id.profileImageView);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        progressBar = findViewById(R.id.progressBar);
        goBackBtn = findViewById(R.id.goBackBtn);
        profileCity = findViewById(R.id.profileCity);
        profileCounty = findViewById(R.id.profileCounty);




        profileCounty = findViewById(R.id.profileCounty);
        ArrayAdapter adCountySpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerCounties,
                R.layout.spinner_text_3
        );
        adCountySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        profileCounty.setAdapter(adCountySpinnerAdapter);



        Intent data = getIntent();
        profileFullName.setText(data.getStringExtra("fullName"));
        profileEmail.setText(data.getStringExtra("email"));
        profilePhoneNum.setText(data.getStringExtra("phone"));
        profileCity.setText(data.getStringExtra("city"));
        profileCounty.setSelection(Integer.parseInt(data.getStringExtra("county")));

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "profile.jpeg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });


        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (profileFullName.getText().toString().isEmpty() || profileEmail.getText().toString().isEmpty() ||
                        profilePhoneNum.getText().toString().isEmpty()){
                    Toast.makeText(ProfileEdit.this, "Jedno ili više polja su prazna.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String email = profileEmail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email", email);
                        edited.put("fname", profileFullName.getText().toString());
                        edited.put("phone", profilePhoneNum.getText().toString());
                        edited.put("city", profileCity.getText().toString());
                        edited.put("county", String.valueOf(profileCounty.getSelectedItemId()));
                        documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (imgUri!=null){
                                uploadImageToFirebase (imgUri);
                                }
                                Toast.makeText(ProfileEdit.this, "Profil ažuriran.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), SearchAds.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileEdit.this, "Profil nije ažuriran. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Profile.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK ) {
            Uri imageUri = null;
            if (data != null) {
                imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUri = null;
            if (result != null) {
                imageUri = result.getUri();
                Picasso.get().load(imageUri).into(profileImage);
                imgUri = imageUri;
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "profile.jpeg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileEdit.this, "Neuspješan prijenos slike." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
