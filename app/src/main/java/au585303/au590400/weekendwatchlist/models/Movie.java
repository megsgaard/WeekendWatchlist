package au585303.au590400.weekendwatchlist.models;

public class Movie implements Comparable<Movie> {
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

    // Inspired by this: https://howtodoinjava.com/sort/collections-sort/
    @Override
    public int compareTo(Movie o) {
        return this.getImdbRating().compareTo(o.getImdbRating());
    }
}
