package au585303.au590400.weekendwatchlist.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import au585303.au590400.weekendwatchlist.models.Movie;

class FirestoreHandler {
    private static final String TAG = "FirestoreHandler";
    private static final String USERS = "users";
    private static final String MOVIES = "movies";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    FirestoreHandler() {
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    void addMovie(Movie movie, String userEmail) {
        // Save movie in Firestore
        String movieId = movie.getTitle().toLowerCase(); // Title of the movie will be the id of the document in Firestore. Since the app doesn't support multiple movies with the same title, this is okay for now.
        db.collection(USERS).document(userEmail).collection(MOVIES).document(movieId).set(movie).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }
}
