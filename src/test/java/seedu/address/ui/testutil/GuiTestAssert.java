package seedu.address.ui.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import guitests.guihandles.CinemaCardHandle;
import guitests.guihandles.CinemaListPanelHandle;
import guitests.guihandles.MovieCardHandle;
import guitests.guihandles.ResultDisplayHandle;
import seedu.address.model.cinema.Cinema;
import seedu.address.model.movie.Movie;

/**
 * A set of assertion methods useful for writing GUI tests.
 */
public class GuiTestAssert {
    private static final String DEFAULT_LABEL_STYLE = "label";
    /**
     * Asserts that {@code actualCard} displays the same values as {@code expectedCard}.
     */
    public static void assertCardEquals(CinemaCardHandle expectedCard, CinemaCardHandle actualCard) {
        assertEquals(expectedCard.getId(), actualCard.getId());
        assertEquals(expectedCard.getAddress(), actualCard.getAddress());
        assertEquals(expectedCard.getEmail(), actualCard.getEmail());
        assertEquals(expectedCard.getName(), actualCard.getName());
        assertEquals(expectedCard.getPhone(), actualCard.getPhone());
        assertEquals(expectedCard.getTags(), actualCard.getTags());

        expectedCard.getTags().forEach(tag ->
            assertEquals(expectedCard.getTagStyleClasses(tag), actualCard.getTagStyleClasses(tag)));
    }

    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedCinema}.
     */
    public static void assertCardDisplaysCinema(Cinema expectedCinema, CinemaCardHandle actualCard) {
        assertEquals(expectedCinema.getName().fullName, actualCard.getName());
        assertEquals(expectedCinema.getPhone().value, actualCard.getPhone());
        assertEquals(expectedCinema.getEmail().value, actualCard.getEmail());
        assertEquals(expectedCinema.getAddress().value, actualCard.getAddress());
        assertTagsEqual(expectedCinema, actualCard);
    }

    private static String getTagColorStyle(String tagName) {
        switch (tagName) {
        case "classmates":
        case "owesMoney":
            return "red";

        case "colleagues":
        case "neighbours":
            return "orange";

        case "friends":
            return "grey";

        case "friend":
        case "family":
            return "yellow";

        case "husband":
            return "brown";

        default:
            fail(tagName + " has no color assignment.");
            return "";
        }
    }

    /**
     * Asserts that tags that are in {@code actualCard} matches all tags in {@code expectedPerson}
     * with corresponding color
     * @param expectedCinema
     * @param actualCard
     */
    private static void assertTagsEqual(Cinema expectedCinema, CinemaCardHandle actualCard) {
        List<String> expectedTags = expectedCinema.getTags()
                .stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.toList());
        assertEquals(expectedTags, actualCard.getTags());
        expectedTags.forEach(tag -> assertEquals(Arrays.asList(
                DEFAULT_LABEL_STYLE, getTagColorStyle(tag)), actualCard.getTagStyleClasses(tag)));
    }

    /**
     * Asserts that the list in {@code cinemaListPanelHandle} displays the details of {@code cinemas} correctly and
     * in the correct order.
     */
    public static void assertListMatching(CinemaListPanelHandle cinemaListPanelHandle, Cinema... cinemas) {
        for (int i = 0; i < cinemas.length; i++) {
            assertCardDisplaysCinema(cinemas[i], cinemaListPanelHandle.getCinemaCardHandle(i));
        }
    }

    /**
     * Asserts that the list in {@code cinemaListPanelHandle} displays the details of {@code cinemas} correctly and
     * in the correct order.
     */
    public static void assertListMatching(CinemaListPanelHandle cinemaListPanelHandle, List<Cinema> cinemas) {
        assertListMatching(cinemaListPanelHandle, cinemas.toArray(new Cinema[0]));
    }

    /**
     * Asserts the size of the list in {@code cinemaListPanelHandle} equals to {@code size}.
     */
    public static void assertListSize(CinemaListPanelHandle cinemaListPanelHandle, int size) {
        int numberOfPeople = cinemaListPanelHandle.getListSize();
        assertEquals(size, numberOfPeople);
    }

    /**
     * Asserts the message shown in {@code resultDisplayHandle} equals to {@code expected}.
     */
    public static void assertResultMessage(ResultDisplayHandle resultDisplayHandle, String expected) {
        assertEquals(expected, resultDisplayHandle.getText());
    }

    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedMovie}.
     */
    public static void assertCardDisplaysMovie(Movie expectedMovie, MovieCardHandle actualCard) {
        assertEquals(expectedMovie.getName().toString(), actualCard.getName());
        assertEquals(expectedMovie.getDuration().toString(), actualCard.getDuration());
        assertEquals(expectedMovie.getRating().toString(), actualCard.getRating());
        assertEquals(expectedMovie.getStartDate().toString(), actualCard.getStartDate());
    }
}
