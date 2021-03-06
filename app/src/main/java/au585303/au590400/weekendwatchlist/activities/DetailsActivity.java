package au585303.au590400.weekendwatchlist.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.services.BackgroundService;

public class DetailsActivity extends AppCompatActivity {
    private ServiceConnection serviceConnection;
    private BackgroundService backgroundService;
    private static final String TAG = "DetailsActivity";
    private String movieTitleFromIntent;
    private Movie movie;
    private View contentView;
    private float ratingState;
    private String notesState;

    //Widgets
    TextView title;
    TextView year;
    TextView imdbRating;
    TextView movieLength;
    TextView plot;
    TextView genre;
    TextView director;
    TextView writer;
    TextView actors;
    TextView awards;
    ImageView poster;
    RatingBar ratingBar;
    EditText personalNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        contentView = findViewById(R.id.details_content);
        contentView.setVisibility(View.GONE); // Hide the scroll view while loading
        initService();
        setActionBar();

        //Set widgets
        title = findViewById(R.id.txtTitle);
        year = findViewById(R.id.txtYear);
        imdbRating = findViewById(R.id.txtIMdBRating);
        movieLength = findViewById(R.id.txtMovieLength);
        plot = findViewById(R.id.txtPlot);
        genre = findViewById(R.id.txtGenre);
        director = findViewById(R.id.txtDirector);
        writer = findViewById(R.id.txtWriter);
        poster = findViewById(R.id.ivPoster);
        actors = findViewById(R.id.txtActors);
        awards = findViewById(R.id.txtAwards);

        //Get intent
        Intent intent = getIntent();
        movieTitleFromIntent = intent.getStringExtra(getResources().getString(R.string.intent_extra_movietitle));

        //Register broadcast receiver
        registerReciever();

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ratingBar = findViewById(R.id.ratingBar);
        personalNotes = findViewById(R.id.txtPersonalNotes);

        Button btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movie.setPersonalRating(Float.toString(ratingBar.getRating()));
                movie.setPersonalNotes(personalNotes.getText().toString());
                backgroundService.updateMovie(movie);
                finish();
            }
        });

        //Retrieve data from savedInstance
        if (savedInstanceState != null) {
            ratingState = savedInstanceState.getFloat("Rating");
            notesState = savedInstanceState.getString("Notes");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
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
                Log.d(TAG, "onServiceConnected: Movie:" + movieTitleFromIntent);

                //Set values of widgets according to intent
                backgroundService.getMovie(movieTitleFromIntent);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
            }
        };
    }

    private void registerReciever() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_MOVIE_READY);
        LocalBroadcastManager.getInstance(this).registerReceiver(onServiceMovieReady, filter);
    }

    private void setActionBar() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        getSupportActionBar().setTitle(userEmail);
    }

    private void openShareDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.share_dialog, null);
        alertDialogBuilder.setTitle(R.string.details_share_dialog_header);
        alertDialogBuilder.setView(view)
                .setPositiveButton(R.string.share_movie, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText txtEmail = view.findViewById(R.id.txtShareEmail);
                        String shareEmail = txtEmail.getText().toString();
                        if (!shareEmail.isEmpty()) {
                            backgroundService.shareMovie(movie, shareEmail);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Since there's only one icon in the menu at this point, it's not necessary to check the item id.
        openShareDialog();
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver onServiceMovieReady = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            movie = backgroundService.getReadyMovie();

            //Set widgets according to movie
            title.setText(movie.getTitle());
            year.setText(movie.getYear());
            imdbRating.setText(movie.getImdbRating());
            movieLength.setText(movie.getRuntime());
            plot.setText(movie.getPlot());
            genre.setText(movie.getGenre());
            director.setText(movie.getDirector());
            writer.setText(movie.getWriter());
            Picasso.get().load(movie.getPoster())
                    .resize(0, 600)
                    .into(poster);

            if (notesState != null) {
                personalNotes.setText(notesState);
            } else {
                personalNotes.setText(movie.getPersonalNotes());
            }
            if (ratingState != 0.0) {
                ratingBar.setRating(ratingState);
            } else {
                ratingBar.setRating(Float.parseFloat(movie.getPersonalRating()));
            }

            contentView.setVisibility(View.VISIBLE); // Show scroll view once the movie is loaded.
            actors.setText(movie.getActors());
            awards.setText(movie.getAwards());
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("Rating", ratingBar.getRating());
        outState.putString("Notes", personalNotes.getText().toString());
    }
}
