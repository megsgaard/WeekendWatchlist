package au585303.au590400.weekendwatchlist.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.models.MovieGsonObject;

public class BackgroundService extends Service {
    //Declare variables
    private final IBinder myBinder = new LocalBinder();
    private final static String TAG = "BackgroundService";
    private FirestoreHandler firestoreHandler;
    private APIHandler apiHandler;
    private APIHandler.IApiResponseListener listener;


    //Constructor
    public BackgroundService() {
        firestoreHandler = new FirestoreHandler();
        listener = new APIHandler.IApiResponseListener() {
            @Override
            public void onMovieReady(Movie movie) {
                Log.d(TAG, "onMovieReady Enter");
           }
       };
       apiHandler = new APIHandler(this, listener);

        //apiHandler = new APIHandler(this, new APIHandler.IApiResponseListener() {
        //    @Override
        //    public void onMovieReady(Movie movie) {
        //        firestoreHandler.addMovie(movie);
        //    }
        //});
    }

    //Create a binder object
    public class LocalBinder extends Binder {
        public BackgroundService getService(){
            return BackgroundService.this;
        }
    }

    //OnBind method
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"Activity called binding method");
        return myBinder;
    }


    //Methods connecting to database
    public List<MovieGsonObject> getAll() //TODO: Implement + udskift MovieGsonObject
    {
        return null;
    }

    public void addMovie(String searchWord) //TODO: Implement
    {
//        Log.d(TAG, "addMovie: "+searchWord);
//        apiHandler.addRequest(searchWord);
        Movie movie1 = new Movie();
        movie1.setTitle("Joker");
        movie1.setYear("2019");
        firestoreHandler.addMovie(movie1);
    }

    public MovieGsonObject getMovie() //TODO: Impelemnt + udskift MovieGsonObject
    {
        return null;
    }

    public void updateMovie() //TODO: Implement
    {

    }

    public void deleteMovie() //TODO: Implement
    {

    }
}
