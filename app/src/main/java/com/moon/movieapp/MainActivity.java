package com.moon.movieapp;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moon.movieapp.adapter.MoviesAdapter;
import com.moon.movieapp.api.Client;
import com.moon.movieapp.api.Service;
import com.moon.movieapp.model.Movie;
import com.moon.movieapp.model.MoviesResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        List<Movie> movieList = new ArrayList<>();
        MoviesAdapter adapter = new MoviesAdapter(this, movieList);

        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        swipeContainer = findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.darker_gray);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        loadJSON1();
    }


    private void loadJSON1() {

        Client Client = new Client();
        Service apiService =
                com.moon.movieapp.api.Client.getClient().create(Service.class);
        Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                List<Movie> movies = response.body().getResults();
                Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                recyclerView.smoothScrollToPosition(0);
                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }


            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadJSON2() {

        Client Client = new Client();
        Service apiService =
                com.moon.movieapp.api.Client.getClient().create(Service.class);
        Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                assert response.body() != null;
                List<Movie> movies = response.body().getResults();
                Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                recyclerView.smoothScrollToPosition(0);
                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                loadJSON1();
                return true;
            case R.id.top_rated:
                loadJSON2();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

