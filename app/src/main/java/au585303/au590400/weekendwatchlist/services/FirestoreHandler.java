package au585303.au590400.weekendwatchlist.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import au585303.au590400.weekendwatchlist.models.Movie;

class FirestoreHandler {
    private static final String TAG = "FirestoreHandler";
    private static final String USERS = "users";
    private static final String MOVIES = "movies";
    private FirebaseFirestore db;
    private Movie fetchedMovie;
    private IMovieResponseListener listener;
    private String userEmail;


    FirestoreHandler(IMovieResponseListener listener) {
        db = FirebaseFirestore.getInstance();
        this.listener = listener;
    }

    void updateMovie(Movie movie) {
        String movieId = movie.getTitle().toLowerCase();
        db.collection(USERS).document(userEmail).collection(MOVIES).document(movieId).set(movie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Document has been updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document was not updated!", e);
            }
        });

    }

    void addMovie(Movie movie) {
        // Save movie in Firestore
        String movieId = movie.getTitle().toLowerCase(); // Title of the movie will be the id of the document in Firestore. Since the app doesn't support multiple movies with the same title, this is okay for now.
        db.collection(USERS).document(userEmail).collection(MOVIES).document(movieId).set(movie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Document has been saved!");
                listener.onMovieAdded();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document was not saved!", e);
            }
        });
    }

    void shareMovie(Movie movie, String shareEmail) {
        String movieId = movie.getTitle().toLowerCase();
        db.collection(USERS).document(shareEmail).collection(MOVIES).document(movieId).set(movie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Movie has been shared");
                listener.onMovieShared();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Could not share movie");
            }
        });
    }

    //https://firebase.google.com/docs/firestore/query-data/get-data#java
    void getMovie(String movieId) {
        db.collection(USERS).document(userEmail).collection(MOVIES).document(movieId.toLowerCase()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    fetchedMovie = documentSnapshot.toObject(Movie.class);
                    Log.d(TAG, "onSuccess: Movie fetched " + fetchedMovie.getTitle());
                    listener.onMovieReady(fetchedMovie);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e);
            }
        });
    }

    //https://firebase.google.com/docs/firestore/manage-data/delete-data
    void deleteMovie(String movieId) {
        String movie = movieId.toLowerCase();
        db.collection(USERS).document(userEmail).collection(MOVIES).document(movie).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error deleting document", e);
            }
        });
    }

    void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // Listener interface
    public interface IMovieResponseListener {
        void onMovieReady(Movie movie);

        void onMovieShared();

        void onMovieAdded();
    }
}
