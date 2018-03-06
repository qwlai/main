package seedu.address.model.theater;

/**
 * Represents a theater in the cinema
 */
public class Theater {

    private final int theaterNumber;
    private String ratings;

    public Theater(int theaterNumber) {
        this.theaterNumber = theaterNumber;
        this.ratings = "";
    }

    public int getTheaterNumber() {
        return theaterNumber;
    }

    public String getRatings() {
        return ratings;
    }

    public String setRatings(String ratings) {
        this.ratings = ratings;
    }
}
