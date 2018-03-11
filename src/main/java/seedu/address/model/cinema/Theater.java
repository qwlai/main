package seedu.address.model.cinema;

/**
 * Represents a theater in a cinema
 */
public class Theater {

    private int theaterNumber;

    public Theater(int theaterNumber) {
        this.theaterNumber = theaterNumber;
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
}
