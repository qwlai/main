package seedu.address.model.movie;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents a Movie in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Movie {

    private final MovieName movieName;
    private final Duration duration;
    private final Rating rating;
    private final StartDate startDate;

    public Movie(MovieName movieName, Duration duration, Rating rating, StartDate startDate) {
        requireAllNonNull(movieName, duration, rating, startDate);
        this.movieName = movieName;
        this.duration = duration;
        this.rating = rating;
        this.startDate = startDate;
    }

    public MovieName getName() {
        return movieName;
    }

    public Duration getDuration() {
        return duration;
    }

    public Rating getRating() {
        return rating;
    }

    public StartDate getStartDate() {
        return startDate;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Movie)) {
            return false;
        }

        Movie otherMovie = (Movie) other;
        return otherMovie.getName().equals(this.getName())
                && otherMovie.getDuration().equals(this.getDuration())
                && otherMovie.getRating().equals(this.getRating())
                && otherMovie.getStartDate().equals(this.getStartDate());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(movieName);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Duration: ")
                .append(getDuration())
                .append(" Rating: ")
                .append(getRating())
                .append(" StartDate: ")
                .append(getStartDate());
        return builder.toString();
    }
}
