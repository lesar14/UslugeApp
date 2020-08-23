package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AdDetails extends AppCompatActivity {

    public static final String TAG = "TAG";

    String adID, adCategory, adAdvertiserID, adAdvertiserName, adImageAdvertised, adNameAdvertised, adDescAdvertised, currentUser, adCityTxt, adCategoryOrdered, adClientName, adClientPhone, adClientCity, advertiserPhone;
    Double adRating, numOfRatings;
    Boolean myAds, orderedAds, adsToDo;
    Boolean  adExists = false;
    FirebaseFirestore fStore;
    TextView adName, adDesc, adAdvertiser, adCity, adClientNameTV, PhoneTV, CityTxt, AdvertiserTxt, NameTxt, PhoneTxt, adDateTV, adDateTxt, adRatingTxt, adRatingTV;
    ImageView adImage;
    StorageReference storageReference;
    Button deleteAd, orderAd, adDoneBtn, adRatingBtn;
    FirebaseAuth fAuth;
    ScrollView scrollView;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad_details);


        fStore = FirebaseFirestore.getInstance();

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        final String adDate = dateFormat.format(date);

        adName = findViewById(R.id.adName);
        adDesc = findViewById(R.id.adDesc);


        scrollView = findViewById(R.id.scrollView);

        adDesc.setMovementMethod(new ScrollingMovementMethod());

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                adDesc.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        adDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                adDesc.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        adAdvertiser = findViewById(R.id.adAdvertiser);
        adCity = findViewById(R.id.adCity);
        adImage = findViewById(R.id.adImage);
        storageReference = FirebaseStorage.getInstance().getReference();
        deleteAd = findViewById(R.id.deleteAd);
        orderAd = findViewById(R.id.orderAd);
        adDoneBtn = findViewById(R.id.adDone);
        adRatingBtn = findViewById(R.id.adRatingBtn);
        adClientNameTV = findViewById(R.id.adClientName);
        PhoneTV = findViewById(R.id.adClientPhone);

        CityTxt = findViewById(R.id.CityText);
        AdvertiserTxt = findViewById(R.id.AdvertiserTxt);
        NameTxt = findViewById(R.id.ClientNameTxt);
        PhoneTxt = findViewById(R.id.ClientPhoneTxt);
        adDateTV = findViewById(R.id.adDate);
        adDateTxt = findViewById(R.id.adDateTxt);
        adRatingTxt = findViewById(R.id.adRatingTxt);
        adRatingTV = findViewById(R.id.adRating);


        fAuth = FirebaseAuth.getInstance();
        currentUser = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        final Intent data = getIntent();
        adID = data.getStringExtra("adID");
        adCategory = data.getStringExtra("adCategory");
        adCategoryOrdered = data.getStringExtra("adCategoryOrdered");
        myAds = data.getBooleanExtra("myAds", false);
        orderedAds = data.getBooleanExtra("orderedAds", false);
        adsToDo = data.getBooleanExtra("adsToDo", false);



        if (orderedAds){
            adAdvertiser.setVisibility(View.VISIBLE);
            adCity.setVisibility(View.VISIBLE);
            AdvertiserTxt.setVisibility(View.VISIBLE);
            CityTxt.setVisibility(View.VISIBLE);
            PhoneTxt.setVisibility(View.VISIBLE);
            PhoneTV.setVisibility(View.VISIBLE);
            adDateTV.setVisibility(View.VISIBLE);
            adDateTxt.setVisibility(View.VISIBLE);



            Intent data1 = getIntent();
            adName.setText(data1.getStringExtra("adName"));
            adDesc.setText(data1.getStringExtra("adDesc"));
            adCity.setText(data1.getStringExtra("adCity"));
            adAdvertiser.setText(data1.getStringExtra("adAdvertiser"));
            Boolean adRatingBoolean = data1.getBooleanExtra("adRatingBoolean", false);
            String adCategory = data1.getStringExtra("adCategoryOrdered");
            String adID = data1.getStringExtra("adID");
            PhoneTV.setText(data1.getStringExtra("advertiser_phone"));
            adDateTV.setText(data1.getStringExtra("adDate"));
            adDateTxt.setText("Datum narudžbe: ");

            final DocumentReference documentReference = fStore.collection("adCategory").document(adCategory).collection("ads").document(adID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    adRating = documentSnapshot.getDouble("adRating");
                    numOfRatings = documentSnapshot.getDouble("numOfRatings");

                }
            });

            if (adRatingBoolean){
            adRatingBtn.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(this, "Nakon što usluga bude odrađena, imat ćete mogućnost da ju ocjenite.", Toast.LENGTH_LONG).show();
            }


            String adImageTxt = data1.getStringExtra("adImage");
            Picasso.get().load(adImageTxt).into(adImage);
        }

        else if (adsToDo){
            adDoneBtn.setVisibility(View.VISIBLE);
            adClientNameTV.setVisibility(View.VISIBLE);
            PhoneTV.setVisibility(View.VISIBLE);
            CityTxt.setVisibility(View.VISIBLE);
            adCity.setVisibility(View.VISIBLE);
            NameTxt.setVisibility(View.VISIBLE);
            PhoneTxt.setVisibility(View.VISIBLE);
            adDateTV.setVisibility(View.VISIBLE);
            adDateTxt.setVisibility(View.VISIBLE);

            Intent data1 = getIntent();
            adName.setText(data1.getStringExtra("adName"));
            adDesc.setText(data1.getStringExtra("adDesc"));
            final String adClient = data1.getStringExtra("adClient");
            final String adToDoID = data1.getStringExtra("adToDoID");
            adClientName = data1.getStringExtra("clientName");
            adClientPhone = data1.getStringExtra("clientPhone");
            adCity.setText(data1.getStringExtra("clientCity"));
            adDateTV.setText(data1.getStringExtra("adDate"));
            adDateTxt.setText("Datum narudžbe: ");

            String adImageTxt = data1.getStringExtra("adImage");
            Picasso.get().load(adImageTxt).into(adImage);

            adClientNameTV.setText(adClientName);
            PhoneTV.setText(adClientPhone);

            adDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DocumentReference documentReference = fStore.collection("users").document(adClient).collection("orderedAds").document(adID);
                    Map<String, Object> edited = new HashMap<>();
                    edited.put("adRatingBoolean", true);

                    documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AdDetails.this, "Uspješno", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SearchAds.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdDetails.this, "Neuspješno.  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    DocumentReference documentReference1 = fStore.collection("users").document(currentUser).collection("adsToDo").document(adToDoID);
                    documentReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent myIntent = new Intent(AdDetails.this, SearchAds.class);
                            AdDetails.this.startActivity(myIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdDetails.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }

        else {
                // SearchAds & MyAds
            final DocumentReference documentReference = fStore.collection("adCategory").document(adCategory).collection("ads").document(adID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                orderAd.setVisibility(View.VISIBLE);
                adAdvertiser.setVisibility(View.VISIBLE);
                adCity.setVisibility(View.VISIBLE);
                AdvertiserTxt.setVisibility(View.VISIBLE);
                CityTxt.setVisibility(View.VISIBLE);
                PhoneTxt.setVisibility(View.VISIBLE);
                PhoneTV.setVisibility(View.VISIBLE);
                adRatingTxt.setVisibility(View.VISIBLE);
                adRatingTV.setVisibility(View.VISIBLE);

                adNameAdvertised = documentSnapshot.getString("adName");
                adName.setText(adNameAdvertised);

                adDescAdvertised = documentSnapshot.getString("adDesc");
                adDesc.setText(adDescAdvertised);

                adImageAdvertised = documentSnapshot.getString("adImageUrl");
                Picasso.get().load(adImageAdvertised).into(adImage);

                adAdvertiserID = documentSnapshot.getString("userID");

                Intent data1 = getIntent();
                String rating = data1.getStringExtra("adRating");

                if (rating.equals("0,00")){
                    adRatingTV.setText("N/A");
                }else {
                    adRatingTV.setText(rating);
                }

                DocumentReference documentReference1 = fStore.collection("users").document(String.valueOf(adAdvertiserID));
                documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            adAdvertiserName = snapshot.getString("fname");
                            adAdvertiser.setText(adAdvertiserName);

                            adCityTxt = snapshot.getString("city");
                            adCity.setText(adCityTxt);

                            advertiserPhone = snapshot.getString("phone");
                            PhoneTV.setText(advertiserPhone);
                        }
                    }

                });

                if (myAds){

                    Intent data = getIntent();
                    final String adImageUrl = data.getStringExtra("adImageUrl");

                    orderAd.setVisibility(View.GONE);
                    deleteAd.setVisibility(View.VISIBLE);
                    deleteAd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MaterialAlertDialogBuilder delete = new MaterialAlertDialogBuilder(AdDetails.this);
                            delete.setTitle("Brisanje oglasa?");
                            delete.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));

                            delete.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DocumentReference documentRef = fStore.collection("adCategory").document(adCategory).collection("ads").document(adID);
                                    documentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AdDetails.this, "Oglas uspješno obrisan.", Toast.LENGTH_SHORT).show();

                                            Intent myIntent = new Intent(AdDetails.this, MyAds.class);
                                            AdDetails.this.startActivity(myIntent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AdDetails.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    StorageReference storageRef = null;
                                    if (adImageUrl != null) {
                                        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(adImageUrl);
                                    }
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
                                }

                            });

                            delete.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            delete.show();
                        }
                    });
                }

                else {

                orderAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference1 = fStore.collection("users").document(currentUser);
                    documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot snapshot = task.getResult();
                                adClientName = snapshot.getString("fname");
                                adClientPhone = snapshot.getString("phone");
                                adClientCity = snapshot.getString("city");

                                DocumentReference documentReference = fStore.collection("users").document(adAdvertiserID).collection("adsToDo").document(adID+currentUser);
                                Map<String, Object> ad = new HashMap<>();

                                ad.put("adID", adID);
                                ad.put("adName", adNameAdvertised);
                                ad.put("adDesc", adDescAdvertised);
                                ad.put("adImageUrl", adImageAdvertised);
                                ad.put("adClient", currentUser);
                                ad.put("adCategory", adCategory);
                                ad.put("adToDoID", adID+currentUser);
                                ad.put("clientName", adClientName);
                                ad.put("clientPhone", adClientPhone);
                                ad.put("clientCity", adClientCity);
                                ad.put("adDate", adDate);

                                documentReference.set(ad).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Success");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.toString());
                                    }
                                });

                                DocumentReference documentReference2 = fStore.collection("users").document(currentUser).collection("orderedAds").document(adID);
                                Map<String, Object> ad1 = new HashMap<>();

                                ad1.put("adID", adID);
                                ad1.put("adName", adNameAdvertised);
                                ad1.put("adDesc", adDescAdvertised);
                                ad1.put("adImageUrl", adImageAdvertised);
                                ad1.put("adCity", adCityTxt);
                                ad1.put("adAdvertiserName", adAdvertiserName);
                                ad1.put("adAdvertiserID", adAdvertiserID);
                                ad1.put("adCategory", adCategory);
                                ad1.put("advertiserPhone", advertiserPhone);
                                ad1.put("adDate", adDate);
                                ad1.put("adRatingBoolean", false);

                                documentReference2.set(ad1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Success");
                                        Toast.makeText(AdDetails.this, "Uspješno naručeno.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.toString());
                                    }
                                });
                            }
                            Intent myIntent = new Intent(AdDetails.this, AdsOrdered.class);
                            AdDetails.this.startActivity(myIntent);
                        }
                    });
                }
            });
            }
            }
        });
        }

        adRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("adCategory").document(adCategoryOrdered).collection("ads").document(adID);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            adExists = true;
                        }
                    }
                });

                final EditText adRatingUser = new EditText(v.getContext());
                adRatingUser.setInputType(InputType.TYPE_CLASS_NUMBER);

                MaterialAlertDialogBuilder rating = new MaterialAlertDialogBuilder(AdDetails.this);
                rating.setTitle("Ocjenite oglas: ");
                rating.setView(adRatingUser);
                // builder.setIcon(R.drawable.ic_rating_star_3);
                rating.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));

                rating.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ratingString = adRatingUser.getText().toString();

                        if (ratingString.length() < 1 ) {
                            Toast.makeText(AdDetails.this, "Unesite ocjenu od 1 do 5", Toast.LENGTH_SHORT).show();
                        }else{
                            final Double rating = Double.parseDouble(ratingString);
                            if (rating < 1  || rating > 5) {
                                Toast.makeText(AdDetails.this, "Unesite ocjenu od 1 do 5", Toast.LENGTH_SHORT).show();
                            }

                            else {

                                if (adExists) {
                                    DocumentReference documentReference1 = fStore.collection("adCategory").document(adCategoryOrdered).collection("ads").document(adID);
                                    Map<String, Object> ad = new HashMap<>();

                                    Double sum = adRating * numOfRatings + rating;
                                    Double newRating = sum / (numOfRatings+1);

                                    ad.put("adRating", newRating);
                                    ad.put("numOfRatings", numOfRatings+1);

                                    documentReference1.update(ad).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Success");
                                            Toast.makeText(AdDetails.this, "Ocjena uspješno unesena.", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            Toast.makeText(AdDetails.this, "Neuspješno ocijenjivanje", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                DocumentReference documentReference2 = fStore.collection("users").document(currentUser).collection("orderedAds").document(adID);
                                documentReference2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent myIntent = new Intent(AdDetails.this, SearchAds.class);
                                        AdDetails.this.startActivity(myIntent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdDetails.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }

                });

                rating.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                rating.show();

            }
        });
    }
}
