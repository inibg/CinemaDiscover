package com.example.app.cinemadiscover;

/**
 * Created by iberretta on 10/29/16.
 */

public class Movie {

    private String mMoviePath;
    private long mId;

    public Movie(long id, String moviePath){
        mMoviePath = moviePath;
        mId = id;
    }
}
