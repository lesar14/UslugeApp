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


public class AdsToDo extends AppCompatActivity {

    Query query;
    FirebaseAuth fAuth;
    String user;
    TextView adsToDo_noAds;


    private FirebaseFirestore firebaseFirestore;
    private FirestorePagingAdapter adapter;
    private RecyclerView adsToDoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ads_to_do);

        fAuth = FirebaseAuth.getInstance();
        user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        adsToDoList = findViewById(R.id.adsToDoList);
        adsToDo_noAds = findViewById(R.id.adsToDo_noAds);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_ad_to_do);

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

        query = firebaseFirestore.collection("users").document(user).collection("adsToDo");

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<AdModel> options = new FirestorePagingOptions.Builder<AdModel>()
                .setLifecycleOwner(AdsToDo.this)
                .setQuery(query, config, AdModel.class)
                .build();


        adapter = new FirestorePagingAdapter<AdModel, AdsToDo.AdsViewHolder>(options) {
            @NonNull
            @Override
            public AdsToDo.AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new AdsToDo.AdsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AdsToDo.AdsViewHolder holder, int position, @NonNull AdModel model) {
                holder.ad_name.setText(model.getAdName());
                holder.ad_desc.setText(model.getAdDesc());
                Picasso.get().load(model.getAdImageUrl()).into(holder.ad_image);
                holder.ad_rating.setVisibility(View.INVISIBLE);
                holder.ad_date.setText(model.getAdDate());

                holder.ad_name_txt = model.getAdName();
                holder.ad_desc_txt = model.getAdDesc();
                holder.ad_image_txt = model.getAdImageUrl();
                holder.ad_client = model.getAdClient();
                holder.ad_ID = model.getAdID();
                holder.adToDoID = model.getAdToDoID();
                holder.client_name = model.getClientName();
                holder.client_phone = model.getClientPhone();
                holder.client_city = model.getClientCity();
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
                            adsToDo_noAds.setVisibility(View.VISIBLE);
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

        adsToDoList.setHasFixedSize(true);
        adsToDoList.setLayoutManager(new LinearLayoutManager(AdsToDo.this));
        adsToDoList.setAdapter(adapter);
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
        private String ad_client;
        private String ad_ID;
        private String adToDoID;
        private String client_name;
        private String client_phone;
        private String client_city;

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
                    i.putExtra("adName", ad_name_txt);
                    i.putExtra("adDesc", ad_desc_txt);
                    i.putExtra("adImage", ad_image_txt);
                    i.putExtra("adsToDo", true);
                    i.putExtra("adClient", ad_client);
                    i.putExtra("adID", ad_ID);
                    i.putExtra("adToDoID", adToDoID);
                    i.putExtra("clientName", client_name);
                    i.putExtra("clientPhone", client_phone);
                    i.putExtra("clientCity", client_city);
                    i.putExtra("adDate", ad_date.getText());
                    startActivity(i);
                }
            });
        }
    }


}



