package au585303.au590400.weekendwatchlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Movie;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private ListenerRegistration itemsListener;
    private String LOG = "ListActivity";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG,"onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Set variable
        recyclerView = findViewById(R.id.recyclerView);



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
        adapter = new ListAdapter(movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
}
