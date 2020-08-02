package com.example.login_register;

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
import android.widget.ImageView;
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

public class AdsOrdered extends AppCompatActivity {

    Query query;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user;

    private RecyclerView orderedAdsList;
    private FirebaseFirestore firebaseFirestore;
    private FirestorePagingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ordered_ads);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        orderedAdsList = findViewById(R.id.orderedAdsList);
        user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_ordered_ads);

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

        query = firebaseFirestore.collection("users").document(user).collection("orderedAds");


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<AdModel> options = new FirestorePagingOptions.Builder<AdModel>()
                .setLifecycleOwner(AdsOrdered.this)
                .setQuery(query, config, AdModel.class)
                .build();


        adapter = new FirestorePagingAdapter<AdModel, AdsOrdered.AdsViewHolder>(options) {
            @NonNull
            @Override
            public AdsOrdered.AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new AdsOrdered.AdsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AdsOrdered.AdsViewHolder holder, int position, @NonNull AdModel model) {
                holder.ad_name.setText(model.getAdName());
                holder.ad_desc.setText(model.getAdDesc());
                Picasso.get().load(model.getAdImageUrl()).into(holder.ad_image);
                holder.ad_date.setText(model.getAdDate());

                holder.ad_name_txt = model.getAdName();
                holder.ad_desc_txt = model.getAdDesc();
                holder.ad_image_txt = model.getAdImageUrl();
                holder.ad_city = model.getAdCity();
                holder.ad_advertiser = model.getAdAdvertiserName();
                holder.ad_rating_boolean = model.getAdRatingBoolean();
                holder.ad_category = model.getAdCategory();
                holder.ad_id = model.getAdID();
                holder.advertiser_phone = model.getAdvertiserPhone();
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
                        if (getItemCount()==0) {
                            Toast.makeText(AdsOrdered.this, "Trenutno nemate naručenih oglasa.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ERROR:
                        //   Toast.makeText(MyAds.this, "Greska", Toast.LENGTH_SHORT).show();
                        break;
                    case LOADED:
                        //   Toast.makeText(MyAds.this, "Ukupan broj ucitanih itema: "+ getItemCount(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        orderedAdsList.setHasFixedSize(true);
        orderedAdsList.setLayoutManager(new LinearLayoutManager(AdsOrdered.this));
        orderedAdsList.setAdapter(adapter);
    }

    private class AdsViewHolder  extends RecyclerView.ViewHolder{

        private TextView ad_name;
        private TextView ad_desc;
        private ImageView ad_image;
        private TextView ad_rating;
        private TextView ad_date;

        private String ad_name_txt;
        private String ad_desc_txt;
        private String ad_image_txt;
        private String ad_city;
        private String ad_advertiser;
        private Boolean ad_rating_boolean;
        private String ad_category;
        private String ad_id;
        private String advertiser_phone;


        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);

            ad_name = itemView.findViewById(R.id.ad_name);
            ad_desc = itemView.findViewById(R.id.ad_description);
            ad_image = itemView.findViewById(R.id.ad_image);
            ad_date = itemView.findViewById(R.id.ad_date);

            ad_rating = itemView.findViewById(R.id.ad_rating);
            ad_rating.setVisibility(View.INVISIBLE);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AdDetails.class);
                    i.putExtra("orderedAds", true);
                    i.putExtra("adName", ad_name_txt);
                    i.putExtra("adDesc", ad_desc_txt);
                    i.putExtra("adImage", ad_image_txt);
                    i.putExtra("adCity", ad_city);
                    i.putExtra("adAdvertiser", ad_advertiser);
                    i.putExtra("adRatingBoolean", ad_rating_boolean);
                    i.putExtra("adCategoryOrdered", ad_category);
                    i.putExtra("adID", ad_id);
                    i.putExtra("advertiser_phone", advertiser_phone);
                    i.putExtra("adDate", ad_date.getText());
                    startActivity(i);
                }
            });
        }
    }
}



