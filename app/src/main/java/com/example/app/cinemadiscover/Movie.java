package com.example.app.cinemadiscover;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by iberretta on 10/29/16.
 */

@SuppressWarnings("serial")
public class Movie implements Serializable{

    private String mMoviePosterPath;
    private String mName;
    private String mSynopsis;
    private long mId;
    private long mRating;

    public Movie(long id, String moviePosterPath, String movieName, String movieSynopsis,
                 long movieRating){
        String BASE_MOVIE_URL = "https://image.tmdb.org/t/p/original";
        mMoviePosterPath = BASE_MOVIE_URL + moviePosterPath;
        mId = id;
        mName = movieName;
        mSynopsis = movieSynopsis;
        mRating = movieRating;
    }

    public String getName(){
        return this.mName;
    }

    public Uri getImage(){
        return Uri.parse(mMoviePosterPath).buildUpon().build();
    }

    public Long getId() {
        return mId;
    }

    public String getSynopsis(){
        return mSynopsis;
    }

    public Long getRating(){
        return mRating;
    }
}
