package com.example.app.cinemadiscover;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iberretta on 10/29/16.
 * This class is created in order to parse the data received from the movie database
 */

public class MovieParser {
    private final String LOG_TAG = MovieParser.class.getSimpleName();
    private String originalJsonData;

    public MovieParser(String jsonData){
        originalJsonData = jsonData;
    }

    public List<Movie> parse() throws JSONException{
        final String TMDB_MOVIE_LIST = "results";
        final String TMDB_MOVIE_POSTER = "poster_path";
        final String TMDB_MOVIE_ID = "id";
        List<Movie> returnMovies = new ArrayList<>();
        JSONObject movies = new JSONObject(originalJsonData);
        JSONArray movies_list = movies.getJSONArray(TMDB_MOVIE_LIST);

        for(int i = 0; i < movies_list.length(); i++){
            Movie movieObject;
            JSONObject movie = movies_list.getJSONObject(i);
            String poster_path =  movie.getString(TMDB_MOVIE_POSTER);
            long id          = movie.getLong(TMDB_MOVIE_ID);
            movieObject = new Movie(id,poster_path);
            returnMovies.add(movieObject);
        }

        return returnMovies;
    }
}
