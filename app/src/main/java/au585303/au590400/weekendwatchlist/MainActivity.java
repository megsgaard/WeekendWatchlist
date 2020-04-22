package au585303.au590400.weekendwatchlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1111111;
    private static final String LOG = "MainActivity";
    private boolean userLoggedIn =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG,"onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If user is already logged in: //TODO: Ved ikke om dette er nødvendigt?
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Log.d(LOG,"RequestCode correct");


            if (resultCode == RESULT_OK) {
                Log.d(LOG,"Result code OK");
                // Successfully signed in
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userLoggedIn=true;
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG,"onResume called");
    }
}
