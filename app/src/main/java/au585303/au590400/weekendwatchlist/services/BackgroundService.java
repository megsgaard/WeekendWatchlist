package au585303.au590400.weekendwatchlist.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.models.MovieGsonObject;

public class BackgroundService extends Service {
    // Declare variables
    private final IBinder myBinder = new LocalBinder();
    private final static String TAG = "BackgroundService";
    public final static String BROADCAST_MOVIE_READY = "au585303.au590400.weekendwatchlist.BROADCAST_MOVIE_READY";
    private FirestoreHandler firestoreHandler;
    private APIHandler apiHandler;
    private APIHandler.IApiResponseListener apiResponseListener;
    private FirestoreHandler.IMovieResponseListener movieResponseListener;
    private Movie fetchedMovie;


    // Constructor
    public BackgroundService() {
        apiResponseListener = new APIHandler.IApiResponseListener() {
            @Override
            public void onMovieReady(Movie movie, String userEmail) {
                Log.d(TAG, "onMovieReady Enter: adding movie to Firestore");
                firestoreHandler.addMovie(movie/*, userEmail*/);
            }
        };
        movieResponseListener = new FirestoreHandler.IMovieResponseListener() {
            @Override
            public void onMovieReady(Movie movie) {
                Log.d(TAG, "onMovieReady: "+ movie.getTitle());
                broadcastMovieReady();
                fetchedMovie = movie;
            }
        };

        apiHandler = new APIHandler(this, apiResponseListener);
        firestoreHandler = new FirestoreHandler(movieResponseListener);

    }

    // Create a binder object
    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    // OnBind method
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Activity called binding method");
        return myBinder;
    }


    // Methods connecting to database
    public List<Movie> getAll() //TODO: Implement
    {
        return null;
    }

    public void addMovie(String searchWord, String userEmail) //TODO: Implement
    {
        Log.d(TAG, "addMovie: " + searchWord);
        apiHandler.addRequest(searchWord, userEmail);
    }

    public Movie getMovie(String movieId)
    {
        firestoreHandler.getMovie(movieId);
        Log.d(TAG, "getMovie: called");
        return null;
    }

    public Movie getReadyMovie()
    {
        return fetchedMovie;
    }

    private void broadcastMovieReady()
    {
        Log.d(TAG, "broadcastMovieReady: Enter");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_MOVIE_READY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    public void updateMovie() //TODO: Implement
    {

    }

    public void deleteMovie(String movie) //TODO: Implement
    {
        firestoreHandler.deleteMovie(movie);
    }
}
