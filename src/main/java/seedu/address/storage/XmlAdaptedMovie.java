package seedu.address.storage;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;

import seedu.address.model.movie.Duration;
import seedu.address.model.movie.Movie;
import seedu.address.model.movie.MovieName;
import seedu.address.model.movie.Rating;
import seedu.address.model.movie.StartDate;

/**
 * JAXB-friendly version of the Movie.
 */
public class XmlAdaptedMovie {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Movie's %s field is missing!";

    @XmlElement(required = true)
    private String movieName;
    @XmlElement(required = true)
    private String duration;
    @XmlElement(required = true)
    private String rating;
    @XmlElement(required = true)
    private String startDate;

    /**
     * Constructs an XmlAdaptedMovie.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedMovie() {}

    /**
     * Constructs an {@code XmlAdaptedCinema} with the given movie details.
     */
    public XmlAdaptedMovie(String movieName, String duration, String rating, String startDate) {
        this.movieName = movieName;
        this.duration = duration;
        this.rating = rating;
        this.startDate = startDate;
    }

    /**
     * Converts a given Movie into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedMovie
     */
    public XmlAdaptedMovie(Movie source) {
        movieName = source.getName().movieName;
        duration = source.getDuration().duration;
        rating = source.getRating().rating;
        startDate = source.getStartDate().startDate;
    }

    /**
     * Converts this jaxb-friendly adapted movie object into the model's Movie object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted movie
     */
    public Movie toModelType() throws IllegalValueException {

        if (this.movieName == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    MovieName.class.getSimpleName()));
        }
        if (!MovieName.isValidName(this.movieName)) {
            throw new IllegalValueException(MovieName.MESSAGE_MOVIENAME_CONSTRAINTS);
        }
        final MovieName movieName = new MovieName(this.movieName);

        if (this.duration == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Duration.class.getSimpleName()));
        }
        if (!Duration.isValidDuration(this.duration)) {
            throw new IllegalValueException(Duration.MESSAGE_DURATION_CONSTRAINTS);
        }
        final Duration duration = new Duration(this.duration);

        if (this.rating == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Rating.class.getSimpleName()));
        }
        if (!Rating.isValidRating(this.rating)) {
            throw new IllegalValueException(Rating.MESSAGE_RATING_CONSTRAINTS);
        }
        final Rating rating = new Rating(this.rating);

        if (this.startDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    StartDate.class.getSimpleName()));
        }
        if (!StartDate.isValidStartDate(this.startDate)) {
            throw new IllegalValueException(StartDate.MESSAGE_STARTDATE_CONSTRAINTS);
        }
        final StartDate startDate = new StartDate(this.startDate);

        return new Movie(movieName, duration, rating, startDate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedMovie)) {
            return false;
        }

        XmlAdaptedMovie otherMovie = (XmlAdaptedMovie) other;
        return Objects.equals(movieName, otherMovie.movieName)
                && Objects.equals(duration, otherMovie.duration)
                && Objects.equals(rating, otherMovie.rating)
                && Objects.equals(startDate, otherMovie.startDate);
    }
}
