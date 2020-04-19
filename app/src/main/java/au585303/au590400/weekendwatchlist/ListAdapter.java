package au585303.au590400.weekendwatchlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

//TODO: Skal det være et MovieGsonObject hele vejen igennem eller ikke? Kan vi tilføje personlige ting til det? Som ikke kommer fra API'et mener jeg

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    //Defining variables
    private List<MovieGsonObject> movieList;
    private Context context;
    private static final String LOG = "ListAdapter";
    public ListAdapter(List<MovieGsonObject> list){movieList = list; }
    //public static final int REQUEST_CODE_DETAILSACTIVITY = 111;

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Set up viewholder
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {
        //Set widgets
        MovieGsonObject movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());

        //TODO: mangler at lave noget med den personlige rating

        Log.d(LOG,"item.imageURL");
        //Use Picasso to load image into imageview (see link above)
        Picasso.get().load(movie.getPoster()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    //Create viewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Define variables
        public TextView title;
        public TextView year;
        public TextView rating;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            //Set variables
            title = view.findViewById(R.id.tv_Title);
            year = view.findViewById(R.id.tv_Year);
            rating = view.findViewById(R.id.tv_Rating);
            image = view.findViewById(R.id.iv_Movie);
        }

        @Override
        public void onClick(View view) {
            //Start activity with intent
            String movieClick = title.getText().toString();
            Intent intent = new Intent(view.getContext(),DetailsActivity.class);
            intent.putExtra(view.getContext().getResources().getString(R.string.IntentAdapterDetails),movieClick);
            view.getContext().startActivity(intent);

        }
    }
}
