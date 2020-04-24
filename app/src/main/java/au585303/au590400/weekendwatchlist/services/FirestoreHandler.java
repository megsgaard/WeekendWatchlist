package au585303.au590400.weekendwatchlist.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHandler {
    private static final String TAG = "FirestoreHandler";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    public FirestoreHandler() {
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void addMovie() {
        // Write data
        String userEmail = firebaseUser.getEmail();
        final Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("text2", "New movie added by: " + userEmail);
        db.collection("users/" + userEmail + "/movies").add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: Document has been saved!" + dataToSave);
                // display toast in here that tell movie has been shared
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document was not saved!", e);
            }
        });

    }
}
