package com.example.UslugeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class SearchAds extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;

    private FirestorePagingAdapter adapter;

    Spinner adCategorySearch;
    Spinner countySearch;
    Button searchBtn;
    String user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_ads);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.adsList);

        adCategorySearch = findViewById(R.id.adCategorySpinner);
        ArrayAdapter adCategorySpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerAdCategories,
                R.layout.spinner_text_3
        );
        adCategorySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adCategorySearch.setAdapter(adCategorySpinnerAdapter);


        countySearch = findViewById(R.id.countySpinner);
        ArrayAdapter adCountySpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerCounties,
                R.layout.spinner_text_3
        );
        adCountySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        countySearch.setAdapter(adCountySpinnerAdapter);


        searchBtn = findViewById(R.id.searchBtn);

        fAuth = FirebaseAuth.getInstance();
        user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


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

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categoryID = String.valueOf(adCategorySearch.getSelectedItemId());
                String county = String.valueOf(countySearch.getSelectedItemId());

                Query query = firebaseFirestore.collection("adCategory").document(categoryID).
                        collection("ads").whereEqualTo("adCounty", county);

                PagedList.Config config = new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)
                        .setPageSize(3)
                        .build();

                FirestorePagingOptions<AdModel> options = new FirestorePagingOptions.Builder<AdModel>()
                        .setLifecycleOwner(SearchAds.this)
                        .setQuery(query, config, AdModel.class)
                        .build();

                adapter = new FirestorePagingAdapter<AdModel, AdsViewHolder>(options) {
                    @NonNull
                    @Override
                    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                        return new AdsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AdsViewHolder holder, int position, @NonNull AdModel model) {
                        if (user.equals(model.getUserID())) {
                            holder.itemView.setVisibility(View.GONE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        } else {
                            holder.ad_name.setText(model.getAdName());
                            holder.ad_desc.setText(model.getAdDesc());
                            Double rating = model.getAdRating();
                            holder.ad_rating_txt = String.format("%.2f", rating);
                            if (rating.equals(0.0)) {
                                holder.ad_rating.setText("N/A");
                            } else {
                                holder.ad_rating.setText(String.format("%.2f", rating));
                            }
                            Picasso.get().load(model.getAdImageUrl()).into(holder.ad_image);

                            holder.ad_ID = model.getAdID();
                            holder.ad_county = model.getAdCounty();
                            holder.ad_category = model.getAdCategory();
                            holder.ad_date.setText(model.getAdDate());
                            holder.user_id = model.getUserID();
                            holder.imageUrl = model.getAdImageUrl();
                            holder.ad_name_txt = model.getAdName();
                            holder.ad_desc_text = model.getAdDesc();
                        }
                    }
                    @Override
                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
                        super.onLoadingStateChanged(state);
                        switch (state) {
                            case LOADING_INITIAL:
                                // Toast.makeText(MainActivity.this, "Učitavanje početnih podataka", Toast.LENGTH_SHORT).show();
                                break;
                            case LOADING_MORE:
                                //   Toast.makeText(MainActivity.this, "Ucitavanje nove stranice", Toast.LENGTH_SHORT).show();
                                break;
                            case FINISHED:
                                if (getItemCount() == 0) {
                                    Toast.makeText(SearchAds.this, "Trenutno nema oglasa koji zadovoljavaju postavljene kriterije.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case ERROR:
                                //    Toast.makeText(MainActivity.this, "Greska", Toast.LENGTH_SHORT).show();
                                break;
                            case LOADED:
                                //  Toast.makeText(MainActivity.this, "Ukupan broj ucitanih itema: " + getItemCount(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                mFirestoreList.setHasFixedSize(true);
                mFirestoreList.setLayoutManager(new LinearLayoutManager(SearchAds.this));
                mFirestoreList.setAdapter(adapter);
            }
        });
    }

    private class AdsViewHolder extends RecyclerView.ViewHolder {

        private TextView ad_name;
        private TextView ad_desc;
        private TextView ad_rating;
        private ImageView ad_image;
        private String ad_ID;
        private String ad_county;
        private String ad_category;
        private String ad_rating_txt;
        private TextView ad_date;
        private String user_id;
        private String imageUrl;
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
                    i.putExtra("adName", ad_name_txt);
                    i.putExtra("adDesc", ad_desc_text);
                    i.putExtra("adImage", imageUrl);
                    i.putExtra("adCounty", ad_county);
                    i.putExtra("adCategory", ad_category);
                    i.putExtra("adRating", ad_rating_txt);
                    i.putExtra("userID", user_id);
                    startActivity(i);

                }
            });
        }
    }

    public void onBackPressed() {
        MaterialAlertDialogBuilder onBackMinimizeApp = new MaterialAlertDialogBuilder(SearchAds.this);
        onBackMinimizeApp.setTitle("Želite minimizirati aplikaciju?");
        onBackMinimizeApp.setBackground(getResources().getDrawable(R.drawable.alert_dialog_bg));


        onBackMinimizeApp.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            minimizeApp();
            }
        });
        onBackMinimizeApp.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close
            }
        });
        onBackMinimizeApp.show();
    }


    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}



