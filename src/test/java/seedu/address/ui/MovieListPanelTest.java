package seedu.address.ui;

import static org.junit.Assert.assertEquals;
import static seedu.address.testutil.TypicalMovies.getTypicalMovies;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysMovie;

import org.junit.Before;
import org.junit.Test;

import guitests.guihandles.MovieCardHandle;
import guitests.guihandles.MovieListPanelHandle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.movie.Movie;

public class MovieListPanelTest extends GuiUnitTest {
    private static final ObservableList<Movie> TYPICAL_MOVIES =
            FXCollections.observableList(getTypicalMovies());

    private MovieListPanelHandle movieListPanelHandle;

    @Before
    public void setUp() {
        MovieListPanel movieListPanel = new MovieListPanel(TYPICAL_MOVIES);
        uiPartRule.setUiPart(movieListPanel);

        movieListPanelHandle = new MovieListPanelHandle(getChildNode(movieListPanel.getRoot(),
                MovieListPanelHandle.MOVIE_LIST_VIEW_ID));
    }

    @Test
    public void display_cardMatches_returnTrue() {
        for (int i = 0; i < TYPICAL_MOVIES.size(); i++) {
            movieListPanelHandle.navigateToCard(TYPICAL_MOVIES.get(i));
            Movie expectedMovie = TYPICAL_MOVIES.get(i);
            MovieCardHandle actualCard = movieListPanelHandle.getMovieCardHandle(i);

            assertCardDisplaysMovie(expectedMovie, actualCard);
            assertEquals(Integer.toString(i + 1) + ". ", actualCard.getId());
        }
    }
}
