package au585303.au590400.weekendwatchlist.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Movie implements Comparable<Movie>, Parcelable {
    private String title;
    private String year;
    private String genre;
    private String runtime;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String awards;
    private String poster;
    private String imdbRating;
    private String personalRating;
    private String personalNotes;

    //Constructor
    public Movie(String title, String year, String genre, String runtime, String director, String writer, String actors, String plot, String awards, String poster, String imdbRating) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.runtime = runtime;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.awards = awards;
        this.poster = poster;
        this.imdbRating = imdbRating;
    }

    //Empty constructor
    public Movie() {
    }


    protected Movie(Parcel in) {
        title = in.readString();
        year = in.readString();
        genre = in.readString();
        runtime = in.readString();
        director = in.readString();
        writer = in.readString();
        actors = in.readString();
        plot = in.readString();
        awards = in.readString();
        poster = in.readString();
        imdbRating = in.readString();
        personalRating = in.readString();
        personalNotes = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //Getter and Setter methods
    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(String personalRating) {
        this.personalRating = personalRating;
    }

    public String getPersonalNotes() {
        return personalNotes;
    }

    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }

    // Overriding this in order for if (!movies.contains(movie)) to work inside the snapshot listener in ListActivity.
    @Override
    public boolean equals(@Nullable Object obj) {
        Movie movie = (Movie) obj;
        return movie.getTitle().equals(title);
    }

    // Inspired by this: https://howtodoinjava.com/sort/collections-sort/
    @Override
    public int compareTo(Movie o) {
        return this.getImdbRating().compareTo(o.getImdbRating());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(genre);
        dest.writeString(runtime);
        dest.writeString(director);
        dest.writeString(writer);
        dest.writeString(actors);
        dest.writeString(plot);
        dest.writeString(awards);
        dest.writeString(poster);
        dest.writeString(imdbRating);
        dest.writeString(personalRating);
        dest.writeString(personalNotes);
    }
}
