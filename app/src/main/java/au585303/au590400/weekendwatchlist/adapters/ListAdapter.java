package au585303.au590400.weekendwatchlist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au585303.au590400.weekendwatchlist.R;
import au585303.au590400.weekendwatchlist.models.Movie;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    // Defining variables
    private static final String LOG = "ListAdapter";
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Movie> movies;
    private List<Movie> fullListOfMovies;
    private List<Movie> moviesFilteredByGenre;
    private boolean sortedByDescending = false;

    public ListAdapter(List<Movie> movies, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        fullListOfMovies = new ArrayList<>(movies);
        moviesFilteredByGenre = new ArrayList<>(movies);
        notifyDataSetChanged();
    }

    // Inspired by this: https://howtodoinjava.com/sort/collections-sort/
    public void sortMoviesByRating() {
        List<Movie> sortedMovies = new ArrayList<>();
        if (!sortedByDescending) {
            Collections.sort(movies, Collections.reverseOrder());
            Collections.sort(fullListOfMovies, Collections.reverseOrder());
            sortedMovies.addAll(movies);
            sortedByDescending = true;
        } else {
            Collections.sort(movies);
            Collections.sort(fullListOfMovies);
            sortedMovies.addAll(movies);
            sortedByDescending = false;
        }
        movies.clear();
        movies.addAll(sortedMovies);
        notifyDataSetChanged();
    }

    public Filter getSearchFilter() {
        return searchFilter;
    }

    public Filter getGenreFilter() {
        return genreFilter;
    }

    private Filter genreFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Movie> filterList = new ArrayList<>();
            if (constraint.equals("All")) {
                filterList.addAll(fullListOfMovies);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Movie movie : fullListOfMovies) {
                    if (movie.getGenre().toLowerCase().contains(filterPattern)) {
                        filterList.add(movie);
                    }
                }
            }
            moviesFilteredByGenre = filterList;
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            movies.clear();
            movies.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Search filter implementation inspired by this video: https://youtu.be/sJ-Z9G0SDhc
    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Movie> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(moviesFilteredByGenre);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Movie movie : moviesFilteredByGenre) {
                    if (movie.getTitle().toLowerCase().contains(filterPattern)) {
                        filterList.add(movie);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            movies.clear();
            movies.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Create viewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // Define variables
        private TextView title;
        private TextView year;
        private TextView rating;
        private ImageView image;
        private OnItemClickListener onItemClickListener;
        private OnItemLongClickListener onItemLongClickListener;

        private ViewHolder(View view, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
            super(view);
            // Set variables
            title = view.findViewById(R.id.txtTitle);
            year = view.findViewById(R.id.txtYear);
            rating = view.findViewById(R.id.txtRating);
            image = view.findViewById(R.id.imgMovie);
            this.onItemClickListener = onItemClickListener;
            this.onItemLongClickListener = onItemLongClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onItemLongClickListener.onItemLongClick(getAdapterPosition());
            return false;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Set up viewholder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view, onItemClickListener, onItemLongClickListener);
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

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}

