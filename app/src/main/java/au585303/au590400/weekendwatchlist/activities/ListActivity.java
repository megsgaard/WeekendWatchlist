package au585303.au590400.weekendwatchlist.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.adapters.ListAdapter;
import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.services.BackgroundService;

import static android.widget.Toast.LENGTH_LONG;

public class ListActivity extends AppCompatActivity implements ListAdapter.OnItemClickListener, ListAdapter.OnItemLongClickListener {
    private static final String TAG = "ListActivity";
    private static final int RC_SIGN_IN = 101;
    public static final String USERS = "users";
    public static final String MOVIES = "movies";
    public static final String SELECTED_GENRE = "SelectedGenre";
    public static final String SELECTED_GENRE_POSITION = "selectedGenrePosition";
    public static final String ALL = "All";
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private ServiceConnection serviceConnection;
    private BackgroundService backgroundService;
    private ArrayList<Movie> movies;
    private int selectedGenrePosition;
    private String selectedGenre;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        checkUserStatus();

        if (savedInstanceState != null) {
            // Doing this to save recycler view position on rotation
            movies = savedInstanceState.getParcelableArrayList(MOVIES);
            selectedGenre = savedInstanceState.getString(SELECTED_GENRE);
            selectedGenrePosition = savedInstanceState.getInt(SELECTED_GENRE_POSITION);
        } else {
            movies = new ArrayList<>();
            selectedGenre = ALL;
        }

        initViews();
        initService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fireStore.collection(USERS).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection(MOVIES).addSnapshotListener(this,
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Movie movie = snapshot.toObject(Movie.class);
                                    if (!movies.contains(movie)) {
                                        movies.add(movie);
                                    }
                                }
                                adapter.setMovies(movies);
                                adapter.getGenreFilter().filter(selectedGenre);
                            }
                        }
                    }
            );
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES, movies);
        outState.putString(SELECTED_GENRE, selectedGenre);
        outState.putInt(SELECTED_GENRE_POSITION, selectedGenrePosition);
    }

    private void checkUserStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If user is not logged in
            launchSignIn();
        } else {
            setActionBar();
        }
    }

    private void launchSignIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void setActionBar() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        getSupportActionBar().setTitle(userEmail);
    }

    private void initViews() {
        //Set widgets
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton buttonAdd = findViewById(R.id.fabAdd);

        //Set up adapter and recyclerview
        adapter = new ListAdapter(movies, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Enter");
                openAddMovieDialog();
            }
        });
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
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    backgroundService.setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
            }
        };
    }

    //Inspiration from: https://developer.android.com/guide/topics/ui/dialogs.html
    private void openAddMovieDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.search_dialog, null);
        alertDialogBuilder.setTitle(R.string.dialog_text);
        alertDialogBuilder.setView(view)
                .setPositiveButton(R.string.add_movie, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText searchWord = view.findViewById(R.id.txtSearchWord);
                        boolean movieAlreadyInList = false;

                        //Check if movie is already in the list
                        for(Movie movie : movies)
                        {
                            String movietitle = movie.getTitle().toLowerCase();
                            String searchtitle = searchWord.getText().toString().toLowerCase();
                            if(movietitle.equals(searchtitle))
                                movieAlreadyInList = true;
                        }

                        //If movie is not already in the list add it
                        if(movieAlreadyInList ==true)
                        {
                            Toast.makeText(ListActivity.this,R.string.movie_already_in_list,LENGTH_LONG).show();
                            Log.d(TAG, "onClick: Movie already in the list");
                        }
                        else
                        {
                            backgroundService.addMovie(searchWord.getText().toString());
                            Log.d(TAG, "onClick: Movie not in the list");
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

    private void openFilterMoviesDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.filter_dialog, null);

        // https://developer.android.com/guide/topics/ui/controls/spinner
        final Spinner spinner = view.findViewById(R.id.genre_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.genres_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(selectedGenrePosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenrePosition = position;
                selectedGenre = parent.getItemAtPosition(position).toString();
                adapter.getGenreFilter().filter(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        alertDialogBuilder.setTitle(R.string.list_filter_dialog_header);
        alertDialogBuilder.setView(view)
                .setPositiveButton(R.string.filter_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.clear_filter_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedGenrePosition = 0;
                        selectedGenre = ALL;
                        adapter.getGenreFilter().filter(ALL);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Login was successful");
                setActionBar();
                backgroundService.setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            } else {
                Log.d(TAG, "Login was aborted");
                launchSignIn();
                Toast.makeText(this, R.string.login_warning_text, LENGTH_LONG).show();
            }
        }
    }

    // Search menu inspired by this video: https://youtu.be/sJ-Z9G0SDhc
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getSearchFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_sortByRating) {
            adapter.sortMoviesByRating();
        }
        if (item.getItemId() == R.id.app_bar_filterByCategory) {
            openFilterMoviesDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Movie movie = movies.get(position);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_extra_movietitle), movie.getTitle());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        final Movie movie = movies.get(position);

        //Create AlertDialog to make safe delete
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
        alertDialogBuilder.setMessage(R.string.delete_question)
                .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backgroundService.deleteMovie(movie.getTitle());
                        movies.remove(movie);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }
}
