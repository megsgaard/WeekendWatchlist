package au585303.au590400.weekendwatchlist.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import au585303.au590400.weekendwatchlist.models.MovieGsonObject;

public class BackgroundService extends Service {
    //Declare variables
    private final IBinder myBinder = new LocalBinder();
    private final static String LOG = "BackgroundService";

    //Constructor
    public BackgroundService() {
    }

    //Create a binder object
    public class LocalBinder extends Binder {
        BackgroundService getService(){
            return BackgroundService.this;
        }
    }

    //OnBind method
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG,"Activity called binding method");
        return myBinder;
    }


    //Methods connecting to database
    public List<MovieGsonObject> getAll() //TODO: Implement + udskift MovieGsonObject
    {
        return null;
    }

    public void addMovie() //TODO: Implement
    {

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
