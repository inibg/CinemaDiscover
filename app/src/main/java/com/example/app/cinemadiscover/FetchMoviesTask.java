package com.example.app.cinemadiscover;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by iberretta on 10/29/16.
 */

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    public FetchMoviesTask(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0)
            return null;
        String order = params[0];
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            String BASE_URL = "https://api.themoviedb.org/3/movie/";
            String SERVICE;
            if (order == context.getString(R.string.order_top_rated)){
                SERVICE = "top_rated?";
            }else{
                SERVICE = "popular?";
            }
            String KEY_PARAM = "api_key";
            String LANGUAGE_PARAM = "language";

            Uri queryUri = Uri.parse(BASE_URL + SERVICE).buildUpon()
                    .appendQueryParameter(KEY_PARAM, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                    .build();
            URL url = new URL(queryUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

        }catch (IOException e){
            Log.e(LOG_TAG, "Error connecting to TMDB", e);
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        MovieParser mp = new MovieParser(moviesJsonStr);
        mp.parse();
        return null;
    }
}
