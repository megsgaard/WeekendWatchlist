package au585303.au590400.weekendwatchlist.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au585303.au590400.weekendwatchlist.adapters.ListAdapter;
import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.models.Movie;
import au585303.au590400.weekendwatchlist.models.MovieGsonObject;
import au585303.au590400.weekendwatchlist.services.APIHandler;

import static android.widget.Toast.LENGTH_LONG;

public class ListActivity extends AppCompatActivity implements ListAdapter.OnItemClickListener {

    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private ListenerRegistration itemsListener;
    private String LOG = "ListActivity";

    //TODO: FHJ: Læg disse i den klasse, hvor det skal foregå
    private APIHandler.IApiResponseListener listener;
    private APIHandler apiHandler;


    //widgets
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    FloatingActionButton buttonAdd;

    //Constructor //TODO: FHJ: Fjern denne? Når indholdet er flyttet derhen hvor det skal være
    public ListActivity(){
        //Inspiration for creating a response listener is found here: https://guides.codepath.com/android/Creating-Custom-Listeners
        listener = new APIHandler.IApiResponseListener() {
            @Override
            public void onMovieReady(Movie movie) {
                Log.d(LOG,"onMovieReady Enter");
            }
        };
        apiHandler= new APIHandler(this,listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG,"onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Set widgets
        recyclerView = findViewById(R.id.recyclerView);
        buttonAdd = findViewById(R.id.fabAdd);


        //Kode direkte kopieret fra hans demo
        Map<String,Object> item = new HashMap<>();
        item.put("text",new Date().toString());

        Task<DocumentReference> items = fireStore.collection("Items").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(LOG, "Added " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG, e.getMessage());
            }
        });

        //List af film til test af adapter //TODO: Slet når færdig
        List<MovieGsonObject> movies = new ArrayList<>();
        MovieGsonObject movie1 = new MovieGsonObject();
        movie1.setTitle("Joker");
        movie1.setYear("2019");
        MovieGsonObject movie2 = new MovieGsonObject();
        movie1.setTitle("FRIDA");
        movie1.setYear("22019");
        MovieGsonObject movie3 = new MovieGsonObject();
        movie1.setTitle("MAthias");
        movie1.setYear("201d9");
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
                apiHandler.addRequest("Joker");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemsListener = fireStore.collection("Items").addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots != null  && !queryDocumentSnapshots.getDocuments().isEmpty())
                        {
                            List<String> items = new ArrayList<>();
                            for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                            {
                                items.add(snapshot.getData().get("text").toString());
                            }
                            //adapter.setItems(items);

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
