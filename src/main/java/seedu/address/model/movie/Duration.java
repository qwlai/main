package seedu.address.model.movie;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Movie's duration in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidDuration(String)}
 */
public class Duration {


    public static final String MESSAGE_DURATION_CONSTRAINTS =
            "Duration can only contain numbers";
    public static final String DURATION_VALIDATION_REGEX = "^[1-9]\\d*$";
    public final String duration;

    /**
     * Constructs a {@code Duration}.
     *
     * @param duration A valid duration.
     */
    public Duration(String duration) {
        requireNonNull(duration);
        checkArgument(isValidDuration(duration), MESSAGE_DURATION_CONSTRAINTS);
        this.duration = duration;
    }

    /**
     * Returns true if a given string is a valid movie duration.
     */
    public static boolean isValidDuration(String test) {
        return test.matches(DURATION_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return duration;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Duration // instanceof handles nulls
                && this.duration.equals(((Duration) other).duration)); // state check
    }

    @Override
    public int hashCode() {
        return duration.hashCode();
    }

}
