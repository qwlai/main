package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.movie.Movie;
import seedu.address.model.movie.exceptions.DuplicateMovieException;

/**
 * A utility class containing a list of {@code Movie} objects to be used in tests.
 */
public class TypicalMovies {


    public static final String KEYWORD_MATCHING_GHOST = "Ghost"; // A keyword that matches GHOST

    private TypicalMovies() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical movies.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Movie movie : getTypicalMovies()) {
            try {
                ab.addMovie(movie);
            } catch (DuplicateMovieException e) {
                throw new AssertionError("not possible");
            }
        }
        return ab;
    }

    public static List<Movie> getTypicalMovies() {
        return new ArrayList<>(Arrays.asList());
    }
}
