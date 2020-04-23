package au585303.au590400.weekendwatchlist.adapters;

import android.content.Context;
import android.content.Intent;
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

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.activities.DetailsActivity;
import au585303.au590400.weekendwatchlist.models.MovieGsonObject;

//TODO: Skal det være et MovieGsonObject hele vejen igennem eller ikke? Kan vi tilføje personlige ting til det? Som ikke kommer fra API'et mener jeg

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    //Defining variables
    private OnItemClickListener onItemClickListener;
    private List<MovieGsonObject> movieList;
    private static final String LOG = "ListAdapter";

    public ListAdapter(List<MovieGsonObject> list, OnItemClickListener onItemClickListener) {
        movieList = list;
        this.onItemClickListener = onItemClickListener;
    }

    //Create viewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Define variables
        private TextView title;
        private TextView year;
        private TextView rating;
        private ImageView image;
        private OnItemClickListener onItemClickListener;

        private ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            //Set variables
            title = view.findViewById(R.id.tv_Title);
            year = view.findViewById(R.id.tv_Year);
            rating = view.findViewById(R.id.tv_Rating);
            image = view.findViewById(R.id.iv_Movie);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
            //Start activity with intent
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Set up viewholder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Set widgets
        MovieGsonObject movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        Picasso.get().load(movie.getPoster()).into(holder.image);

        //TODO: mangler at lave noget med den personlige rating

        Log.d(LOG, "item.imageURL");
        //Use Picasso to load image into imageview (see link above)
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
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
            title = view.findViewById(R.id.tvTitle);
            year = view.findViewById(R.id.tvYear);
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
