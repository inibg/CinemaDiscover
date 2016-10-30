package com.example.app.cinemadiscover;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridMoviesAdapter mGridAdapter;
    private List<Movie> mMovies = new ArrayList<>();
    private String[] mGridData;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_main, container, false);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity myActivity = getActivity();

        mGridView = (GridView) getView().findViewById(R.id.grid_poster_view);

        FetchMoviesTask fmt = new FetchMoviesTask(getActivity());
        fmt.execute(getString(R.string.order_top_rated));
        mGridData = mMovies.toArray(new String[mMovies.size()]);
        mGridAdapter = new GridMoviesAdapter(getActivity(), mGridData);
        mGridView.setAdapter(mGridAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Void> {

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
            List<Movie> movies = null;
            try {
                movies = mp.parse();
            }catch (JSONException e){
                e.printStackTrace();
            }
            Log.v(LOG_TAG, " " + movies.size());
            mMovies = movies;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadGridData();
            mGridAdapter.updateData(mGridData);
            mGridAdapter.notifyDataSetChanged();
        }

        private void loadGridData(){
            Iterator<Movie> it = mMovies.iterator();
            List<String> moviePostersURL = new ArrayList<>();
            while(it.hasNext()){
                moviePostersURL.add(it.next().getImage().toString());
            }
            mGridData = moviePostersURL.toArray(new String[moviePostersURL.size()]);
        }
    }
}
