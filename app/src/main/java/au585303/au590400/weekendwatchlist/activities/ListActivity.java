package au585303.au590400.weekendwatchlist.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.ListenerRegistration;
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
import au585303.au590400.weekendwatchlist.services.APIHandler;
import au585303.au590400.weekendwatchlist.services.BackgroundService;

import static android.widget.Toast.LENGTH_LONG;

public class ListActivity extends AppCompatActivity implements ListAdapter.OnItemClickListener {
    private static final String TAG = "ListActivity";
    private static final int RC_SIGN_IN = 101;
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private ListenerRegistration itemsListener;
    private ServiceConnection serviceConnection;
    private BackgroundService backgroundService;
    private List<Movie> movies = new ArrayList<>();

    //widgets
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FloatingActionButton buttonAdd;

    //TODO: FHJ: Læg disse i den klasse, hvor det skal foregå
//    private APIHandler.IApiResponseListener listener;
//    private APIHandler apiHandler;




    //Constructor //TODO: FHJ: Fjern denne? Når indholdet er flyttet derhen hvor det skal være
//    public ListActivity() {
//        //Inspiration for creating a response listener is found here: https://guides.codepath.com/android/Creating-Custom-Listeners
//        listener = new APIHandler.IApiResponseListener() {
//            @Override
//            public void onMovieReady(Movie movie) {
//                Log.d(TAG, "onMovieReady Enter");
//            }
//        };
//        apiHandler = new APIHandler(this, listener);
//    }

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


        //List af film til test af adapter //TODO: Slet når færdig

        Movie movie1 = new Movie();
        movie1.setTitle("Joker");
        movie1.setYear("2019");
        Movie movie2 = new Movie();
        movie2.setTitle("FRIDA");
        movie2.setYear("2019");
        Movie movie3 = new Movie();
        movie3.setTitle("Mathias");
        movie3.setYear("2019");
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);

        //Set up adapter and recyclerview
        adapter = new ListAdapter(movies, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Test af button_add //TODO: FHJ: Lav denne som den rigtigt skal være
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundService.addMovie();
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
        itemsListener = fireStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("movies").addSnapshotListener(this,
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                // TODO: MEG: Update this logic
                                Movie test = snapshot.toObject(Movie.class);
                                movies.add(test);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        itemsListener.remove();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }
}
