package seedu.address.model.movie;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Movie's startDate in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidStartDate(String)}
 */
public class StartDate {


    public static final String MESSAGE_STARTDATE_CONSTRAINTS =
            "StartDate must be in this format: DD/MM/YYYY";
    //This regex does not validate dates such as leap years and such.
    public static final String STARTDATE_VALIDATION_REGEX =
            "^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$";
    public final String startDate;

    /**
     * Constructs a {@code StartDate}.
     *
     * @param startDate A valid startDate.
     */
    public StartDate(String startDate) {
        requireNonNull(startDate);
        checkArgument(isValidStartDate(startDate), MESSAGE_STARTDATE_CONSTRAINTS);
        this.startDate = startDate;
    }

    /**
     * Returns true if a given string is a valid movie startDate.
     */
    public static boolean isValidStartDate(String test) {
        return test.matches(STARTDATE_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return startDate;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof StartDate // instanceof handles nulls
                && this.startDate.equals(((StartDate) other).startDate)); // state check
    }

    @Override
    public int hashCode() {
        return startDate.hashCode();
    }

}
