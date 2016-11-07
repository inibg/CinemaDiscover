package com.example.app.cinemadiscover;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie selectedMovie;
    private String posterURL;
    private ScrollView mScrollView;
    private ImageView mMoviePoster;
    private FrameLayout mWrapperFL;
    private ImageLoader imgLoader = ImageLoader.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        posterURL = (String) getIntent().getExtras().get("moviePoster");

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mMoviePoster = (ImageView) findViewById(R.id.moviePoster);
        mWrapperFL = (FrameLayout) findViewById(R.id.flWrapper);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());

        imgLoader.DisplayImage(posterURL, mMoviePoster);

        TextView textTitle = (TextView) mScrollView.findViewById(R.id.movieTitle);
        textTitle.setText(selectedMovie.getName());

        TextView textSynopsis = (TextView) mScrollView.findViewById(R.id.synopsis);
        textSynopsis.setText(selectedMovie.getSynopsis());

        RatingBar rating = (RatingBar) mScrollView.findViewById(R.id.ratingBar);
        Toast.makeText(this, "rating: " + selectedMovie.getRating().toString(), Toast.LENGTH_SHORT).show();
        rating.setRating(Float.parseFloat(selectedMovie.getRating().toString()));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
     //   Toast.makeText(this, selectedMovie.getName(), Toast.LENGTH_SHORT).show();
        super.onPostCreate(savedInstanceState);

    }

    private class ScrollPositionObserver implements ViewTreeObserver.OnScrollChangedListener {

        private int mImageViewHeight;

        public ScrollPositionObserver() {
            mImageViewHeight = getResources().getDimensionPixelSize(R.dimen.poster_photo_height);
        }

        @Override
        public void onScrollChanged() {
            int scrollY = Math.min(Math.max(mScrollView.getScrollY(), 0), mImageViewHeight);

            // changing position of ImageView
            mMoviePoster.setTranslationY(scrollY/8);

            // alpha you could set to ActionBar background
            float alpha = scrollY / (float) mImageViewHeight;
            //getActionBar().setStackedBackgroundDrawable(new ColorDrawable());
        }
    }
}
