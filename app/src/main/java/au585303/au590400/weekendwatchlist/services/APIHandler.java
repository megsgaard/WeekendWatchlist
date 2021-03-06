package au585303.au590400.weekendwatchlist.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.activities.ListActivity;
import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.models.MovieGsonObject;

public class APIHandler {
    private static final String TAG = "APIHandler";
    private IApiResponseListener listener;
    private static String API_URL = "http://www.omdbapi.com/?apikey=edeb0a57&t="; //"http://www.omdbapi.com/?i=tt3896198&apikey=edeb0a57";
    private static String API_KEY = "edeb0a57";
    private RequestQueue requestQueue;
    private Context context;
    private Movie movieToBeAdded;

    public APIHandler(Context context, IApiResponseListener listener) {
        this.listener = listener;
        this.context = context;

    }

    // AddRequest to queue method
    public void addRequest(final String searchWord) {
        Log.d(TAG, "addRequest Enter");
        if (requestQueue == null) {
            Log.d(TAG, "myRequestQueue was null");
            requestQueue = Volley.newRequestQueue(context);
        }

        String url = API_URL + searchWord;

        // Create new JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!response.toString().contains("Error")) {
                    Log.d(TAG, "Json response was not null");
                    Movie movie = parseJsonWithGson(response.toString());
                    if (movie != null) {
                        movieToBeAdded = movie;
                        listener.onMovieReady(movieToBeAdded);
                        Log.d(TAG, "movie from parsing Json:" + movie.getTitle());

                    } else {
                        Log.d(TAG, "movie from parsing Json was null");
                        movieToBeAdded = null;
                    }
                }
                else
                {
                    Toast.makeText(context, R.string.api_fail, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Json response error" + error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    // Method to parse Json
    private Movie parseJsonWithGson(String jsonString) {
        Gson gson = new GsonBuilder().create();
        MovieGsonObject movieGsonObject = gson.fromJson(jsonString, MovieGsonObject.class);

        // Create new Movie based on MovieGsonObject
        // TODO: MEG: add null checking
        String imageUrl;
        if (movieGsonObject.getPoster().equals("N/A")) {
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/f/fc/No_picture_available.png";
        } else {
            imageUrl = movieGsonObject.getPoster();
        }
        Movie movie = new Movie(movieGsonObject.getTitle(), movieGsonObject.getYear(), movieGsonObject.getGenre(), movieGsonObject.getRuntime(), movieGsonObject.getDirector(), movieGsonObject.getWriter(), movieGsonObject.getActors(), movieGsonObject.getPlot(), movieGsonObject.getAwards(), imageUrl, movieGsonObject.getImdbRating(), "0", "");
        Log.d(TAG, movie.getPoster());
        return movie;
    }

    // Listener interface
    public interface IApiResponseListener {
        void onMovieReady(Movie movie);
    }

}
