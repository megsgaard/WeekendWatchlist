package au585303.au590400.weekendwatchlist.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import au585303.au590400.weekendwatchlist.models.Movie;

class FirestoreHandler {
    private static final String TAG = "FirestoreHandler";
    private static final String USERS = "users";
    private static final String MOVIES = "movies";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private Movie fetchedMovie;
    private IMovieResponseListener listener;


    FirestoreHandler(IMovieResponseListener listener) {
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        collectionReference = db.collection(USERS).document(firebaseUser.getEmail()).collection(MOVIES);
        this.listener = listener;
    }

    void addMovie(Movie movie /*, String userEmail*/) {
        // Save movie in Firestore
        String movieId = movie.getTitle().toLowerCase(); // Title of the movie will be the id of the document in Firestore. Since the app doesn't support multiple movies with the same title, this is okay for now.
        collectionReference.document(movieId).set(movie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Document has been saved!");
                // Could show a toast here or something else, but the list should automatically update so might be redundant.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document was not saved!", e);
            }
        });

        //Log.d(TAG, "addMovie: "+firebaseUser.getEmail() + "vs " +userEmail);
    }

    void getMovie(String movieId){
        String movie = movieId.toLowerCase();
        collectionReference.document(movie).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    fetchedMovie = documentSnapshot.toObject(Movie.class);
                    Log.d(TAG, "onSuccess: Movie fetched " + fetchedMovie.getTitle());
                    listener.onMovieReady(fetchedMovie);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e);
            }
        });

        //return fetchedMovie;
    }

    // Listener interface
    public interface IMovieResponseListener {
        void onMovieReady(Movie movie);
    }
}
