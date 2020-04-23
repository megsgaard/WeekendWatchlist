package au585303.au590400.weekendwatchlist.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.services.BackgroundService;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 11;
    private static final String TAG = "MainActivity";
    private boolean userLoggedIn = false;
    private TextView txt;
    private EditText shareTxt;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ServiceConnection serviceConnection;
    private BackgroundService backgroundService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();
        txt = findViewById(R.id.textView);
        shareTxt = findViewById(R.id.txtShareEmail);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            txt.setText("User logged in: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
            Intent intent = new Intent(MainActivity.this,ListActivity.class);
            startActivity(intent);
        } else {
            txt.setText("No user logged in");
        }

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If user is already logged in: //TODO: Ved ikke om dette er nødvendigt?
                //Intent intent = new Intent(MainActivity.this,ListActivity.class);
                //startActivity(intent);

                //If user is not logged in:
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
        });
        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String sharedEmail = shareTxt.getText().toString();
//                shareText(sharedEmail);
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
                backgroundService = ((BackgroundService.LocalBinder)service).getService();
//                backgroundService.addMovie(); this will crash if the user isn't logged in. tested that it works. commented out to not break on first startup
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundService = null;
            }
        };
    }


    // TODO: MEG: Move or remove this block of test code
    private void shareText(String shareEmail) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Do this check before being to share movie to make sure the user exists. Otherwise it will create a new user in the database.
        DocumentReference docRef = db.collection("users").document(shareEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("text", "This has been shared by: " + userEmail);
        db.collection("users/" + shareEmail + "/movies").add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "onSuccess: Document has been saved!");
                // display toast in here that tell movie has been shared
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document was not saved!", e);
            }
        });
    }

    // TODO: MEG: Move this code to FirestoreService
    private void readData() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final DocumentReference documentReference = db.document("users/" + userEmail);

        // Read data
        db.collection("users/" + userEmail + "/movies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                    List<String> items = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        String testString = snapshot.getData().get("text").toString();
                        txt.setText(testString);
                    }
                }
            }
        });

        // Check if user is logged in
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    txt.setText("User logged in: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
            }
        });
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

                userLoggedIn = true;
                //If user is already logged in:
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                userLoggedIn = false;
            }
        }
    }
}
