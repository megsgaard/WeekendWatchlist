package au585303.au590400.weekendwatchlist.services;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.models.MovieGsonObject;

import static android.widget.Toast.LENGTH_LONG;

public class APIHandler {
    //Variables
    private static final String TAG = "APIHandler";
    private IApiResponseListener listener;
    private static String API_URL = "http://www.omdbapi.com/?apikey=edeb0a57&t="; //"http://www.omdbapi.com/?i=tt3896198&apikey=edeb0a57";
    private static String API_KEY = "edeb0a57";
    private RequestQueue myRequestQueue;
    private Context context;
    private Movie movieToBeAdded;

    //Constructor
    public APIHandler(Context context, IApiResponseListener listener) {
        this.listener = listener;
        this.context = context;

    }

    //AddRequest to queue method
    public void addRequest(final String searchWord) {
        Log.d(TAG, "addRequest Enter");
        if (myRequestQueue == null) {
            Log.d(TAG, "myRequestQueue was null");
            myRequestQueue = Volley.newRequestQueue(context);
        }

        String url = API_URL + searchWord;

        //Create new JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    Log.d(TAG, "Json response was not null");
                    Movie movie = parseJsonWithGson(response.toString());
                    if (movie != null) {
                        Log.d(TAG, "movie from parsing Json was not null");
                        movieToBeAdded = movie;
                        listener.onMovieReady(movieToBeAdded);
                    } else {
                        Log.d(TAG, "movie from parsing Json was null");
                        movieToBeAdded = null;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Json response error" + error);

                //Created custom toast with inspiration from: https://stackoverflow.com/questions/31175601/how-can-i-change-default-toast-message-color-and-background-color-in-android
                /*Toast toast = Toast.makeText(context.getApplicationContext(), searchWord + " " + context.getText(R.string.word_does_not_exist), LENGTH_LONG);
                View view = toast.getView();

                view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(context.getResources().getColor(R.color.colorAccent));
                text.setTextSize(25);

                toast.show();
*/
            }
        }) {
            /*@Override
            public Map<String, String> getHeaders() {
                String token = API_KEY;

                final Map<String, String> header;
                header = new HashMap<String, String>();
                header.put("Authorization", "Token " + token);
                return header;
            }*/
        };
        myRequestQueue.add(jsonObjectRequest);
    }

    //Method to parse Json
    private Movie parseJsonWithGson(String jsonString) {
        Gson gson = new GsonBuilder().create();
        MovieGsonObject movieGsonObject = gson.fromJson(jsonString, MovieGsonObject.class);

        //Create new Movie based on MovieGsonObject
        Movie movie = new Movie(movieGsonObject.getTitle(), movieGsonObject.getYear(), movieGsonObject.getGenre(), movieGsonObject.getRuntime(), movieGsonObject.getDirector(), movieGsonObject.getWriter(), movieGsonObject.getActors(), movieGsonObject.getPlot(), movieGsonObject.getAwards(), movieGsonObject.getPoster(), movieGsonObject.getImdbRating());
        Log.d(TAG, movie.getPoster());
        return movie;
    }

    //Listener interface
    public interface IApiResponseListener {
        public void onMovieReady(Movie movie);
    }

}
