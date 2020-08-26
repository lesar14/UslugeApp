package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MyAds extends AppCompatActivity {

    Query query;
    FirebaseAuth fAuth;
    String userID;
    Spinner adCategory;
    Button newAd;

    private RecyclerView mMyAdsList;
    private FirebaseFirestore firebaseFirestore;
    private FirestorePagingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_ads);

        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mMyAdsList = findViewById(R.id.myAdsList);

        adCategory = findViewById(R.id.adCategory);
        ArrayAdapter adCategorySpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerAdCategories,
                R.layout.spinner_text_3
        );
        adCategorySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adCategory.setAdapter(adCategorySpinnerAdapter);


        newAd = findViewById(R.id.newAdBtn);


        adCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                query = firebaseFirestore.collection("adCategory").document(String.valueOf(position)).collection("ads").whereEqualTo( "userID", userID);


                PagedList.Config config = new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)
                        .setPageSize(3)
                        .build();

                FirestorePagingOptions<AdModel> options = new FirestorePagingOptions.Builder<AdModel>()
                        .setLifecycleOwner(MyAds.this)
                        .setQuery(query, config, AdModel.class)
                        .build();


                adapter = new FirestorePagingAdapter<AdModel, AdsViewHolder>(options) {
                    @NonNull
                    @Override
                    public MyAds.AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                        return new MyAds.AdsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull MyAds.AdsViewHolder holder, int position, @NonNull AdModel model) {
                        holder.ad_name.setText(model.getAdName());
                        holder.ad_desc.setText(model.getAdDesc());
                        Picasso.get().load(model.getAdImageUrl()).into(holder.ad_image);
                        Double rating = model.getAdRating();
                        holder.ad_rating_txt = String.format("%.2f", rating);
                        if (rating.equals(0.0)){
                            holder.ad_rating.setText("N/A");
                        }else {
                        holder.ad_rating.setText(String.format( "%.2f", rating));
                        }
                        holder.ad_ID = model.getAdID();
                        holder.ad_county = model.getAdCounty();
                        holder.ad_category = model.getAdCategory();
                        holder.ad_image_Url = model.getAdImageUrl();
                        holder.ad_date.setText(model.getAdDate());
                        holder.ad_date_txt = model.getAdDate();
                        holder.ad_name_txt = model.getAdName();
                        holder.ad_desc_text = model.getAdDesc();
                        holder.ad_image_Url = model.getAdImageUrl();
                    }

                    @Override
                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
                        super.onLoadingStateChanged(state);
                        switch (state){
                            case LOADING_INITIAL:
                                //   Toast.makeText(MyAds.this, "Učitavanje početnih podataka", Toast.LENGTH_SHORT).show();
                                break;
                            case LOADING_MORE:
                                //   Toast.makeText(MyAds.this, "Ucitavanje nove stranice", Toast.LENGTH_SHORT).show();
                                break;
                            case FINISHED:

                                break;
                            case ERROR:
                                //   Toast.makeText(MyAds.this, "Greska", Toast.LENGTH_SHORT).show();
                                break;
                            case LOADED:
                                if (getItemCount()==0) {
                                    Toast.makeText(MyAds.this, "Trenutno nemate objavljenih oglasa u ovoj kategoriji", Toast.LENGTH_SHORT).show();
                                }
                                //   Toast.makeText(MyAds.this, "Ukupan broj ucitanih itema: "+ getItemCount(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };

                mMyAdsList.setHasFixedSize(true);
                mMyAdsList.setLayoutManager(new LinearLayoutManager(MyAds.this));
                mMyAdsList.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_my_ads);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), SearchAds.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.nav_my_ads:
                        startActivity(new Intent(getApplicationContext(), MyAds.class));
                        overridePendingTransition(0, 0);
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

        newAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NewAd.class));
            }
        });
    }

    private class AdsViewHolder  extends RecyclerView.ViewHolder{

        private TextView ad_name;
        private TextView ad_desc;
        private ImageView ad_image;
        private TextView ad_rating;
        private String ad_ID;
        private String ad_county;
        private String ad_category;
        private String ad_image_Url;
        private TextView ad_date;
        private String ad_date_txt;
        private String ad_rating_txt;
        private String ad_name_txt;
        private String ad_desc_text;


        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);

            ad_name = itemView.findViewById(R.id.ad_name);
            ad_desc = itemView.findViewById(R.id.ad_description);
            ad_image = itemView.findViewById(R.id.ad_image);
            ad_rating = itemView.findViewById(R.id.ad_rating);
            ad_date = itemView.findViewById(R.id.ad_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(v.getContext(), AdDetails.class);
                    i.putExtra("adID", ad_ID);
                    i.putExtra("adCounty", ad_county);
                    i.putExtra("adCategory", ad_category);
                    i.putExtra("myAds", true);
                    i.putExtra("adImageUrl", ad_image_Url);
                    i.putExtra("adRating", ad_rating_txt);
                    i.putExtra("adName", ad_name_txt);
                    i.putExtra("adDesc", ad_desc_text);
                    i.putExtra("adImage", ad_image_Url);
                    i.putExtra("adDate", ad_date_txt);
                    startActivity(i);
                }
            });
        }
    }
    public void onBackPressed() {
        Intent myIntent = new Intent(MyAds.this, SearchAds.class);
        MyAds.this.startActivity(myIntent);
    }

}


