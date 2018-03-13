package seedu.address.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysMovie;

import org.junit.Before;
import org.junit.Test;

import guitests.guihandles.MovieCardHandle;
import seedu.address.model.movie.Movie;
import seedu.address.testutil.MovieBuilder;

public class MovieCardTest extends GuiUnitTest {

    private Movie movie;
    private MovieCard movieCard;

    @Before
    public void setUp() throws Exception {
        movie = new MovieBuilder().build();
        movieCard = new MovieCard(movie, 1);
    }

    @Test
    public void display_checkDetails_displayedCorrectly() {
        uiPartRule.setUiPart(movieCard);
        assertCardDisplay(movieCard, movie, 1);
    }

    @Test
    public void equals_sameNameSameIndex_returnTrue() {
        MovieCard copy = new MovieCard(movie, 1);
        assertTrue(movieCard.equals(copy));
    }

    @Test
    public void equals_checkNull_returnFalse() {
        assertFalse(movieCard.equals(null));
    }

    @Test
    public void equals_sameMovieCard_returnTrue() {
        assertTrue(movieCard.equals(movieCard));
    }

    @Test
    public void equals_differentMovieSameIndex_returnFalse() {
        Movie differentMovie = new MovieBuilder().withName("differentName").build();
        assertFalse(movieCard.equals((new MovieCard(differentMovie, 1))));
    }

    @Test
    public void equals_sameMovieDifferentIndex_returnFalse() {
        assertFalse(movieCard.equals(new MovieCard(movie, 2)));
    }

    /**
     * Asserts that {@code movieCard} displays the details of {@code expectedMovie} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(MovieCard movieCard, Movie expectedMovie, int expectedId) {
        guiRobot.pauseForHuman();

        MovieCardHandle movieCardHandle = new MovieCardHandle(movieCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", movieCardHandle.getId());

        // verify movie details are displayed correctly
        assertCardDisplaysMovie(expectedMovie, movieCardHandle);
    }
}
