package seedu.address.model.cinema;

/**
 * Represents a theater in cinema
 */
public class Theater {

    public static final String MESSAGE_THEATER_CONSTRAINTS =
            "Theater number should be positive.";

    /*
     * theater number must be positive
     */
    public static final String THEATER_VALIDATION_REGEX = "^[1-9]\\d*$";

    private int theaterNumber;

    public Theater() {
    }

    public Theater(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    /**
     * Returns true if a given string is a valid theater number.
     */
    public static boolean isValidTheater(int test) {
        return String.valueOf(test).matches(THEATER_VALIDATION_REGEX);
    }

    public int getTheaterNumber() {
        return theaterNumber;
    }

    public void setTheaterNumber(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    @Override
    public String toString() {
        return "Theater " + theaterNumber;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Theater // instanceof handles nulls
                && this.theaterNumber == ((Theater) other).theaterNumber); // state check
    }
}
