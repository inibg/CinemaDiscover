package com.example.app.cinemadiscover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
    private GridMoviesAdapter mGridAdapter;
    private List<Movie> mMovies;
    private String[] mGridData;
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        inflater.inflate(R.layout.fragment_main, container, false);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void updateMovies(String pageNumber){
        if (mGridView == null)
            mGridView = (GridView) getView().findViewById(R.id.grid_poster_view);

        FetchMoviesTask fmt = new FetchMoviesTask(getActivity());
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        fmt.execute(preferences.getString(getString(R.string.pref_order_key),
                getString(R.string.pref_order_default_order)), pageNumber);
        if (mMovies == null || pageNumber.compareTo("1") == 0)
            mMovies = new ArrayList<>();
        mGridData = getMoviePosters();
        mGridAdapter = new GridMoviesAdapter(getActivity(), mGridData);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnScrollListener(new EndlessScrollListener(10));
        mGridView.setOnItemClickListener(new GridOnClickListener());
    }

    private String[] getMoviePosters(){
        String[] posters = new String[mMovies.size()];
        int i = 0;
        for (Movie m: mMovies){
            posters[i] = m.getImage().toString();
            i++;
        }
        return posters;
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        updateMovies("1");
        super.onResume();
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Void> {

        private Context context;
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        public FetchMoviesTask(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params.length < 2)
                return null;
            String order = params[0];
            String page = params[1];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                String BASE_URL = "https://api.themoviedb.org/3/movie/";
                String SERVICE;
                String top_rated = context.getString(R.string.order_top_rated);
                if (order.compareTo(top_rated) == 0){
                    SERVICE = "top_rated?";
                }else{
                    SERVICE = "popular?";
                }
                String KEY_PARAM = "api_key";
                String LANGUAGE_PARAM = "language";
                String PAGE_PARAM = "page";

                Uri queryUri = Uri.parse(BASE_URL + SERVICE).buildUpon()
                        .appendQueryParameter(KEY_PARAM, BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                        .appendQueryParameter(PAGE_PARAM, page)
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
            if (mMovies.size() == 0) {
                mMovies = movies;
            }else{
                Iterator<Movie> it = movies.iterator();
                while(it.hasNext()){
                    mMovies.add(it.next());
                }
            }
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
            if (mMovies == null)
                return;
            Iterator<Movie> it = mMovies.iterator();
            List<String> moviePostersURL = new ArrayList<>();
            while(it.hasNext()){
                moviePostersURL.add(it.next().getImage().toString());
            }
            mGridData = Utils.concatStringsArrays(new String[0], moviePostersURL.toArray(new String[moviePostersURL.size()]));

        }


    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                Long page =  new Long(currentPage + 1);
                updateMovies(page.toString());
                loading = true;
            }
        }

    }

    private class GridOnClickListener implements  AbsListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent detailIntent = new Intent(getActivity(),MovieDetailActivity.class);
            Movie selectedMovie = mMovies.get(i);
            detailIntent.putExtra("moviePoster", mGridData[i]);
            detailIntent.putExtra("selectedMovie", selectedMovie);
            startActivity(detailIntent);
        }
    }
}
