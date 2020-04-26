package au585303.au590400.weekendwatchlist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.services.BackgroundService;

public class DetailsActivity extends AppCompatActivity {
    private ServiceConnection serviceConnection;
    private BackgroundService backgroundService;
    private static final String TAG = "DetailsActivity";
    private String movieTitleFromIntent;

    //Widgets
    TextView title;
    TextView year;
    TextView imdbRating;
    TextView movieLength;
    TextView plot;
    TextView genre;
    TextView director;
    TextView writer;
    ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initService();

        //Set widgets
        title = findViewById(R.id.txtTitle);
        year = findViewById(R.id.txtYear);
        imdbRating = findViewById(R.id.tvIMdBRating);
        movieLength = findViewById(R.id.tvMovieLength);
        plot = findViewById(R.id.tvPlot);
        genre = findViewById(R.id.tvGenre);
        director = findViewById(R.id.tvDirector);
        writer = findViewById(R.id.tvWriter);
        poster = findViewById(R.id.ivPoster);

        //Get intent
        Intent intent = getIntent();
        movieTitleFromIntent = intent.getStringExtra(getResources().getString(R.string.intent_extra_movietitle));

        //Register broadcast reciever
        registerReciever();
    }

    private void initService() {
        Log.d(TAG, "initService: setting up connection, starting and binding to service");
        setupServiceConnection();
        bindService(new Intent(this, BackgroundService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                backgroundService = ((BackgroundService.LocalBinder) service).getService();

                //Set values of widgets accordning to intent
                Movie movie = backgroundService.getMovie(movieTitleFromIntent);
                Log.d(TAG, "onServiceConnected: Movie:"+movieTitleFromIntent);
                //title.setText(movie.getTitle());

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
            }
        };
    }

    private void registerReciever()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_MOVIE_READY);
        LocalBroadcastManager.getInstance(this).registerReceiver(onServiceMovieReady,filter);
    }

    private BroadcastReceiver onServiceMovieReady =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Movie movie = backgroundService.getReadyMovie();

            //Set widgets accordning to movie
            title.setText(movie.getTitle());
            year.setText(movie.getYear());
            imdbRating.setText(movie.getImdbRating());
            movieLength.setText(movie.getRuntime());
            plot.setText(movie.getPlot());
            genre.setText(movie.getGenre());
            director.setText(movie.getDirector());
            writer.setText(movie.getWriter());
            Picasso.get().load(movie.getPoster()).into(poster);
        }
    };
}
