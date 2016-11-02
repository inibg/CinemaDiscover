package com.example.app.cinemadiscover;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie selectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Toast.makeText(this, selectedMovie.getName(), Toast.LENGTH_SHORT).show();
        super.onPostCreate(savedInstanceState);

    }
}
