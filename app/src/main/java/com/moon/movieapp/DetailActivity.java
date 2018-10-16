package com.moon.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moon.movieapp.adapter.TrailerAdapter;
import com.moon.movieapp.api.Client;
import com.moon.movieapp.api.Service;
import com.moon.movieapp.model.Movie;
import com.moon.movieapp.model.Trailer;
import com.moon.movieapp.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private final AppCompatActivity activity = DetailActivity.this;
    @BindView(R.id.title)
    TextView nameOfMovie;
    @BindView(R.id.plotsynopsis)
    TextView plotSynopsis;
    @BindView(R.id.userrating)
    TextView userRating;
    @BindView(R.id.releasedate)
    TextView releaseDate;
    @BindView(R.id.thumbnail_image_header)
    ImageView imageView;
    int movie_id;
    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("movies")) {

            Movie movie = getIntent().getParcelableExtra("movies");

            String thumbnail = movie.getPosterPath();
            String movieName = movie.getOriginalTitle();
            String synopsis = movie.getOverview();
            String rating = Double.toString(movie.getVoteAverage());
            String dateOfRelease = movie.getReleaseDate();
            int movie_id = movie.getId();

            String poster = "https://image.tmdb.org/t/p/w185" + thumbnail;

            Glide.with(this)
                    .load(poster)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);
            movie_id = movie.getId();

        } else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();

    }
    private void loadJSON(){

            Client Client = new Client();
            Service apiService =  com.moon.movieapp.api.Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error fetching trailer data",
                            Toast.LENGTH_SHORT).show();

                }
            });

        }
    }


