package seedu.address.model.movie;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Movie's duration in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidRating(String)}
 */
public class Rating {


    public static final String MESSAGE_RATING_CONSTRAINTS =
            "Rating can only contain alphanumeric characters from 2 to 4 characters";
    public static final String RATING_VALIDATION_REGEX = "^\\w{1,4}$";
    public final String rating;

    /**
     * Constructs a {@code Rating}.
     *
     * @param rating A valid rating.
     */
    public Rating(String rating) {
        requireNonNull(rating);
        checkArgument(isValidRating(rating), MESSAGE_RATING_CONSTRAINTS);
        this.rating = rating;
    }

    /**
     * Returns true if a given string is a valid movie rating.
     */
    public static boolean isValidRating(String test) {
        return test.matches(RATING_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return rating;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Rating // instanceof handles nulls
                && this.rating.equals(((Rating) other).rating)); // state check
    }

    @Override
    public int hashCode() {
        return rating.hashCode();
    }

}
