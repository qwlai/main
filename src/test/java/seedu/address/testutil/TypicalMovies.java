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

    public static final Movie BLACK_PANTHER = new MovieBuilder().withName("Black Panther")
            .withDuration("134").withRating("PG").withStartDate("14/02/2018").build();

    public static final Movie THE_AVENGERS = new MovieBuilder().withName("The Avengers")
            .withDuration("143").withRating("PG13").withStartDate("01/05/2012").build();

    public static final Movie THOR_RAGNAROK = new MovieBuilder().withName("Thor Ragnarok")
            .withDuration("130").withRating("PG13").withStartDate("10/10/2017").build();


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
        return new ArrayList<>(Arrays.asList(BLACK_PANTHER, THE_AVENGERS, THOR_RAGNAROK));
    }
}
