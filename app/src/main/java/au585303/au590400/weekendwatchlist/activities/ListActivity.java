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
import android.widget.EditText;
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

public class ListActivity extends AppCompatActivity implements ListAdapter.OnItemClickListener, ListAdapter.OnItemLongClickListener  {
    private static final String TAG = "ListActivity";
    private static final int RC_SIGN_IN = 101;
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private ServiceConnection serviceConnection;
    private BackgroundService backgroundService;
    private List<Movie> movies = new ArrayList<>();

    //widgets
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private FloatingActionButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        initService();
        setContentView(R.layout.activity_list);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If user is not logged in
            launchSignIn();
        } else {
            setActionBar();
        }

        //Set widgets
        recyclerView = findViewById(R.id.recyclerView);
        buttonAdd = findViewById(R.id.fabAdd);

        //Set up adapter and recyclerview
        adapter = new ListAdapter(movies, this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Enter");
                OpenAlertDialog();
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
//                backgroundService.addMovie(); this will crash if the user isn't logged in. tested that it works. commented out to not break on first startup
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
            }
        };
    }

    private void testCode() {
        //Kode direkte kopieret fra hans demo
        Map<String, Object> item = new HashMap<>();
        item.put("text", new Date().toString());

        Task<DocumentReference> items = fireStore.collection("Items").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Added " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });
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

    //Inspiration from: https://developer.android.com/guide/topics/ui/dialogs.html
    private void OpenAlertDialog()
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.search_dialog,null);
        alertDialogBuilder.setView(view)
                .setPositiveButton(R.string.add_movie, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText searchWord = view.findViewById(R.id.etSearchWord);
                        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        backgroundService.addMovie(searchWord.getText().toString(), userEmail); //TODO: FHJ: Tror ikke emailen er nødvendig
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialogBuilder.show();
        //backgroundService.addMovie("Joker");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Log.d(TAG, "RequestCode correct");
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Result code OK");
                // Successfully signed in
                // Change appbar title to username
                setActionBar();
            } else {
                Log.d(TAG, "Login was aborted");
                launchSignIn();
                Toast.makeText(this, "You must login or sign up to use the app", LENGTH_LONG).show();

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    // TODO: MEG: Maybe this should be moved to onStart() instead?
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fireStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("movies").addSnapshotListener(this,
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                                /*List<Movie>*/ movies = new ArrayList<>();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Movie movie = snapshot.toObject(Movie.class);
                                    movies.add(movie);
                                }
                                adapter.setMovies(movies);
                            }
                        }
                    }
            );
        }
    }

    // Search menu inspired by this video: https://youtu.be/sJ-Z9G0SDhc
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        adapter.sortMoviesByRating();
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
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
        //backgroundService.deleteMovie(movie.getTitle());
    }
}
