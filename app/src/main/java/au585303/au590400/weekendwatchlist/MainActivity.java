package au585303.au590400.weekendwatchlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 11;
    private static final String TAG = "MainActivity";
    private boolean userLoggedIn = false;
    private TextView txt;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.textView);
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
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Map<String, Object> dataToSave = new HashMap<>();
                dataToSave.put("text", "This has been updated by: " + userEmail);
                final DocumentReference documentReference = db.document("users/test2@mail.com");
                documentReference.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Document has been saved!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: Document was not saved!", e);
                    }
                });
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

                // Dummy code to test read and write

                // Write data
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Map<String, Object> dataToSave = new HashMap<>();
                dataToSave.put("text", "This was written by: " + userEmail);
                final DocumentReference documentReference = db.document("users/" + userEmail);
                documentReference.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Document has been saved!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: Document was not saved!", e);
                    }
                });

                // Read data
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            String testString = documentSnapshot.getString("text");
                            txt.setText(testString);
                        }
                    }
                });

                userLoggedIn = true;
                //If user is already logged in:
//                Intent intent = new Intent(MainActivity.this,ListActivity.class);
//                startActivity(intent);

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
