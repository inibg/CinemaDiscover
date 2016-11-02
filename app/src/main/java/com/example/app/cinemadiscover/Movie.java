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
    private long mId;

    public Movie(long id, String moviePosterPath, String movieName){
        String BASE_MOVIE_URL = "https://image.tmdb.org/t/p/w500";
        mMoviePosterPath = BASE_MOVIE_URL + moviePosterPath;
        mId = id;
        mName = movieName;
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
}
