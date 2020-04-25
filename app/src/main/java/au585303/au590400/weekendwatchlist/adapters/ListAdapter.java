package au585303.au590400.weekendwatchlist.adapters;

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
import au585303.au590400.weekendwatchlist.models.Movie;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    // Defining variables
    private static final String LOG = "ListAdapter";
    private OnItemClickListener onItemClickListener;
    private List<Movie> movies;

    public ListAdapter(List<Movie> movies, OnItemClickListener onItemClickListener) {
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    // Create viewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Define variables
        private TextView title;
        private TextView year;
        private TextView rating;
        private ImageView image;
        private OnItemClickListener onItemClickListener;

        private ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            // Set variables
            title = view.findViewById(R.id.txtTitle);
            year = view.findViewById(R.id.txtYear);
            rating = view.findViewById(R.id.txtRating);
            image = view.findViewById(R.id.imgMovie);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Set up viewholder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set widgets
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        holder.rating.setText(movie.getImdbRating());
        String posterUrl = movie.getPoster();
        Picasso.get().load(movie.getPoster()).into(holder.image);
        //TODO: mangler at lave noget med den personlige rating
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

