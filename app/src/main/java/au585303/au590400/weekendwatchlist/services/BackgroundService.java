package au585303.au590400.weekendwatchlist.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

import au585303.au590400.weekendwatchlist.models.Movie;

import static android.widget.Toast.LENGTH_LONG;

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
        Log.d(TAG, "BackgroundService: ");
        apiResponseListener = new APIHandler.IApiResponseListener() {
            @Override
            public void onMovieReady(Movie movie) {
                Log.d(TAG, "onMovieReady Enter: adding movie to Firestore");
                firestoreHandler.addMovie(movie);
            }
        };
        movieResponseListener = new FirestoreHandler.IMovieResponseListener() {
            @Override
            public void onMovieReady(Movie movie) {
                Log.d(TAG, "onMovieReady: "+ movie.getTitle());
                broadcastMovieReady();
                fetchedMovie = movie;
            }

            @Override
            public void onMovieShared() {
                showMovieShared();
            }

            @Override
            public void onMovieAdded() {
                showMovieAdded();
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

    public void addMovie(String searchWord) //TODO: Implement
    {
        Log.d(TAG, "addMovie: " + searchWord);
        apiHandler.addRequest(searchWord);
    }

    public void shareMovie(Movie movie, String shareEmail) {
        Log.d(TAG, "shareMovie: Trying to share movie: " + movie.getTitle() + "with user: " + shareEmail);
        firestoreHandler.shareMovie(movie, shareEmail);
    }

    public void getMovie(String movieId)
    {
        firestoreHandler.getMovie(movieId);
        Log.d(TAG, "getMovie: called");
    }

    public Movie getReadyMovie()
    {
        return fetchedMovie;
    }

    public void updateMovie() //TODO: Implement
    {

    }

    public void deleteMovie(String movieId) //TODO: Implement
    {
        Log.d(TAG, "deleteMovie: with title: " + movieId);
        firestoreHandler.deleteMovie(movieId);
    }

    public void setUserEmail(String userEmail) {
        firestoreHandler.setUserEmail(userEmail);
    }

    private void broadcastMovieReady()
    {
        Log.d(TAG, "broadcastMovieReady: Enter");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_MOVIE_READY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void showMovieShared() {
        Toast.makeText(this, "Movie has been shared!", LENGTH_LONG).show();
    }

    private void showMovieAdded() {
        Toast.makeText(this, "Movie has been added to your list!", Toast.LENGTH_SHORT).show();
    }
}
