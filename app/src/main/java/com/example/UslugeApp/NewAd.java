package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Calendar;


public class NewAd extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText adName, adDescription;
    Spinner adCategory;
    Button saveAdBtn;
    String adCounty, phoneNum, advertiserName;
    ImageView addAdImage;
    String adImageUrl;
    ProgressBar progressBar;
    boolean imageUpload = false;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;
    StorageReference storageReference;
    String adImageID = UUID.randomUUID().toString();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_ad);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        final String adDate = dateFormat.format(date);

        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        adName = findViewById(R.id.adName);
        adDescription = findViewById(R.id.adDescription);
        adDescription.setMovementMethod(new ScrollingMovementMethod());

        adCategory = findViewById(R.id.adCategory);
        ArrayAdapter adCategorySpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerAdCategories,
                R.layout.spinner_text_2
        );
        adCategorySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adCategory.setAdapter(adCategorySpinnerAdapter);

        saveAdBtn = findViewById(R.id.saveAdBtn);
        addAdImage = findViewById(R.id.add_ad_image);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();


        assert user != null;
        final String userID = user.getUid();



        addAdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });



        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                adCounty = documentSnapshot.getString("county");
                phoneNum = documentSnapshot.getString("phone");
                advertiserName =documentSnapshot.getString("fname");
            }
        });


        saveAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = adName.getText().toString();
                String category = String.valueOf(adCategory.getSelectedItemId());
                String desc = adDescription.getText().toString();

                if (TextUtils.isEmpty(name)){
                    adName.setError("Naziv je obavezan");
                    return;
                }

                if (TextUtils.isEmpty(desc)){
                    adDescription.setError("Opis je obavezan");
                    return;
                }
                if (imageUpload){

                DocumentReference documentReference = fStore.collection("adCategory").document(category).collection("ads").document();
                Map <String, Object> ad = new HashMap<>();

                ad.put("userID", userID);
                ad.put("adName", name);
                ad.put("adDesc", desc);
                ad.put("adCounty", adCounty);
                ad.put("adImageUrl", adImageUrl);
                ad.put("adCategory", category);
                ad.put("adID", documentReference.getId());
                ad.put("adRating", 0);
                ad.put("numOfRatings", 0);
                ad.put("phoneNum", phoneNum);
                ad.put("advertiserName", advertiserName);
                ad.put("adDate", adDate);


                documentReference.set(ad).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess");
                        Toast.makeText(NewAd.this, "Uspješno", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                        Toast.makeText(NewAd.this, "Neuspješno", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(getApplicationContext(),MyAds.class));

            } else {
                    Toast.makeText(NewAd.this, "Slika je obavezna", Toast.LENGTH_SHORT).show();
                }
        }}
        );
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
                progressBar.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }

        }
    }

    private void uploadImageToFirebase(final Uri imageUri) {
        final StorageReference fileRef = storageReference.child("ads/" + adImageID + "_AD.jpeg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(addAdImage);
                        adImageUrl = uri.toString();
                        progressBar.setVisibility(View.GONE);
                        imageUpload = true;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewAd.this, "Neuspješan prijenos slike." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (adImageUrl != null){
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(adImageUrl);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e("firebasestorage", "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebasestorage", "onFailure: did not delete file");
                }
            });
            Intent myIntent = new Intent(NewAd.this, SearchAds.class);
            NewAd.this.startActivity(myIntent);
        }
        Intent myIntent = new Intent(NewAd.this, SearchAds.class);
        NewAd.this.startActivity(myIntent);
    }
}
