package seedu.address.testutil;

import seedu.address.model.movie.Duration;
import seedu.address.model.movie.Movie;
import seedu.address.model.movie.MovieName;
import seedu.address.model.movie.Rating;
import seedu.address.model.movie.StartDate;

/**
 * A utility class to help with building Movie objects.
 */
public class MovieBuilder {

    public static final String DEFAULT_MOVIENAME = "The Incredibles";
    public static final String DEFAULT_DURATION = "90";
    public static final String DEFAULT_RATING = "PG";
    public static final String DEFAULT_STARTDATE = "13/03/2018";

    private MovieName movieName;
    private Duration duration;
    private Rating rating;
    private StartDate startDate;

    public MovieBuilder() {
        movieName = new MovieName(DEFAULT_MOVIENAME);
        duration = new Duration(DEFAULT_DURATION);
        rating = new Rating(DEFAULT_RATING);
        startDate = new StartDate(DEFAULT_STARTDATE);
    }

    /**
     * Initializes the MovieBuilder with the data of {@code movieToCopy}.
     */
    public MovieBuilder(Movie movieToCopy) {
        movieName = movieToCopy.getName();
        duration = movieToCopy.getDuration();
        rating = movieToCopy.getRating();
        startDate = movieToCopy.getStartDate();
    }

    /**
     * Sets the {@code MovieName} of the {@code Movie} that we are building.
     */
    public MovieBuilder withName(String movieName) {
        this.movieName = new MovieName(movieName);
        return this;
    }

    /**
     * Sets the {@code Duration} of the {@code Movie} that we are building.
     */
    public MovieBuilder withDuration(String duration) {
        this.duration = new Duration(duration);
        return this;
    }

    /**
     * Sets the {@code Rating} of the {@code Movie} that we are building.
     */
    public MovieBuilder withRating(String rating) {
        this.rating = new Rating(rating);
        return this;
    }

    /**
     * Sets the {@code StartDate} of the {@code Movie} that we are building.
     */
    public MovieBuilder withStartDate(String startDate) {
        this.startDate = new StartDate(startDate);
        return this;
    }

    public Movie build() {
        return new Movie(movieName, duration, rating, startDate);
    }

}
